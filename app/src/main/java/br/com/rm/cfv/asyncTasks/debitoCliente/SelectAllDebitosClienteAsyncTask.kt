package br.com.rm.cfv.asyncTasks.debitoCliente

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.DebitoClienteDAO
import br.com.rm.cfv.database.entities.DebitoCliente

class SelectAllDebitosClienteAsyncTask(private val dao : DebitoClienteDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, Any, List<DebitoCliente>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Débitos...")
    }

    override fun doInBackground(vararg params: Any?): List<DebitoCliente> {

        var cliente_id = params[0]

        return dao.findByClienteId(cliente_id as Int)
    }

    override fun onPostExecute(result: List<DebitoCliente>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
