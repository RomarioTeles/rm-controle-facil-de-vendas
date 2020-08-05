package br.com.rm.cfv.asyncTasks.balancete

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.BalanceteDAO
import br.com.rm.cfv.database.entities.Balancete
import java.util.*

class SelectAllBalancetesAsyncTask(private val dao : BalanceteDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, Any, List<Balancete>>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Balancetes...")
    }

    override fun doInBackground(vararg params: Any?): List<Balancete> {
        var balancetes = mutableListOf<Balancete>()

        val dadoReturn = dao.getAll()

        if(dadoReturn.isEmpty()){
            val cal = Calendar.getInstance()
            val mes = cal.get(Calendar.MONTH) + 1
            val ano = cal.get(Calendar.YEAR)
            val balancete = Balancete(null, mes, ano)
            val id = dao.insert(balancete)
            balancete.uid = id.toInt()
            balancetes.add(balancete)
        }else{
            balancetes.addAll(dadoReturn)
        }

        return balancetes.toList()
    }

    override fun onPostExecute(result: List<Balancete>?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
