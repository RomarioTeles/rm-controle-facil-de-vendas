package br.com.rm.cfv.asyncTasks.cliente

import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IAsyncTaskPostExecute
import br.com.rm.cfv.database.daos.interfaces.DepartamentoDAO
import br.com.rm.cfv.database.entities.Departamento

open class InsertDepartamentoAsyncTask(private var dao: DepartamentoDAO?, private val asyncTaskPostExecute: IAsyncTaskPostExecute) : AsyncTask<Departamento, Departamento, Departamento>() {

    override fun doInBackground(vararg params: Departamento): Departamento? {
        try {
            var result: Departamento? = null

            var cliente: Departamento = params.get(0)

            dao!!.insertAll(cliente)

            result = dao!!.findByNome(cliente.nome!!)

            return result
        }catch (e : SQLiteConstraintException){
            return null
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        asyncTaskPostExecute.showProgress("Inserindo Departamento...")
    }

    override fun onPostExecute(result: Departamento?) {
        super.onPostExecute(result)
        asyncTaskPostExecute.afterExecute(result)
        asyncTaskPostExecute.hideProgress()
    }

}