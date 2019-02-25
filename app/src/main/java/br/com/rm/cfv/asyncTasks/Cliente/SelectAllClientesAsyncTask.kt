package br.com.rm.cfv.asyncTasks.Cliente

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IAsyncTaskPostExecute
import br.com.rm.cfv.database.daos.interfaces.ClienteDAO
import br.com.rm.cfv.database.entities.Cliente

class SelectAllClientesAsyncTask(private val dao : ClienteDAO, private var iAsyncTaskPostExecute : IAsyncTaskPostExecute) : AsyncTask<Any, Any, List<Cliente>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        iAsyncTaskPostExecute.showProgress("Buscando Clientes...")
    }

    override fun doInBackground(vararg params: Any?): List<Cliente> {
        return dao.getAll()
    }

    override fun onPostExecute(result: List<Cliente>?) {
        super.onPostExecute(result)
        iAsyncTaskPostExecute.afterExecute(result)
        iAsyncTaskPostExecute.hideProgress()
    }

}
