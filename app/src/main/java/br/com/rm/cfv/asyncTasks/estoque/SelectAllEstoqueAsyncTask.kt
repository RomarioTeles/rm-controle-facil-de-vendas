package br.com.rm.cfv.asyncTasks.estoque

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.MovimentacaoEstoqueDAO
import br.com.rm.cfv.database.entities.dtos.EstoqueDTO

class SelectAllEstoqueAsyncTask(private val dao : MovimentacaoEstoqueDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, Any, List<EstoqueDTO>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Produtos...")
    }

    override fun doInBackground(vararg params: Any?): List<EstoqueDTO> {
        return dao.getAllEstoque()
    }

    override fun onPostExecute(result: List<EstoqueDTO>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
