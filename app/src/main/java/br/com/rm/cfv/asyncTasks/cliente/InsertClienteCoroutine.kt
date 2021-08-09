package br.com.rm.cfv.asyncTasks.cliente

import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import android.util.Log
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.database.daos.interfaces.ClienteDAO
import br.com.rm.cfv.database.entities.Cliente
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class InsertClienteCoroutine(private var dao: ClienteDAO?, private val ipostExecuteInsert: IPostExecuteInsertAndUpdate){

    fun execute(vararg params: Cliente){

        onPreExecute()
        CoroutineScope(Dispatchers.IO).launch {
            var result: Cliente? = null

            try {
                val cliente: Cliente = params.get(0)

                if (cliente.uid == null) {
                    dao!!.insertAll(cliente)
                } else {
                    dao!!.update(cliente)
                }

                result = dao!!.findCpf(cliente.cpf!!)

            } catch (e: SQLiteConstraintException) {
                Log.e("InsertClienteCoroutine", e.message, e)
            }

            withContext(Dispatchers.Main){
                onPostExecute(result)
            }
        }
    }

    private fun onPreExecute() {
        ipostExecuteInsert.showProgress("Inserindo Cliente...")
    }

    private fun onPostExecute(result: Cliente?) {
        ipostExecuteInsert.afterInsert(result)
        ipostExecuteInsert.hideProgress()
    }

}