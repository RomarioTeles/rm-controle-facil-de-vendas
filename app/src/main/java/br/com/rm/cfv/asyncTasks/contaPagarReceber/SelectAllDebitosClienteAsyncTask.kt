package br.com.rm.cfv.asyncTasks.contaPagarReceber

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.ContaPagarReceberDAO
import br.com.rm.cfv.database.entities.dtos.DebitoClienteDTO

class SelectAllDebitosClienteAsyncTask(private val dao : ContaPagarReceberDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, Any, DebitoClienteDTO>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Débitos...")
    }

    override fun doInBackground(vararg params: Any?): DebitoClienteDTO {

        var cliente_id = params[0]

        var debitos = dao.getSubtotal(cliente_id as Int)

        var dto : DebitoClienteDTO = dao.findByClienteIdAndStatus(cliente_id)
        if(dto != null) {
            dto.debitos = debitos
        }else{
            dto = DebitoClienteDTO()
            dto.debitos = listOf()
        }

        return dto
    }

    override fun onPostExecute(result: DebitoClienteDTO?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
