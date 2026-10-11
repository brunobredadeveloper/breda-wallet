package dev.breda.wallet.data

import dev.breda.wallet.domain.*
import kotlin.test.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest

class DemoWalletRepositoryTest {
    @Test
    fun duplicateRequestDebitsOnlyOnce() = runTest {
        val repo = DemoWalletRepository(Money(10000))
        val command = Transfer("same", "Ana", Money(2000))
        assertEquals(TransferResult.Success, repo.transfer(command))
        assertEquals(TransferResult.Success, repo.transfer(command))
        assertEquals(8000L, repo.wallet.value.balance.cents)
        assertEquals(1, repo.wallet.value.entries.size)
    }

    @Test
    fun concurrentRequestsCannotOverdraw() = runTest {
        val repo = DemoWalletRepository(Money(10000))
        val results =
            (1..20).map { async { repo.transfer(Transfer("$it", "Ana", Money(1000))) } }.awaitAll()
        assertEquals(10, results.count { it == TransferResult.Success })
        assertEquals(0L, repo.wallet.value.balance.cents)
    }

    @Test
    fun requestIdCannotBeReusedForDifferentPayload() = runTest {
        val repo = DemoWalletRepository()
        repo.transfer(Transfer("same", "Ana", Money(100)))
        assertEquals(
            TransferResult.IdempotencyConflict,
            repo.transfer(Transfer("same", "Ana", Money(200))),
        )
    }

    @Test
    fun rejectedTransferDoesNotChangeWallet() = runTest {
        val repo = DemoWalletRepository(Money(100))
        val before = repo.wallet.value
        assertEquals(
            TransferResult.InsufficientFunds,
            repo.transfer(Transfer("a", "Ana", Money(101))),
        )
        assertEquals(TransferResult.InvalidRequest, repo.transfer(Transfer("b", "", Money(1))))
        assertEquals(before, repo.wallet.value)
    }
}
