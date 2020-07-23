package br.com.rm.cfv.asyncTasks.cliente

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteDelete
import br.com.rm.cfv.database.daos.interfaces.ClienteDAO
import java.lang.Exception

class DeleteClienteAsyncTask(private val dao : ClienteDAO, private var ipostExecuteDelete : IPostExecuteDelete) : AsyncTask<Any, Any, Int>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteDelete.showProgress("Removendo cliente...")
    }

    override fun doInBackground(vararg params: Any?): Int {

        val id = params[0]
        val position = params[1]

        try {
            val cliente = dao.findById(id as Int)
            dao.delete(cliente!!)
            return position as Int
        }catch (e : Exception){
            e.printStackTrace()
            return -1
        }
    }

    override fun onPostExecute(result: Int) {
        super.onPostExecute(result)
        ipostExecuteDelete.afterDelete(result)
        ipostExecuteDelete.hideProgress()
    }

}
