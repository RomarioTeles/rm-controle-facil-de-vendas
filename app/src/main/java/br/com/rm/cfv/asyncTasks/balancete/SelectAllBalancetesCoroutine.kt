package br.com.rm.cfv.asyncTasks.balancete

import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.BalanceteDAO
import br.com.rm.cfv.database.entities.Balancete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SelectAllBalancetesCoroutine(private val dao : BalanceteDAO, private var ipostExecuteSearch : IPostExecuteSearch){

    private fun onPreExecute() {
        ipostExecuteSearch.showProgress("Buscando Balancetes...")
    }

    fun execute(){

        CoroutineScope(Dispatchers.Main).launch {
            onPreExecute()
            val balancetes = mutableListOf<Balancete>()
            withContext(Dispatchers.IO) {
                val dadoReturn = dao.getAll()

                if(dadoReturn.isEmpty()){
                    val cal = Calendar.getInstance()
                    val mes = cal.get(Calendar.MONTH) + 1
                    val ano = cal.get(Calendar.YEAR)
                    val balancete = Balancete(null, mes, ano)
                    val id = dao.insert(balancete)
                    balancete.uid = id.toInt()
                    balancetes.add(balancete)
                }else{
                    balancetes.addAll(dadoReturn)
                }
            }
            withContext(Dispatchers.Main) {
                onPostExecute(balancetes.toList())
            }
        }

    }

    private fun onPostExecute(result: List<Balancete>?) {
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
