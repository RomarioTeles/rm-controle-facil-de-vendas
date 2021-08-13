package br.com.rm.cfv.asyncTasks.contaPagarReceber

import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.constants.TipoReferencia
import br.com.rm.cfv.database.daos.interfaces.ContaPagarReceberDAO
import br.com.rm.cfv.database.entities.IReferencia
import br.com.rm.cfv.database.entities.dtos.DebitoClienteDTO
import java.util.*

class SelectAllDebitosClienteAsyncTask(private val dao : ContaPagarReceberDAO, private var ipostExecuteSearch : IPostExecuteSearch) : AsyncTask<IReferencia, Any, DebitoClienteDTO>(){

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteSearch.showProgress("Buscando Débitos...")
    }

    override fun doInBackground(vararg params: IReferencia?): DebitoClienteDTO {

        var ref = params[0] as IReferencia

        val types = getTiposRef(ref.getTipoRef()!!)
        var debitos = dao.getSubtotalPagamentos(ref.getIdRef()!!, *types)

        var dto : DebitoClienteDTO = dao.getSubtotal(ref.getIdRef()!!, *types)

        if(dto != null) {
            dto.debitos = debitos
        }else{
            dto = DebitoClienteDTO()
            dto.debitos = listOf()
        }

        return dto
    }

    private fun getTiposRef(tipoRef: String) : Array<String>{
        return TipoReferencia.valuesByTipoRef(tipoRef.uppercase(Locale.getDefault()))!!.toTypedArray()
    }

    override fun onPostExecute(result: DebitoClienteDTO?) {
        super.onPostExecute(result)
        ipostExecuteSearch.afterSearch(result)
        ipostExecuteSearch.hideProgress()
    }

}
