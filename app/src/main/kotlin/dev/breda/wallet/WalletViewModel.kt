package dev.breda.wallet

import androidx.lifecycle.viewModelScope
import dev.breda.wallet.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalletViewModel(
    private val repository: WalletRepository,
    private val transferMoney: TransferMoney,
) : androidx.lifecycle.ViewModel() {
    private val phaseState = MutableStateFlow(TransferPhase.IDLE)
    val phase = phaseState.asStateFlow()
    val wallet = repository.wallet
    private val messageState = MutableStateFlow("")
    val message = messageState.asStateFlow()
    private val busyState = MutableStateFlow(false)
    val busy = busyState.asStateFlow()

    fun transfer(recipient: String, rawAmount: String) {
        if (busyState.value) return
        val cents = AmountInput.cents(rawAmount)
        if (cents == null || cents <= 0 || recipient.isBlank()) {
            phaseState.value = TransferPhase.FAILED
            messageState.value = "Informe destinatário e valor válido com até 2 casas decimais."
            return
        }
        phaseState.value = TransferPhase.PROCESSING
        busyState.value = true
        messageState.value = "Processando transferência fictícia…"
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(
                    2200
                ) // simulated gateway latency for the confirmation flow
                val result = transferMoney(recipient, rawAmount)
                phaseState.value =
                    if (result == TransferResult.Success) TransferPhase.SUCCEEDED
                    else TransferPhase.FAILED
                messageState.value =
                    when (result) {
                        TransferResult.Success -> "Transferência simulada concluída."
                        TransferResult.InsufficientFunds -> "Saldo insuficiente."
                        else -> "Não foi possível realizar a transferência."
                    }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (_: Exception) {
                phaseState.value = TransferPhase.FAILED
                messageState.value =
                    "Não foi possível salvar a transferência. Nenhum débito foi confirmado."
            } finally {
                busyState.value = false
            }
        }
    }
}
