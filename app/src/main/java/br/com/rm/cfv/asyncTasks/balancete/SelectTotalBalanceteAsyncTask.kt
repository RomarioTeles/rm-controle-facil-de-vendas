package br.com.rm.cfv.asyncTasks.balancete

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.ItemBalanceteDAO
import br.com.rm.cfv.database.entities.Balancete
import br.com.rm.cfv.database.entities.ItemBalancete
import br.com.rm.cfv.database.entities.dtos.TotalBalanceteDTO

class SelectTotalBalanceteAsyncTask(private val dao : ItemBalanceteDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, TotalBalanceteDTO, TotalBalanceteDTO>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Balancetes...")
    }

    override fun doInBackground(vararg params: Any?): TotalBalanceteDTO {
        var balancete = params[0] as Balancete
        return dao.getTotalBalancete(balancete.uid!!)
    }

    override fun onPostExecute(result: TotalBalanceteDTO?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
