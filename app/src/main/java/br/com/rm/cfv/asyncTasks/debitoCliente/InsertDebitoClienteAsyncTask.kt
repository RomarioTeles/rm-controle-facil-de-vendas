package br.com.rm.cfv.asyncTasks.debitoCliente

import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.constants.MeioPagamento
import br.com.rm.cfv.constants.TipoPagamento
import br.com.rm.cfv.database.daos.AppDataBase
import br.com.rm.cfv.database.entities.DebitoCliente
import br.com.rm.cfv.database.entities.PagamentoDebito
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList

open class InsertDebitoClienteAsyncTask(private var dao: AppDataBase?, private val ipostExecuteInsert: IPostExecuteInsertAndUpdate) : AsyncTask<DebitoCliente, DebitoCliente, DebitoCliente>() {

    override fun doInBackground(vararg params: DebitoCliente): DebitoCliente? {
        try {
            var result: DebitoCliente? = null

            var cliente: DebitoCliente = params.get(0)

            dao!!.debitoClienteDAO().insertAll(cliente)

            result = dao!!.debitoClienteDAO().findByCodigo(cliente.codigo)

            if(result != null){
                dao!!.itemProdutoDAO().insertAll(cliente.itemProdutoList)
                dao!!.pagamentoDebitoDAO().insertAll(*criaPagamentos(cliente))
            }

            return result
        }catch (e : SQLiteConstraintException){
            return null
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteInsert.showProgress("Registrando Debito pra o Cliente...")
    }

    override fun onPostExecute(result: DebitoCliente?) {
        super.onPostExecute(result)
        ipostExecuteInsert.afterInsert(result)
        ipostExecuteInsert.hideProgress()
    }

    fun criaPagamentos(debitoCliente: DebitoCliente): Array<PagamentoDebito>{
        if(debitoCliente.tipoPagamento == TipoPagamento.A_VISTA){
            val pagamentoDebito = PagamentoDebito()
            pagamentoDebito.debitoClienteId = debitoCliente.uid
            pagamentoDebito.meioPagamento = debitoCliente.meioPagamento
            pagamentoDebito.valorPago = debitoCliente.total
            pagamentoDebito.valor = debitoCliente.total
            return arrayOf(pagamentoDebito)
        }else{
            val lista : ArrayList<PagamentoDebito> = ArrayList()
            val qtdeParcelas = debitoCliente.qtdeParcelas
            val percentualJurosParcelas= debitoCliente.percentualJurosParcelas
            val valor = debitoCliente.total / qtdeParcelas
            val juros = valor * (percentualJurosParcelas / 100)
            val valorParcela = valor+juros
            for(num in 1..qtdeParcelas){
                val pagamentoDebito = PagamentoDebito()
                pagamentoDebito.debitoClienteId = debitoCliente.uid
                pagamentoDebito.meioPagamento = MeioPagamento.DINHEIRO.name
                pagamentoDebito.valor = BigDecimal(valorParcela).setScale(2, RoundingMode.CEILING).toDouble()
                if(num > 1){
                    val calendar = GregorianCalendar()
                    calendar.time = Date(debitoCliente.dataPrevistaPagamento!!)
                    calendar.add(Calendar.MONTH, num)
                    pagamentoDebito.dataVencimento = calendar.time.time
                }else{
                    pagamentoDebito.dataVencimento = debitoCliente.dataPrevistaPagamento!!
                }

                lista.add(pagamentoDebito)
            }
            return lista.toTypedArray()
        }
    }


}