package dev.breda.wallet.domain

import kotlinx.coroutines.flow.StateFlow

/** BRL minor units. Never represent money using floating point. */
@JvmInline
value class Money(val cents: Long) { init { require(cents >= 0) } }
data class Transfer(val requestId: String, val recipient: String, val amount: Money)
data class Entry(val id: String, val description: String, val debit: Money)
data class Wallet(val balance: Money, val entries: List<Entry> = emptyList())
sealed interface TransferResult {
 data object Success : TransferResult
 data object InsufficientFunds : TransferResult
 data object InvalidRequest : TransferResult
 data object SessionCapacityReached : TransferResult
 data object IdempotencyConflict : TransferResult
}
interface WalletRepository {
 val wallet: StateFlow<Wallet>
 suspend fun transfer(command: Transfer): TransferResult
}
