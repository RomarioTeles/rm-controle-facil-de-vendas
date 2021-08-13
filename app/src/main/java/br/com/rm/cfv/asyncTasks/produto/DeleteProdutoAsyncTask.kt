package br.com.rm.cfv.asyncTasks.produto

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteDelete
import br.com.rm.cfv.database.daos.interfaces.ProdutoDAO
import java.lang.Exception

class DeleteProdutoAsyncTask(private val dao : ProdutoDAO, private var ipostExecuteSearch : IPostExecuteDelete) : AsyncTask<Any, Any, Int>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Removendo produto...")
    }

    override fun doInBackground(vararg params: Any?): Int {

        val id = params[0]
        val position = params[1]

        try {
            val produto = dao.findById(id as Int)
            dao.delete(produto)
            return position as Int
        }catch (e : Exception){
            e.printStackTrace()
            return -1
        }
    }

    override fun onPostExecute(result: Int) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterDelete(result)
        ipostExecuteSearch.hideProgress()
    }

}
