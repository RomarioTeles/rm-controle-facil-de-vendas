package br.com.rm.cfv.asyncTasks.balancete

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.ItemBalanceteDAO
import br.com.rm.cfv.database.entities.Balancete
import br.com.rm.cfv.database.entities.ItemBalancete
import br.com.rm.cfv.database.entities.dtos.TotalBalanceteDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectTotalBalanceteCoroutine(private val dao : ItemBalanceteDAO, private var ipostExecuteSearch : IPostExecuteSearch){

    private fun onPreExecute() {
        ipostExecuteSearch.showProgress("Buscando Balancetes...")
    }

    fun execute(balancete: Balancete) {
        onPreExecute()
        CoroutineScope(Dispatchers.IO).launch {
            val data = dao.getTotalBalancete(balancete.uid!!)
            withContext(Dispatchers.Main){
                onPostExecute(data)
            }
        }
    }

    private fun onPostExecute(result: TotalBalanceteDTO?) {
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
