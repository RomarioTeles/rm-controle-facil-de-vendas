package br.com.rm.cfv.asyncTasks.contaPagarReceber

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.daos.interfaces.ContaPagarReceberDAO
import br.com.rm.cfv.database.entities.IReferencia
import br.com.rm.cfv.database.entities.dtos.DebitoClienteDTO

class SelectAllDebitosClienteAsyncTask(private val dao : ContaPagarReceberDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<IReferencia, Any, DebitoClienteDTO>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Débitos...")
    }

    override fun doInBackground(vararg params: IReferencia?): DebitoClienteDTO {

        var ref = params[0] as IReferencia

        var debitos = dao.getSubtotalPagamentos(ref.getIdRef()!!, ref.getTipoRef()!!)

        var dto : DebitoClienteDTO = dao.getSubtotal(ref.getIdRef()!!, ref.getTipoRef()!!)

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
