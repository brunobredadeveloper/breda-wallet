package dev.breda.wallet

import dev.breda.wallet.domain.*
import java.util.UUID

class TransferMoney(
    private val repository: WalletRepository,
    private val newKey: () -> String = { UUID.randomUUID().toString() },
) {
    suspend operator fun invoke(recipient: String, rawAmount: String): TransferResult {
        val cents = AmountInput.cents(rawAmount) ?: return TransferResult.InvalidRequest
        if (recipient.isBlank()) return TransferResult.InvalidRequest
        return repository.transfer(Transfer(newKey(), recipient.trim(), Money(cents)))
    }
}

enum class TransferPhase {
    IDLE,
    PROCESSING,
    SUCCEEDED,
    FAILED,
}
