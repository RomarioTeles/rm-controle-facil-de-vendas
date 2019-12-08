package br.com.rm.cfv.asyncTasks.debitoCliente

import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.constants.MeioPagamento
import br.com.rm.cfv.constants.MotivoMovimentacao
import br.com.rm.cfv.constants.TipoMovimentacaoEstoque
import br.com.rm.cfv.constants.TipoPagamento
import br.com.rm.cfv.database.daos.AppDataBase
import br.com.rm.cfv.database.entities.DebitoCliente
import br.com.rm.cfv.database.entities.ItemProduto
import br.com.rm.cfv.database.entities.MovimentacaoEstoque
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

                cliente.itemProdutoList.forEach {
                    it.debitoClienteId = result.uid
                }

                dao!!.itemProdutoDAO().insertAll(cliente.itemProdutoList)
                cliente.uid = result.uid
                dao!!.pagamentoDebitoDAO().insertAll(*criaPagamentos(cliente))
                atualizaEstoque(dao!!, cliente.itemProdutoList)
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
            for(num in 0 until qtdeParcelas){
                val pagamentoDebito = PagamentoDebito()
                pagamentoDebito.debitoClienteId = debitoCliente.uid
                pagamentoDebito.meioPagamento = debitoCliente.meioPagamento
                pagamentoDebito.valor = BigDecimal(valorParcela).setScale(2, RoundingMode.CEILING).toDouble()
                if(num > 0){
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

    fun atualizaEstoque(dao: AppDataBase, itensProdutos : List<ItemProduto>){

        val date = Date().time
        val listaEstoque = mutableListOf<MovimentacaoEstoque>()
        itensProdutos.forEach {
            val estoque = MovimentacaoEstoque(null, it.codigoProduto!!, MotivoMovimentacao.VENDA_ITEM, date, TipoMovimentacaoEstoque.SAIDA ,it.getQuantidade() )
            listaEstoque.add(estoque)
        }

        dao.movimentacaoEstoqueDAO().insertAll(*listaEstoque.toTypedArray())

    }

}