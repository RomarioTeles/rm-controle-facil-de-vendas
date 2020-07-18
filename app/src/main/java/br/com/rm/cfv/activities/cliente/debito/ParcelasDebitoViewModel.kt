package br.com.rm.cfv.activities.cliente.debito

import androidx.recyclerview.widget.LinearLayoutManager
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.adapters.cliente.ItemPagamentoAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.debitoCliente.SelectAllParcelasAsyncTask
import br.com.rm.cfv.database.entities.PagamentoDebito
import kotlinx.android.synthetic.main.fragment_visualizar_debito.view.*

class ParcelasDebitoViewModel : PageViewModel(), IPostExecuteSearch {

    lateinit var listaPagamentos : List<PagamentoDebito>
    lateinit var itemPagamentoAdapter : ItemPagamentoAdapter

    override fun showProgress(text: String) {
        baseActivity.showProgress(text)
    }

    override fun hideProgress() {
        baseActivity.hideProgress()
    }

    override fun afterSearch(result: Any?) {
        if(result != null){
            listaPagamentos = result as List<PagamentoDebito>
            itemPagamentoAdapter = ItemPagamentoAdapter(view.context, listaPagamentos.toMutableList())
            val recyclerView = view.recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = itemPagamentoAdapter
            }
        }
    }

    fun buscarParcelas(){
        val task = SelectAllParcelasAsyncTask(CfvApplication.database!!.pagamentoDebitoDAO(), this)
        task.execute(debitoCliente.id)
    }


}