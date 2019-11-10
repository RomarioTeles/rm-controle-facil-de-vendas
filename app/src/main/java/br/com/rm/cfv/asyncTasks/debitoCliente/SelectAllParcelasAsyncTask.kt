package br.com.rm.cfv.asyncTasks.debitoCliente

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.DebitoClienteDAO
import br.com.rm.cfv.database.daos.interfaces.PagamentoDebitoDAO
import br.com.rm.cfv.database.entities.DebitoCliente
import br.com.rm.cfv.database.entities.PagamentoDebito

class SelectAllParcelasAsyncTask(private val dao : PagamentoDebitoDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, Any, List<PagamentoDebito>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Carregando Débito...")
    }

    override fun doInBackground(vararg params: Any?):  List<PagamentoDebito> {

        var codigo = params[0]

        return dao.getAll(codigo as Int)
    }

    override fun onPostExecute(result:  List<PagamentoDebito>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
