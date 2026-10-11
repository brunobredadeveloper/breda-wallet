package dev.breda.wallet
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.breda.wallet.data.DemoWalletRepository
import dev.breda.wallet.domain.*
import java.math.BigDecimal
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalletViewModel : ViewModel() {
 private val repository = DemoWalletRepository()
 val wallet = repository.wallet
 private val messageState = MutableStateFlow("")
 val message = messageState.asStateFlow()
 fun transfer(recipient: String, rawAmount: String) {
  val cents = runCatching { BigDecimal(rawAmount.trim().replace(',', '.')).movePointRight(2).longValueExact() }.getOrNull()
  if (cents == null || cents <= 0 || recipient.isBlank()) { messageState.value = "Informe destinatário e valor válido com até 2 casas decimais."; return }
  viewModelScope.launch {
   messageState.value = when(repository.transfer(Transfer(UUID.randomUUID().toString(), recipient.trim(), Money(cents)))) {
    TransferResult.Success -> "Transferência simulada concluída."
    TransferResult.InsufficientFunds -> "Saldo insuficiente."
    else -> "Não foi possível realizar a transferência."
   }
  }
 }
}
