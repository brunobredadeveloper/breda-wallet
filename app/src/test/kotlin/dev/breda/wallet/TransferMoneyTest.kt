package dev.breda.wallet

import dev.breda.wallet.data.DemoWalletRepository
import dev.breda.wallet.domain.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class TransferMoneyTest {
    @Test
    fun invalidInputNeverMutatesLedgerAndValidTransferUsesInjectedKey() = runTest {
        val repository = DemoWalletRepository(Money(1000))
        val transfer = TransferMoney(repository) { "fixed-key" }
        assertEquals(TransferResult.InvalidRequest, transfer("Bruno", "1.001"))
        assertEquals(1000L, repository.wallet.value.balance.cents)
        assertEquals(TransferResult.Success, transfer(" Bruno ", "2,50"))
        assertEquals(750L, repository.wallet.value.balance.cents)
        assertEquals("fixed-key", repository.wallet.value.entries.single().id)
        assertEquals(TransferResult.Success, transfer("Bruno", "2.50"))
        assertEquals(750L, repository.wallet.value.balance.cents)
    }
}
