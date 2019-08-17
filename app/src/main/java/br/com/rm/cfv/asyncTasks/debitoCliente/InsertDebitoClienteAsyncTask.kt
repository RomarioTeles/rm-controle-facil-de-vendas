package br.com.rm.cfv.asyncTasks.debitoCliente

import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.database.daos.AppDataBase
import br.com.rm.cfv.database.entities.DebitoCliente

open class InsertDebitoClienteAsyncTask(private var dao: AppDataBase?, private val ipostExecuteInsert: IPostExecuteInsertAndUpdate) : AsyncTask<DebitoCliente, DebitoCliente, DebitoCliente>() {

    override fun doInBackground(vararg params: DebitoCliente): DebitoCliente? {
        try {
            var result: DebitoCliente? = null

            var cliente: DebitoCliente = params.get(0)

            dao!!.debitoClienteDAO().insertAll(cliente)

            result = dao!!.debitoClienteDAO().findByCodigo(cliente.codigo)

            if(result != null){
                dao!!.itemProdutoDAO().insertAll(cliente.itemProdutoList)
            }

            return result
        }catch (e : SQLiteConstraintException){
            return null
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteInsert.showProgress("Registrando Debito pra o Cliente...")
    }

    override fun onPostExecute(result: DebitoCliente?) {
        super.onPostExecute(result)
        ipostExecuteInsert.afterInsert(result)
        ipostExecuteInsert.hideProgress()
    }

}