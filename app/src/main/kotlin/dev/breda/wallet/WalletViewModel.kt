package dev.breda.wallet
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.breda.wallet.data.DemoWalletRepository
import dev.breda.wallet.domain.*
import java.math.BigDecimal
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalletViewModel(app:Application) : AndroidViewModel(app) {
 private val namespace=app.getSharedPreferences("portfolio-account",0).getString("namespace",sessionNamespace("local-demo"))!!
 private val repository = DemoWalletRepository(storage=LocalWalletStorage(app.getSharedPreferences("wallet-$namespace",0)))
 val wallet = repository.wallet
 private val messageState = MutableStateFlow("")
 val message = messageState.asStateFlow()
 private val busyState=MutableStateFlow(false)
 val busy=busyState.asStateFlow()
 fun transfer(recipient: String, rawAmount: String) {
  if(busyState.value)return
  val cents = runCatching { BigDecimal(rawAmount.trim().replace(',', '.')).movePointRight(2).longValueExact() }.getOrNull()
  if (cents == null || cents <= 0 || recipient.isBlank()) { messageState.value = "Informe destinatário e valor válido com até 2 casas decimais."; return }
  busyState.value=true;messageState.value="Processando transferência fictícia…"
  viewModelScope.launch {
   try {
   kotlinx.coroutines.delay(450) // simulated gateway latency for the confirmation flow
   messageState.value = when(repository.transfer(Transfer(UUID.randomUUID().toString(), recipient.trim(), Money(cents)))) {
    TransferResult.Success -> "Transferência simulada concluída."
    TransferResult.InsufficientFunds -> "Saldo insuficiente."
    else -> "Não foi possível realizar a transferência."
   }
   } catch(e:kotlinx.coroutines.CancellationException){throw e} catch(_:Exception){messageState.value="Não foi possível salvar a transferência. Nenhum débito foi confirmado."} finally {busyState.value=false}
  }
 }
}
