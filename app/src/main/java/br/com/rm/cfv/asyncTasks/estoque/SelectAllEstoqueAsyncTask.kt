package br.com.rm.cfv.asyncTasks.estoque

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.MovimentacaoEstoqueDAO
import br.com.rm.cfv.database.entities.dtos.EstoqueDTO

class SelectAllEstoqueAsyncTask(private val dao : MovimentacaoEstoqueDAO, private var ipostExecuteSearch : IPostExecuteSearch, private var showProgress : Boolean = true) : AsyncTask<Any, Any, List<EstoqueDTO>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        if(showProgress)
            ipostExecuteSearch.showProgress("Buscando Produtos...")
    }

    override fun doInBackground(vararg params: Any?): List<EstoqueDTO> {
        if(params.isEmpty() || params[0] == null) {
            return dao.getAllEstoque()
        }else{
            return dao.search("%"+params[0]+"%")
        }
    }

    override fun onPostExecute(result: List<EstoqueDTO>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        if(showProgress)
            ipostExecuteSearch.hideProgress()
    }

}
