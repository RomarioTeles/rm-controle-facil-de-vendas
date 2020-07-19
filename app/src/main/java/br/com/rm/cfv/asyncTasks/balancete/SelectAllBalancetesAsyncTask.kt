package br.com.rm.cfv.asyncTasks.balancete

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.BalanceteDAO
import br.com.rm.cfv.database.entities.Balancete

class SelectAllBalancetesAsyncTask(private val dao : BalanceteDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, Any, List<Balancete>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Balancetes...")
    }

    override fun doInBackground(vararg params: Any?): List<Balancete> {
        return dao.getAll()
    }

    override fun onPostExecute(result: List<Balancete>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
