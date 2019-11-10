package br.com.rm.cfv.asyncTasks.debitoCliente

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.DebitoClienteDAO
import br.com.rm.cfv.database.entities.DebitoCliente
import java.lang.Exception

class DeleteDebitoClienteAsyncTask(private val dao : DebitoClienteDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, Any, Int>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Removendo Débito...")
    }

    override fun doInBackground(vararg params: Any?): Int {

        val debito = params[0]
        val position = params[1]

        try {
            dao.delete(debito as DebitoCliente)
            return position as Int
        }catch (e : Exception){
            e.printStackTrace()
            return -1
        }
    }

    override fun onPostExecute(result: Int) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
