package dev.breda.wallet
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContent { MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFFB5F36C))) { Surface(Modifier.fillMaxSize()) { WalletScreen() } } }
 }
}
private fun currency(cents: Long) = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(BigDecimal.valueOf(cents, 2))
@Composable
fun WalletScreen(vm: WalletViewModel = viewModel()) {
 val wallet by vm.wallet.collectAsStateWithLifecycle()
 val message by vm.message.collectAsStateWithLifecycle()
 var recipient by rememberSaveable { mutableStateOf("") }
 var amount by rememberSaveable { mutableStateOf("") }
 LazyColumn(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
  item { Text("BREDA WALLET", style = MaterialTheme.typography.labelLarge); Text("Seu dinheiro, com clareza.", style = MaterialTheme.typography.headlineSmall) }
  item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(24.dp)) { Text("Saldo disponível · demonstração"); Text(currency(wallet.balance.cents), style = MaterialTheme.typography.headlineLarge) } } }
  item { Text("Transferir", style = MaterialTheme.typography.titleLarge) }
  item { OutlinedTextField(recipient, { recipient = it }, label = { Text("Destinatário fictício") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
  item { OutlinedTextField(amount, { amount = it }, label = { Text("Valor em reais · ex.: 25,50") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
  item { Button(onClick = { vm.transfer(recipient, amount); amount = "" }, modifier = Modifier.fillMaxWidth(), enabled = recipient.isNotBlank() && amount.isNotBlank()) { Text("Simular transferência") } }
  item { if (message.isNotEmpty()) Text(message); Text("Extrato", style = MaterialTheme.typography.titleLarge) }
  if (wallet.entries.isEmpty()) item { Text("Nenhuma transferência nesta sessão.") }
  items(wallet.entries, key = { it.id }) { entry -> Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text(entry.description); Text("− ${currency(entry.debit.cents)}") } } }
  item { Text("Projeto de portfólio. Dados em memória, reiniciados ao encerrar o processo.", style = MaterialTheme.typography.bodySmall) }
 }
}
