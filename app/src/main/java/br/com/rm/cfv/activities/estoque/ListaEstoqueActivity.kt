package br.com.rm.cfv.activities.estoque

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewStub
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_base.*
import java.util.*


class ListaEstoqueActivity : BaseActivity(), IPostExecuteSearch, IMovimentacaoEstoque, IPostExecuteInsertAndUpdate {

    override fun getToobarTitle(): String {
        return getString(R.string.listar_estoques_title)
    }

    override fun getReportFileName(): String {
        return "estoque_produtos.csv"
    }

    override fun getDataSet(): List<Any> {
        return myDataset
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
    private lateinit var viewStub: ViewStub

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_estoque)

        viewStub = findViewById(R.id.viewStub)
        val viewStubView = viewStub.inflate()
        viewStubView.findViewById<TextView>(R.id.textViewHint).text = getString(R.string.estoque_produtos_nao_cadastrados_hint)
        viewStub.visibility = View.GONE

        viewManager = GridLayoutManager(this, 1)

        viewAdapter = EstoqueAdapter(this, myDataset)

       listaTipoMovimentacao = TipoMovimentacaoEstoque.values()

       listaMotivos = MotivoMovimentacao.values()

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewItens).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        getAllProdutos()

        hideFab()

    }

    override fun onResume() {
        super.onResume()
        nav_view.setCheckedItem(R.id.nav_estoque)
    }

    override fun getHomeIcon() : Int{
        return R.drawable.ic_menu
    }

    fun openDialog(){
        val view = getLayoutInflater().inflate(R.layout.estoque_bottom_seet, null)
        var dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        adaptertipoMovimentacao = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaTipoMovimentacao)

        adapterMotivos = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaMotivos)

        view.findViewById<Spinner>(R.id.spinner_motivo).adapter = adapterMotivos

        view.findViewById<Spinner>(R.id.spinner_tipo_movimentacao).adapter = adaptertipoMovimentacao

        view.findViewById<Button>(R.id.buttonCancel).setOnClickListener{
            estoque = null
            dialog.dismiss()
        }

        view.findViewById<Button>(R.id.buttonAdd).setOnClickListener{
            confirmarMovimentacao(view)
            dialog.dismiss()
        }

        dialog.show()
    }

    fun getAllProdutos(query: String? = null, showProgress: Boolean = true){
        var task =
            SelectAllEstoqueAsyncTask(
                getCfvApplication().getDataBase()!!.movimentacaoEstoqueDAO(),
                this, showProgress
            )
        task.execute(query)
    }

    override fun afterSearch(result: Any?) {
        myDataset = result as List<EstoqueDTO>
        viewAdapter.setDataset(myDataset)
        if(myDataset == null || myDataset.isEmpty()){
            viewStub.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }else{
            viewStub.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun preparaMovimentacao(estoque: EstoqueDTO) {
       this.estoque = estoque
        openDialog()
    }

    fun confirmarMovimentacao(view : View) {
        var isValid = true
        val motivo = view.findViewById<Spinner>(R.id.spinner_motivo).selectedItem as String
        val tipo = view.findViewById<Spinner>(R.id.spinner_tipo_movimentacao).selectedItem as String
        var quantidade = view.findViewById<TextView>(R.id.textViewQuantidade).text.toString()
        if(quantidade.isBlank()){
            isValid = false
            view.findViewById<TextInputLayout>(R.id.textInputLayoutQuantidade).error = getString(R.string.mensagem_campo_requerido)
        }
        if(motivo.isBlank()){
            isValid = false
        }
        if(tipo.isBlank()){
            isValid = false
        }
        if(isValid) {
            view.findViewById<TextInputLayout>(R.id.textInputLayoutQuantidade).error = null
            var qtdInserir = quantidade.toInt()
            if(estoque!!.quantidade() < 0){
                qtdInserir += Math.abs(estoque!!.quantidade())
            }

            var movimentacao = MovimentacaoEstoque(
                null, estoque!!.codigo!!,
                motivo,
                Date().time,
                tipo,
                qtdInserir
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
    }

    override fun afterUpdate(result: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        getMenuInflater().inflate(R.menu.menu_search,menu)

        var menuItem : MenuItem = menu.findItem(R.id.searchView)

        var searchView = menuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String) : Boolean {

                getAllProdutos(newText, false)

                return true
            }
        })

        return true
    }
}
