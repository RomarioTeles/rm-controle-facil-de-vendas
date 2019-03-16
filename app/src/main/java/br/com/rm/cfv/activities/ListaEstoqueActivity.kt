package br.com.rm.cfv.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.activities.interfaces.IMovimentacaoEstoque
import br.com.rm.cfv.activities.produto.CadastrarProdutoActivity
import br.com.rm.cfv.adapters.estoque.EstoqueAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.estoque.InsertEstoqueAsyncTask
import br.com.rm.cfv.asyncTasks.estoque.SelectAllEstoqueAsyncTask
import br.com.rm.cfv.constants.MotivoMovimentacao
import br.com.rm.cfv.constants.TipoMovimentacaoEstoque
import br.com.rm.cfv.database.entities.MovimentacaoEstoque
import br.com.rm.cfv.database.entities.dtos.EstoqueDTO
import com.rm.cfv.R
import kotlinx.android.synthetic.main.activity_lista_estoque.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*

class ListaEstoqueActivity : BaseActivity(), IPostExecuteSearch, IMovimentacaoEstoque, IPostExecuteInsertAndUpdate {

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
        toolbar.title = getString(R.string.listar_produtos_title)
        viewManager = LinearLayoutManager(this)

        viewAdapter = EstoqueAdapter(this, myDataset)

       listaTipoMovimentacao = TipoMovimentacaoEstoque.values()
       listaMotivos = MotivoMovimentacao.values()

        adaptertipoMovimentacao = ArrayAdapter(this, android.R.layout.select_dialog_item, listaTipoMovimentacao)

        adapterMotivos = ArrayAdapter(this, android.R.layout.select_dialog_item, listaMotivos)

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
            framelayout_movimentar_estoque.visibility = View.GONE
            estoque = null
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
        framelayout_movimentar_estoque.visibility = View.VISIBLE
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

        framelayout_movimentar_estoque.visibility = View.GONE
    }

    override fun afterUpdate(result: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}