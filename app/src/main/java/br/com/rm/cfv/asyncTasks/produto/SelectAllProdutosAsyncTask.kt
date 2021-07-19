package br.com.rm.cfv.asyncTasks.produto

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.ProdutoDAO
import br.com.rm.cfv.database.entities.Produto

class SelectAllProdutosAsyncTask(private val dao : ProdutoDAO, private var ipostExecuteSearch : IPostExecuteSearch, private var showProgress : Boolean = true, private var considerarEstoque: Boolean = false) : AsyncTask<String, Any, List<Produto>>(){


    override fun onPreExecute() {
        super.onPreExecute()
        if(showProgress) {
            ipostExecuteSearch.showProgress("Buscando Produtos...")
        }
    }

    override fun doInBackground(vararg params: String?): List<Produto> {
        var query = if (params.isEmpty() || params[0] == null) "%%" else "%" + params[0] + "%"
        if(considerarEstoque){
            return dao.searchComEstoquePositivo(query)
        }else {
            return dao.search(query)
        }
    }

    override fun onPostExecute(result: List<Produto>?) {
        super.onPostExecute(result)
        if(showProgress) {
            ipostExecuteSearch.hideProgress()
        }
        ipostExecuteSearch.afterSearch(result)

    }

}
