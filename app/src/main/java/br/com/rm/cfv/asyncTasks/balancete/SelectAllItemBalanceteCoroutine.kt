package br.com.rm.cfv.asyncTasks.balancete

import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.ItemBalanceteDAO
import br.com.rm.cfv.database.entities.Balancete
import br.com.rm.cfv.database.entities.ItemBalancete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectAllItemBalanceteCoroutine(private val dao : ItemBalanceteDAO, private var ipostExecuteSearch : IPostExecuteSearch){

    private fun onPreExecute() {
        ipostExecuteSearch.showProgress("Buscando Balancetes...")
    }

    fun execute(balancete: Balancete, tipos: List<String>){
        onPreExecute()
        CoroutineScope(Dispatchers.IO).launch {
            val data = dao.findByBalanceteIdAndTipo(balancete.uid!!, tipos)
            withContext(Dispatchers.Main){
                onPostExecute(data)
            }
        }

    }

    private fun onPostExecute(result: List<ItemBalancete>?) {
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
