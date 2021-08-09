package br.com.rm.cfv.asyncTasks.contaPagarReceber

import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.PagamentoDebitoDAO
import br.com.rm.cfv.database.entities.PagamentoDebito
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectAllParcelasCoroutine(private val dao : PagamentoDebitoDAO, private var ipostExecuteSearch : IPostExecuteSearch){

    private fun onPreExecute() {
        ipostExecuteSearch.showProgress("Carregando Débito...")
    }

    fun execute(codigo: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val data = dao.getAll(codigo)
            withContext(Dispatchers.Main) {
                onPostExecute(data)
            }
        }
    }

    private fun onPostExecute(result:  List<PagamentoDebito>) {
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
