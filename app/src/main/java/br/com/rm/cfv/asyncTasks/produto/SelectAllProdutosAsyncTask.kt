package br.com.rm.cfv.asyncTasks.produto

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.ProdutoDAO
import br.com.rm.cfv.database.entities.Produto

class SelectAllProdutosAsyncTask(private val dao : ProdutoDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<String, Any, List<Produto>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Produtos...")
    }

    override fun doInBackground(vararg params: String?): List<Produto> {
        if(params.isEmpty() || params[0]!!.isBlank()) {
            return dao.getAll()
        }else{
            return dao.search(params[0]+"%")
        }
    }

    override fun onPostExecute(result: List<Produto>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
