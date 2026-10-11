package dev.breda.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
private fun money(cents:Long)=NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(BigDecimal.valueOf(cents,2))

@Composable fun ProductWorkspace(account:AccountSession) {
 val vm:WalletViewModel=viewModel(key="wallet-${account.session}")
 val busy by vm.busy.collectAsStateWithLifecycle()
 val wallet by vm.wallet.collectAsStateWithLifecycle();val message by vm.message.collectAsStateWithLifecycle()
 var recipient by rememberSaveable {mutableStateOf("")};var amount by rememberSaveable {mutableStateOf("")};var selected by rememberSaveable {mutableStateOf("")};var query by rememberSaveable {mutableStateOf("")};var confirmed by rememberSaveable {mutableStateOf(false)}
 Workspace(account,listOf("Início","Extrato","Planejar","Conta")) {route,go->
  when(route) {
   "home"->Page("Olá, ${account.name.substringBefore(' ')}") {
    WalletCard(wallet.balance.cents)
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){Metric("Transferido",money(wallet.entries.sumOf {it.debit.cents}),Modifier.weight(1f));Metric("Operações",wallet.entries.size.toString(),Modifier.weight(1f))}
    ActionTile("Enviar dinheiro","Destinatário → valor → confirmação",Icons.Default.Send){confirmed=false;go("operation")}
    ActionTile("Seu planejamento","Defina um objetivo e acompanhe o saldo",Icons.Default.Savings){go("tools")}
    Text("Últimas movimentações",style=MaterialTheme.typography.titleLarge)
    if(wallet.entries.isEmpty())Text("Seu extrato começa na primeira transferência.")
    wallet.entries.take(3).forEach {entry->ActionTile(entry.description,"− ${money(entry.debit.cents)}",Icons.Default.ReceiptLong){selected=entry.id;go("details")}}
   }
   "operation"->Page(if(confirmed)"Confira antes de enviar" else "Nova transferência",{go("home")}) {
    if(!confirmed){OutlinedTextField(recipient,{recipient=it},label={Text("Destinatário fictício")},modifier=Modifier.fillMaxWidth());OutlinedTextField(amount,{amount=it},label={Text("Valor em reais")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),modifier=Modifier.fillMaxWidth());Text("Disponível: ${money(wallet.balance.cents)}");Button(onClick={confirmed=true},enabled=recipient.isNotBlank()&&amount.isNotBlank(),modifier=Modifier.fillMaxWidth()){Text("Revisar transferência")}}
    else {Hero("REVISÃO",recipient,"Valor informado: R$ $amount");Text("Operação fictícia. Confira o valor e destinatário antes de confirmar.");Button(onClick={vm.transfer(recipient,amount);go("result")},modifier=Modifier.fillMaxWidth()){Text("Confirmar envio")};OutlinedButton(onClick={confirmed=false}){Text("Editar dados")}}
   }
   "result"->{val success=message.contains("concluída");ResultExperience(if(busy)"loading" else if(success)"success" else "error",if(busy)"Enviando sua transferência" else if(success)"Transferência concluída!" else "Não foi possível transferir",if(busy)"Estamos preparando o lançamento no seu extrato." else message,amount=if(success||busy)"R$ $amount" else "",reference=if(success)wallet.entries.firstOrNull()?.id.orEmpty() else "",primaryLabel=if(success)"Ver comprovante" else "Revisar transferência",onPrimary={if(success){selected=wallet.entries.firstOrNull()?.id.orEmpty();go("details")}else{confirmed=false;go("operation")}},secondaryLabel="Voltar ao início",onSecondary={go("home")})}
   "activity"->Page("Extrato") {OutlinedTextField(query,{query=it},label={Text("Buscar destinatário")},modifier=Modifier.fillMaxWidth());val entries=wallet.entries.filter {it.description.contains(query,true)};Text("${entries.size} movimentações");entries.forEach {entry->ActionTile(entry.description,"− ${money(entry.debit.cents)}",Icons.Default.ReceiptLong){selected=entry.id;go("details")}};if(entries.isEmpty())Text("Nenhuma movimentação encontrada. Envie uma transferência ou ajuste a busca.")}
   "details"->Page("Comprovante",{go("activity")}) {val entry=wallet.entries.find {it.id==selected};if(entry!=null){StatusPanel("success",money(entry.debit.cents),entry.description);Text("Identificador: ${entry.id}");Text("Comprovante fictício · Breda Wallet")}else {Text("Contrato da carteira",style=MaterialTheme.typography.titleLarge);Text("Valores em centavos, mutex para serializar transferências e request ID para impedir débitos repetidos. A biblioteca de domínio não conhece Firebase nem Compose.")}}
   "tools"->Page("Planejamento") {var goal by rememberSaveable {mutableStateOf("1000,00")};Hero("OBJETIVO","Sua próxima conquista","Planeje usando o saldo da demonstração.");OutlinedTextField(goal,{goal=it},label={Text("Meta em reais")},modifier=Modifier.fillMaxWidth());val cents=runCatching {BigDecimal(goal.replace(',','.')).movePointRight(2).longValueExact()}.getOrDefault(0);if(cents>0){val ratio=(wallet.balance.cents.toFloat()/cents).coerceIn(0f,1f);LinearProgressIndicator(progress={ratio},modifier=Modifier.fillMaxWidth());Text("${(ratio*100).toInt()}% do objetivo · ${money(cents)}")}else Text("Informe uma meta positiva.");ActionTile("Entenda a arquitetura","Dinheiro em centavos, mutex e idempotência",Icons.Default.Code){go("details");selected=""};Text("Saldo, extrato e chaves de idempotência persistem no aparelho, separados por conta.")}
  }
 }
}

@Composable private fun WalletCard(cents:Long){
 var revealed by rememberSaveable {mutableStateOf(true)}
 Card(colors=CardDefaults.cardColors(containerColor=Brand,contentColor=androidx.compose.ui.graphics.Color.White),modifier=Modifier.fillMaxWidth()){
  Column(Modifier.padding(24.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){
   Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("BREDA / WALLET",style=MaterialTheme.typography.labelLarge);IconButton(onClick={revealed=!revealed}){Icon(if(revealed)Icons.Default.Visibility else Icons.Default.VisibilityOff,"Mostrar ou ocultar saldo")}}
   Text(if(revealed)money(cents) else "••••••",style=MaterialTheme.typography.headlineLarge,fontWeight=FontWeight.Bold)
   Text("Saldo fictício disponível")
   Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("••••  ••••  ••••  2048");Icon(Icons.Default.Contactless,null)}
  }
 }
}
