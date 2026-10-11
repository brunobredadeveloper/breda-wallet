package dev.breda.wallet.data

import dev.breda.wallet.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Session-only simulator. No real payment rails or persistence. */
class DemoWalletRepository(initialBalance: Money = Money(250000)) : WalletRepository {
 private val state = MutableStateFlow(Wallet(initialBalance))
 override val wallet = state.asStateFlow()
 private val mutex = Mutex()
 private val processed = mutableMapOf<String, Pair<Transfer, TransferResult>>()
 override suspend fun transfer(command: Transfer): TransferResult = mutex.withLock {
  processed[command.requestId]?.let { (original, result) ->
   return@withLock if (original == command) result else TransferResult.IdempotencyConflict
  }
  val result = when {
   command.requestId.isBlank() || command.recipient.isBlank() || command.amount.cents == 0L -> TransferResult.InvalidRequest
   command.amount.cents > state.value.balance.cents -> TransferResult.InsufficientFunds
   else -> {
    val current = state.value
    state.value = Wallet(Money(current.balance.cents - command.amount.cents),
     listOf(Entry(command.requestId, "Transferência para ${command.recipient}", command.amount)) + current.entries)
    TransferResult.Success
   }
  }
  if (command.requestId.isNotBlank()) processed[command.requestId] = command to result
  result
 }
}
