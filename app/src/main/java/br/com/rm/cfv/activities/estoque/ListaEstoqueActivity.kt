package br.com.rm.cfv.activities.estoque

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.activities.interfaces.IMovimentacaoEstoque
import br.com.rm.cfv.adapters.estoque.EstoqueAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.estoque.InsertEstoqueAsyncTask
import br.com.rm.cfv.asyncTasks.estoque.SelectAllEstoqueAsyncTask
import br.com.rm.cfv.constants.MotivoMovimentacao
import br.com.rm.cfv.constants.TipoMovimentacaoEstoque
import br.com.rm.cfv.database.entities.MovimentacaoEstoque
import br.com.rm.cfv.database.entities.dtos.EstoqueDTO
import kotlinx.android.synthetic.main.activity_lista_estoque.*
import java.util.*


class ListaEstoqueActivity : BaseActivity(), IPostExecuteSearch, IMovimentacaoEstoque, IPostExecuteInsertAndUpdate {

    override fun getToobarTitle(): String {
        return getString(R.string.listar_estoques_title)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: EstoqueAdapter
    private var estoque: EstoqueDTO? = null
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var myDataset : List<EstoqueDTO> = ArrayList()
    private lateinit var listaMotivos : Array<String>
    private lateinit var listaTipoMovimentacao : Array<String>
    private lateinit var adaptertipoMovimentacao : ArrayAdapter<String>
    private lateinit var adapterMotivos : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_estoque)
        viewManager = LinearLayoutManager(this)

        viewAdapter = EstoqueAdapter(this, myDataset)

       listaTipoMovimentacao = TipoMovimentacaoEstoque.values()
       listaMotivos = MotivoMovimentacao.values()

        adaptertipoMovimentacao = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaTipoMovimentacao)

        adapterMotivos = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaMotivos)

        spinner_motivo.adapter = adapterMotivos

        spinner_tipo_movimentacao.adapter = adaptertipoMovimentacao

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewItens).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        framelayout_movimentar_estoque.visibility = View.GONE

        getAllProdutos()

        hideFab()

        addClickEvents()
    }

    private fun addClickEvents() {

        buttonCancel.setOnClickListener{
            estoque = null
            framelayout_movimentar_estoque.animate().setDuration(400)
                .alpha(0.0F)
                .withEndAction {
                    framelayout_movimentar_estoque.visibility = View.GONE
                    framelayout_movimentar_estoque.alpha = 1.0F
                }
                .start()
        }

        buttonAdd.setOnClickListener{
            confirmarMovimentacao()
        }


    }

    fun getAllProdutos(){
        var task =
            SelectAllEstoqueAsyncTask(
                getCfvApplication().getDataBase()!!.movimentacaoEstoqueDAO(),
                this
            )
        task.execute()
    }

    override fun afterSearch(result: Any?) {
        myDataset = result as List<EstoqueDTO>
        viewAdapter.setDataset(myDataset)
    }

    override fun preparaMovimentacao(estoque: EstoqueDTO) {
       this.estoque = estoque
        framelayout_movimentar_estoque.alpha = 0.0F
        framelayout_movimentar_estoque.visibility = View.VISIBLE
        framelayout_movimentar_estoque.animate().setDuration(600)
            .alpha(1.0F)
            .start()

    }

    fun confirmarMovimentacao() {
        var isValid = true
        val motivo = spinner_motivo.selectedItem as String
        val tipo = spinner_tipo_movimentacao.selectedItem as String
        var quantidade = textViewQuantidade.text.toString()
        if(quantidade.isBlank()){
            isValid = false
            textInputLayoutQuantidade.error = getString(R.string.mensagem_campo_requerido)
        }
        if(motivo.isBlank()){
            isValid = false
        }
        if(tipo.isBlank()){
            isValid = false
        }
        if(isValid) {
            textInputLayoutQuantidade.error = null
            var movimentacao = MovimentacaoEstoque(
                null, estoque!!.codigo!!,
                motivo,
                Date().time,
                tipo,
                quantidade.toInt()
            )

            InsertEstoqueAsyncTask(getCfvApplication().getDataBase()!!.movimentacaoEstoqueDAO(), this).execute(movimentacao)

        }
    }

    override fun afterInsert(result: Any?) {

        val success = result as Boolean

        if(success) {
            getAllProdutos()
        }else{
            Toast.makeText(this, "Erro ao inserir registro.", Toast.LENGTH_LONG).show()
        }
        textViewQuantidade.text = null
        framelayout_movimentar_estoque.visibility = View.GONE
    }

    override fun afterUpdate(result: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
