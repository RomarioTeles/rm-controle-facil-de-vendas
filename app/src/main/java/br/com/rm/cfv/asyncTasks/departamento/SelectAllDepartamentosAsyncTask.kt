package br.com.rm.cfv.asyncTasks.cliente

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.DepartamentoDAO
import br.com.rm.cfv.database.entities.Departamento

class SelectAllDepartamentosAsyncTask(private val dao : DepartamentoDAO, private var ipostExecuteSearch : IPostExecuteSearch, private var showProgress: Boolean = true) : AsyncTask<Any, Any, List<Departamento>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        if(showProgress)
            ipostExecuteSearch.showProgress("Buscando Departamentos...")
    }

    override fun doInBackground(vararg params: Any?): List<Departamento> {
        if(params.isEmpty() || params[0] == null) {
            return dao.getAll()
        }else{
            return dao.search("%"+params[0]+"%")
        }
    }

    override fun onPostExecute(result: List<Departamento>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        if(showProgress)
            ipostExecuteSearch.hideProgress()
    }

}
