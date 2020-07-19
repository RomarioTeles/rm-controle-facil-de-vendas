package br.com.rm.cfv.asyncTasks.contaPagarReceber

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.ContaPagarReceberDAO
import java.lang.Exception

class DeleteDebitoClienteAsyncTask(private val dao : ContaPagarReceberDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, Any, Int>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Removendo Débito...")
    }

    override fun doInBackground(vararg params: Any?): Int {

        val id = params[0]
        val position = params[1]

        try {
            var debito = dao.findById(id as Int)
            dao.delete(debito)
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
