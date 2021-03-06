package br.com.rm.cfv.asyncTasks.fornecedor

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.FornecedorDAO
import br.com.rm.cfv.database.entities.Fornecedor

class SelectAllFornecedorsAsyncTask(private val dao : FornecedorDAO, private var ipostExecuteSearch : IPostExecuteSearch, private var showProgress: Boolean = true) : AsyncTask<Any, Any, List<Fornecedor>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        if(showProgress)
            ipostExecuteSearch.showProgress("Buscando Fornecedores...")
    }

    override fun doInBackground(vararg params: Any?): List<Fornecedor> {
        if(params.isEmpty() || params[0] == null) {
            return dao.getAll()
        }else{
            return dao.search("%"+params[0]+"%")
        }
    }

    override fun onPostExecute(result: List<Fornecedor>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        if(showProgress)
            ipostExecuteSearch.hideProgress()
    }

}
