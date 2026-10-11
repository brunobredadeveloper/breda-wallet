package dev.breda.wallet.data

import dev.breda.wallet.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class ProcessedTransfer(val command: Transfer, val result: TransferResult)

data class WalletSnapshot(val wallet: Wallet, val processed: List<ProcessedTransfer> = emptyList())

/** Atomic snapshot boundary: implementations must either store all of it or throw. */
interface WalletStorage {
    fun load(): WalletSnapshot?

    fun save(snapshot: WalletSnapshot)
}

/** Fictitious ledger. Persist before publishing a successful debit. */
class DemoWalletRepository(
    initialBalance: Money = Money(250000),
    private val storage: WalletStorage? = null,
) : WalletRepository {
    private val restored = storage?.load() ?: WalletSnapshot(Wallet(initialBalance))
    private val state = MutableStateFlow(restored.wallet)
    override val wallet = state.asStateFlow()
    private val mutex = Mutex()
    private val processed = restored.processed.associateBy { it.command.requestId }.toMutableMap()

    override suspend fun transfer(command: Transfer): TransferResult =
        mutex.withLock {
            processed[command.requestId]?.let {
                return@withLock if (it.command == command) it.result
                else TransferResult.IdempotencyConflict
            }
            if (processed.size >= 1000) return@withLock TransferResult.SessionCapacityReached
            val current = state.value
            val result =
                when {
                    command.requestId.isBlank() ||
                        command.recipient.isBlank() ||
                        command.amount.cents == 0L -> TransferResult.InvalidRequest
                    command.amount.cents > current.balance.cents -> TransferResult.InsufficientFunds
                    else -> TransferResult.Success
                }
            val next =
                if (result == TransferResult.Success)
                    Wallet(
                        Money(current.balance.cents - command.amount.cents),
                        listOf(
                            Entry(
                                command.requestId,
                                "Transferência para ${command.recipient}",
                                command.amount,
                            )
                        ) + current.entries,
                    )
                else current
            val records = processed.toMutableMap()
            if (command.requestId.isNotBlank())
                records[command.requestId] = ProcessedTransfer(command, result)
            storage?.save(WalletSnapshot(next, records.values.toList()))
            processed.clear()
            processed.putAll(records)
            state.value = next
            result
        }
}
