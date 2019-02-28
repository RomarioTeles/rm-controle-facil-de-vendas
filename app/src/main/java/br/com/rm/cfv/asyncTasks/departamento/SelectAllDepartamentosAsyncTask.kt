package br.com.rm.cfv.asyncTasks.cliente

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.DepartamentoDAO
import br.com.rm.cfv.database.entities.Departamento

class SelectAllDepartamentosAsyncTask(private val dao : DepartamentoDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, Any, List<Departamento>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Departamentos...")
    }

    override fun doInBackground(vararg params: Any?): List<Departamento> {
        return dao.getAll()
    }

    override fun onPostExecute(result: List<Departamento>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
