package br.com.rm.cfv.asyncTasks.cliente

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IAsyncTaskPostExecute
import br.com.rm.cfv.database.daos.interfaces.DepartamentoDAO
import br.com.rm.cfv.database.entities.Departamento

class SelectAllDepartamentosAsyncTask(private val dao : DepartamentoDAO, private var iAsyncTaskPostExecute : IAsyncTaskPostExecute) : AsyncTask<Any, Any, List<Departamento>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        iAsyncTaskPostExecute.showProgress("Buscando Departamentos...")
    }

    override fun doInBackground(vararg params: Any?): List<Departamento> {
        return dao.getAll()
    }

    override fun onPostExecute(result: List<Departamento>?) {
        super.onPostExecute(result)
        iAsyncTaskPostExecute.afterExecute(result)
        iAsyncTaskPostExecute.hideProgress()
    }

}
