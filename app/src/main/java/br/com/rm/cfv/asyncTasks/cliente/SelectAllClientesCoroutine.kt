package br.com.rm.cfv.asyncTasks.cliente

import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.ClienteDAO
import br.com.rm.cfv.database.entities.Cliente
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectAllClientesCoroutine(private val dao : ClienteDAO, private var ipostExecuteSearch : IPostExecuteSearch, private var showProgress: Boolean = true) {

    private fun onPreExecute() {
        if(showProgress)
            ipostExecuteSearch.showProgress("Buscando Clientes...")
    }

    fun execute(query: String?){

        onPreExecute()
        CoroutineScope(Dispatchers.IO).launch {
            val data = mutableListOf<Cliente>()
            if (query == null || query.isBlank()) {
                 data.addAll(dao.getAll())
            } else {
                data.addAll(dao.search("%" + query + "%"))
            }
            withContext(Dispatchers.Main){
                onPostExecute(data)
            }
        }
    }

    private fun onPostExecute(result: List<Cliente>?) {
        ipostExecuteSearch.afterSearch(result)
        if(showProgress)
            ipostExecuteSearch.hideProgress()
    }

}
