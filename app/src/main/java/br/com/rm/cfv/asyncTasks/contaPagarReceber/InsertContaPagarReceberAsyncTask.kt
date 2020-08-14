package br.com.rm.cfv.asyncTasks.contaPagarReceber

import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.constants.*
import br.com.rm.cfv.database.daos.AppDataBase
import br.com.rm.cfv.database.entities.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList

open class InsertContaPagarReceberAsyncTask(private var dao: AppDataBase?, private val ipostExecuteInsert: IPostExecuteInsertAndUpdate) : AsyncTask<ContaPagarReceber, ContaPagarReceber, ContaPagarReceber>() {

    override fun doInBackground(vararg params: ContaPagarReceber): ContaPagarReceber? {
        try {

            var conta: ContaPagarReceber = params.get(0)

            var result = dao!!.contaPagarReceberDAO().insert(conta)

            if(result != null){

                conta.uid = result.toInt()

                if(conta.itemProdutoList != null && conta.itemProdutoList.isNotEmpty()) {
                    conta.itemProdutoList.forEach {
                        it.debitoClienteId = result.toInt()
                    }

                    dao!!.itemProdutoDAO().insertAll(conta.itemProdutoList)

                }

                val pagamentos = criaPagamentos(conta)
                dao!!.pagamentoDebitoDAO().insertAll(*pagamentos)
                atualizaEstoque(dao!!, conta)
                criaBalancete(conta, pagamentos)
            }

            return conta
        }catch (e : SQLiteConstraintException){
            return null
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteInsert.showProgress("Registrando...")
    }

    override fun onPostExecute(result: ContaPagarReceber?) {
        super.onPostExecute(result)
        ipostExecuteInsert.afterInsert(result)
        ipostExecuteInsert.hideProgress()
    }

    fun criaBalancete(conta : ContaPagarReceber, pagamentos:  Array<PagamentoDebito>){


        val data = Calendar.getInstance()
        val mes = data.get(Calendar.MONTH) + 1
        val ano = data.get(Calendar.YEAR)
        val balancete =  dao!!.balanceteDAO().findByMesAndAno(mes, ano)
        val balanceteId : Long?
        if(balancete == null){
            balanceteId = dao!!.balanceteDAO().insert(Balancete(null, mes, ano ))
        }else{
            balanceteId = balancete.uid!!.toLong()
        }

        if(conta.tipoPagamento == TipoPagamento.A_VISTA) {

            var tipoRef = TipoItemBalancete.getByTipoReferencia(conta.tipoRef)

            if (conta.tipoRef == TipoReferencia.CLIENTE || conta.tipoRef == TipoReferencia.RECEITAS) {
                tipoRef = TipoItemBalancete.RECEITA
            }


            var itensBalancete: List<ItemBalancete> = pagamentos.map {
                ItemBalancete(
                    null, Date(),
                    tipoRef!!,
                    it.valor,
                    conta.nomeRef,
                    conta.idRef!!,
                    balanceteId!!.toInt()
                )
            }


            dao!!.itemBalanceteDAO().insertAll(*itensBalancete.toTypedArray())
        }

    }

    fun criaPagamentos(debitoCliente: ContaPagarReceber): Array<PagamentoDebito>{
        if(debitoCliente.tipoPagamento == TipoPagamento.A_VISTA){
            val pagamentoDebito = PagamentoDebito()
            pagamentoDebito.contaPagarReceberId = debitoCliente.uid
            pagamentoDebito.meioPagamento = debitoCliente.meioPagamento
            pagamentoDebito.valorPago = debitoCliente.total
            pagamentoDebito.valor = debitoCliente.total
            return arrayOf(pagamentoDebito)
        }else{
            val lista : ArrayList<PagamentoDebito> = ArrayList()
            val qtdeParcelas = debitoCliente.qtdeParcelas
            val valor = debitoCliente.total / qtdeParcelas
            val valorParcela = valor
            for(num in 0 until qtdeParcelas){
                val pagamentoDebito = PagamentoDebito()
                pagamentoDebito.contaPagarReceberId = debitoCliente.uid
                pagamentoDebito.meioPagamento = debitoCliente.meioPagamento
                pagamentoDebito.valor = BigDecimal(valorParcela).setScale(2, RoundingMode.CEILING).toDouble()
                if(num > 0){
                    val calendar = GregorianCalendar()
                    calendar.time = Date(debitoCliente.dataPrevistaPagamento!!)
                    calendar.add(Calendar.MONTH, num)
                    pagamentoDebito.dataVencimento = calendar.time
                }else{
                    pagamentoDebito.dataVencimento = Date(debitoCliente.dataPrevistaPagamento!!)
                }

                lista.add(pagamentoDebito)
            }
            return lista.toTypedArray()
        }
    }

    fun atualizaEstoque(dao: AppDataBase, conta: ContaPagarReceber){
        if(conta.tipoRef == TipoReferencia.FORNECEDOR || conta.tipoRef == TipoReferencia.CLIENTE ) {
            var itensProdutos = conta.itemProdutoList
            val date = Date().time
            val listaEstoque = mutableListOf<MovimentacaoEstoque>()
            var tipoMotivoMovimentacao = if (conta.tipoRef == TipoReferencia.FORNECEDOR) TipoMovimentacaoEstoque.ENTRADA else TipoMovimentacaoEstoque.SAIDA
            var movimentacaoEstoque = if (conta.tipoRef == TipoReferencia.FORNECEDOR) MotivoMovimentacao.COMPRA_MERCADORIA else MotivoMovimentacao.VENDA_MERCADORIA

            itensProdutos.forEach {
                val estoque = MovimentacaoEstoque(
                    null,
                    it.codigoProduto!!,
                    movimentacaoEstoque,
                    date,
                    tipoMotivoMovimentacao,
                    it.getQuantidade()
                )
                listaEstoque.add(estoque)
            }

            dao.movimentacaoEstoqueDAO().insertAll(*listaEstoque.toTypedArray())
        }

    }

}