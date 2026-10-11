package dev.breda.wallet.data

import dev.breda.wallet.domain.*
import kotlin.test.*
import kotlinx.coroutines.test.runTest

class WalletPersistenceTest {
    private class MemoryStorage : WalletStorage {
        var snapshot: WalletSnapshot? = null
        var fail = false

        override fun load() = snapshot

        override fun save(snapshot: WalletSnapshot) {
            check(!fail)
            this.snapshot = snapshot
        }
    }

    @Test
    fun restartRestoresBalanceAndIdempotency() = runTest {
        val disk = MemoryStorage()
        val command = Transfer("stable", "Ana", Money(500))
        DemoWalletRepository(Money(1000), disk).transfer(command)
        val restarted = DemoWalletRepository(storage = disk)
        assertEquals(TransferResult.Success, restarted.transfer(command))
        assertEquals(500L, restarted.wallet.value.balance.cents)
        assertEquals(1, restarted.wallet.value.entries.size)
        assertEquals(
            TransferResult.IdempotencyConflict,
            restarted.transfer(command.copy(recipient = "Pedro")),
        )
    }

    @Test
    fun failedPersistenceDoesNotPublishDebit() = runTest {
        val disk = MemoryStorage()
        disk.fail = true
        val repo = DemoWalletRepository(Money(1000), disk)
        assertFailsWith<IllegalStateException> { repo.transfer(Transfer("a", "Ana", Money(500))) }
        assertEquals(1000L, repo.wallet.value.balance.cents)
        assertTrue(repo.wallet.value.entries.isEmpty())
        disk.fail = false
        assertEquals(TransferResult.Success, repo.transfer(Transfer("a", "Ana", Money(500))))
    }

    @Test
    fun deniedRequestStaysDeniedAcrossRestart() = runTest {
        val disk = MemoryStorage()
        val cmd = Transfer("a", "Ana", Money(1001))
        DemoWalletRepository(Money(1000), disk).transfer(cmd)
        assertEquals(
            TransferResult.InsufficientFunds,
            DemoWalletRepository(storage = disk).transfer(cmd),
        )
    }
}
