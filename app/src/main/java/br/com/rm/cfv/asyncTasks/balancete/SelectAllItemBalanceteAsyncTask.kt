package br.com.rm.cfv.asyncTasks.balancete

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.ItemBalanceteDAO
import br.com.rm.cfv.database.entities.Balancete
import br.com.rm.cfv.database.entities.ItemBalancete

class SelectAllItemBalanceteAsyncTask(private val dao : ItemBalanceteDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Balancete, List<ItemBalancete>, List<ItemBalancete>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Balancetes...")
    }

    override fun doInBackground(vararg params: Balancete?): List<ItemBalancete> {
        var balancete = params[0] as Balancete
        return dao.findByBalanceteId(balancete.uid!!)
    }

    override fun onPostExecute(result: List<ItemBalancete>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
