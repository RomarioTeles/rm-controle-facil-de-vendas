package br.com.rm.cfv.asyncTasks.debitoCliente

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.DebitoClienteDAO
import br.com.rm.cfv.database.daos.interfaces.ItemProdutoDAO
import br.com.rm.cfv.database.entities.DebitoCliente
import br.com.rm.cfv.database.entities.ItemProduto

class SelectAllLancamentosAsyncTask(private val dao : ItemProdutoDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, Any, List<ItemProduto>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Carregando Débito...")
    }

    override fun doInBackground(vararg params: Any?): List<ItemProduto> {

        var codigo = params[0]

        return dao.getAll(codigo as Int)
    }

    override fun onPostExecute(result: List<ItemProduto>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
