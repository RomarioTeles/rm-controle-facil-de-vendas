package br.com.rm.cfv.asyncTasks.produto

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.ProdutoDAO
import br.com.rm.cfv.database.entities.Produto

class SelectAllProdutosAsyncTask(private val dao : ProdutoDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, Any, List<Produto>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Produtos...")
    }

    override fun doInBackground(vararg params: Any?): List<Produto> {
        return dao.getAll()
    }

    override fun onPostExecute(result: List<Produto>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
