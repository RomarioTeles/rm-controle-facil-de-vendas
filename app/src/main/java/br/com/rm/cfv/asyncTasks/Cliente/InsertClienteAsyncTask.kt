package br.com.rm.cfv.asyncTasks.Cliente

import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IAsyncTaskPostExecute
import br.com.rm.cfv.database.daos.interfaces.ClienteDAO
import br.com.rm.cfv.database.entities.Cliente

open class InsertClienteAsyncTask(private var dao: ClienteDAO?,private val asyncTaskPostExecute: IAsyncTaskPostExecute) : AsyncTask<Cliente, Cliente, Cliente>() {

    override fun doInBackground(vararg params: Cliente): Cliente? {
        try {
            var result: Cliente? = null

            var cliente: Cliente = params.get(0)

            dao!!.insertAll(cliente)

            result = dao!!.findCpf(cliente.cpf!!)

            return result
        }catch (e : SQLiteConstraintException){
            return null
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        asyncTaskPostExecute.showProgress("Inserindo Cliente...")
    }

    override fun onPostExecute(result: Cliente?) {
        super.onPostExecute(result)
        asyncTaskPostExecute.afterExecute(result)
        asyncTaskPostExecute.hideProgress()
    }

}