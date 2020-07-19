package br.com.rm.cfv.asyncTasks.fornecedor

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.FornecedorDAO
import br.com.rm.cfv.database.entities.Fornecedor

class SelectAllFornecedorsAsyncTask(private val dao : FornecedorDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, Any, List<Fornecedor>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Fornecedores...")
    }

    override fun doInBackground(vararg params: Any?): List<Fornecedor> {
        return dao.getAll()
    }

    override fun onPostExecute(result: List<Fornecedor>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
