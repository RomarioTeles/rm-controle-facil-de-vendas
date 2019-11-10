package br.com.rm.cfv.asyncTasks.debitoCliente

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.constants.StatusPagamento
import br.com.rm.cfv.database.daos.interfaces.DebitoClienteDAO
import br.com.rm.cfv.database.entities.DebitoCliente
import br.com.rm.cfv.database.entities.dtos.DebitoClienteDTO

class SelectAllDebitosClienteAsyncTask(private val dao : DebitoClienteDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<Any, Any, DebitoClienteDTO>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Débitos...")
    }

    override fun doInBackground(vararg params: Any?): DebitoClienteDTO {

        var cliente_id = params[0]

        var debitos = dao.findByClienteId(cliente_id as Int)

        var dto : DebitoClienteDTO = dao.findByClienteIdAndStatus(cliente_id, StatusPagamento.PENDENTE)
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
