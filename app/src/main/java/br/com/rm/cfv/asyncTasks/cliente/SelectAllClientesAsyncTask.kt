package br.com.rm.cfv.asyncTasks.cliente

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.ClienteDAO
import br.com.rm.cfv.database.entities.Cliente

class SelectAllClientesAsyncTask(private val dao : ClienteDAO, private var ipostExecuteSearch : IPostExecuteSearch, private var showProgress: Boolean = true) : AsyncTask<Any, Any, List<Cliente>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        if(showProgress)
            ipostExecuteSearch.showProgress("Buscando Clientes...")
    }

    override fun doInBackground(vararg params: Any?): List<Cliente> {
        if(params.isEmpty() || params[0] == null) {
            return dao.getAll()
        }else{
            return dao.search("%"+params[0]+"%")
        }
    }

    override fun onPostExecute(result: List<Cliente>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        if(showProgress)
            ipostExecuteSearch.hideProgress()
    }

}
