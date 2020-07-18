package br.com.rm.cfv.activities.cliente.debito

import androidx.recyclerview.widget.LinearLayoutManager
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.adapters.produto.ItemProdutoAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.debitoCliente.SelectAllLancamentosAsyncTask
import br.com.rm.cfv.database.entities.ItemProduto
import kotlinx.android.synthetic.main.fragment_visualizar_debito.view.*

open class LancamentosDebitoViewModel : PageViewModel(), IPostExecuteSearch {

    lateinit var listaLancamentos : List<ItemProduto>
    lateinit var itemProdutoAdapter : ItemProdutoAdapter

    override fun showProgress(text: String) {
        baseActivity.showProgress(text)
    }

    override fun hideProgress() {
        baseActivity.hideProgress()
    }

    override fun afterSearch(result: Any?) {
       if(result != null){
           listaLancamentos = result as List<ItemProduto>
           itemProdutoAdapter = ItemProdutoAdapter(view.context, listaLancamentos.toMutableList())
           val recyclerView = view.recyclerView.apply {
               setHasFixedSize(true)
               layoutManager = LinearLayoutManager(context)
               adapter = itemProdutoAdapter
           }
       }
    }

    fun buscarLancamentos(){
        val task = SelectAllLancamentosAsyncTask(CfvApplication.database!!.itemProdutoDAO(), this)
        task.execute(debitoCliente.id)
    }

}