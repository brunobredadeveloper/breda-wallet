package dev.breda.wallet
import android.content.SharedPreferences
import dev.breda.wallet.data.*
import dev.breda.wallet.domain.*
import org.json.JSONArray
import org.json.JSONObject
class LocalWalletStorage(private val prefs:SharedPreferences):WalletStorage {
 override fun load():WalletSnapshot? {
  val raw=prefs.getString("ledger",null) ?: return null
  val x=JSONObject(raw);val rows=x.getJSONArray("entries");val requests=x.getJSONArray("requests")
  return WalletSnapshot(Wallet(Money(x.getLong("balance")),(0 until rows.length()).map {i->val e=rows.getJSONObject(i);Entry(e.getString("id"),e.getString("description"),Money(e.getLong("debit")))}),(0 until requests.length()).map {i->val r=requests.getJSONObject(i);ProcessedTransfer(Transfer(r.getString("id"),r.getString("recipient"),Money(r.getLong("amount"))),when(r.getString("result")){"success"->TransferResult.Success;"funds"->TransferResult.InsufficientFunds;else->TransferResult.InvalidRequest})})
 }
 override fun save(snapshot:WalletSnapshot){
  val rows=JSONArray();snapshot.wallet.entries.forEach {rows.put(JSONObject().put("id",it.id).put("description",it.description).put("debit",it.debit.cents))}
  val requests=JSONArray();snapshot.processed.forEach {requests.put(JSONObject().put("id",it.command.requestId).put("recipient",it.command.recipient).put("amount",it.command.amount.cents).put("result",when(it.result){TransferResult.Success->"success";TransferResult.InsufficientFunds->"funds";else->"invalid"}))}
  check(prefs.edit().putString("ledger",JSONObject().put("balance",snapshot.wallet.balance.cents).put("entries",rows).put("requests",requests).toString()).commit()){ "Não foi possível persistir o lançamento." }
 }
}
