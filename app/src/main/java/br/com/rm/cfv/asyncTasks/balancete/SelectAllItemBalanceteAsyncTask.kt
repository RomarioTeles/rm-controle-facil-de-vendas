package br.com.rm.cfv.asyncTasks.balancete

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.ItemBalanceteDAO
import br.com.rm.cfv.database.entities.Balancete
import br.com.rm.cfv.database.entities.ItemBalancete

class SelectAllItemBalanceteAsyncTask(private val dao : ItemBalanceteDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, List<ItemBalancete>, List<ItemBalancete>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Balancetes...")
    }

    override fun doInBackground(vararg params: Any?): List<ItemBalancete> {
        val balancete = params[0] as Balancete
        val tipos = params[1] as List<String>
        return dao.findByBalanceteIdAndTipo(balancete.uid!!, tipos)
    }

    override fun onPostExecute(result: List<ItemBalancete>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
