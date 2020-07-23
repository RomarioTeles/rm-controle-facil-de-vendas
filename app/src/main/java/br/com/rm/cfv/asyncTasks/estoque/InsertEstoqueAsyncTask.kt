package br.com.rm.cfv.asyncTasks.estoque

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.database.daos.interfaces.MovimentacaoEstoqueDAO
import br.com.rm.cfv.database.entities.MovimentacaoEstoque
import java.lang.Exception

open class InsertEstoqueAsyncTask(private var dao: MovimentacaoEstoqueDAO, private val ipostExecuteInsert: IPostExecuteInsertAndUpdate) : AsyncTask<MovimentacaoEstoque, Boolean, Boolean>() {

    override fun doInBackground(vararg params: MovimentacaoEstoque?): Boolean {

        try {
            val estoque: MovimentacaoEstoque? = params[0]

            dao.insertAll(estoque!!)

            return true
        }catch (ignored : Exception){ }
        return false
    }


    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteInsert.showProgress("Atualizando estoque...")
    }

    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)
        ipostExecuteInsert.afterInsert(result)
        ipostExecuteInsert.hideProgress()
    }

}