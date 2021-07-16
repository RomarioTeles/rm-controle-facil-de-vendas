package br.com.rm.cfv.activities.contaPagarReceber.viewModel

import androidx.recyclerview.widget.LinearLayoutManager
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.adapters.produto.IOnClickItemProdutoListener
import br.com.rm.cfv.adapters.produto.ItemProdutoAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.contaPagarReceber.SelectAllLancamentosAsyncTask
import br.com.rm.cfv.database.entities.ItemProduto
import kotlinx.android.synthetic.main.fragment_visualizar_debito.view.*

open class LancamentosViewModel : PageViewModel(), IPostExecuteSearch, IOnClickItemProdutoListener {

    override fun onItemProdutoClick(item: ItemProduto) {

    }

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
           itemProdutoAdapter = ItemProdutoAdapter(view.context, this, listaLancamentos.toMutableList())
           view.recyclerView.apply {
               setHasFixedSize(true)
               layoutManager = LinearLayoutManager(context)
               adapter = itemProdutoAdapter
           }
       }
    }

    fun buscarLancamentos(){
        val task = SelectAllLancamentosAsyncTask(CfvApplication.database!!.itemProdutoDAO(), this)
        task.execute(pagamentoDebitoSubtotal.id)
    }

}