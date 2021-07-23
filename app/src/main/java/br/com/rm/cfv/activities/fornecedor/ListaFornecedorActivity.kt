package br.com.rm.cfv.activities.fornecedor

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewStub
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.adapters.fornecedor.FornecedorAdapter
import br.com.rm.cfv.adapters.fornecedor.FornecedorSelectableAdapter
import br.com.rm.cfv.adapters.fornecedor.FornecedorViewHolder
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.fornecedor.SelectAllFornecedorsAsyncTask
import br.com.rm.cfv.database.entities.Fornecedor
import kotlinx.android.synthetic.main.activity_base.*

class ListaFornecedorActivity : BaseActivity(), IPostExecuteSearch{

    override fun getToobarTitle(): String {
        return getString(R.string.listar_fornecedores_title)
    }

    override fun getReportFileName(): String {
        return "fornecedores.csv"
    }

    override fun getDataSet(): List<Any> {
        return myDataset
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<FornecedorViewHolder>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewStub: ViewStub
    private var selectable = false
    private var myDataset : List<Fornecedor> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_lista_default)

        viewStub = findViewById(R.id.viewStub)
        viewStub.inflate()
        viewStub.visibility = View.GONE

        viewManager = LinearLayoutManager(this)

        if(intent.hasExtra("SELECTABLE")) {
            selectable = intent.extras!!.getBoolean("SELECTABLE")
        }

        if(selectable){
            viewAdapter = FornecedorSelectableAdapter(this, myDataset.toMutableList())
        }else{
            viewAdapter = FornecedorAdapter(this, getCfvApplication().getDataBase()!!.fornecedorDAO(), myDataset.toMutableList())
        }

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewItens).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        getAllFornecedors()

        fab().setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, CadastrarFornecedorActivity::class.java))
        })
    }

    override fun onResume() {
        super.onResume()
        nav_view.setCheckedItem(R.id.nav_fornecedores)
    }

    override fun getHomeIcon() : Int{
        return R.drawable.ic_menu
    }

    fun getAllFornecedors(query: String?  = null, showProgress: Boolean = true){
        var task =
            SelectAllFornecedorsAsyncTask(
                getCfvApplication().getDataBase()!!.fornecedorDAO(),
                this, showProgress
            )
        task.execute(query)
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

                getAllFornecedors(newText, false)

                return true
            }
        })

        return true
    }

    override fun afterSearch(result: Any?) {
        myDataset = result as List<Fornecedor>

        if(selectable) {
            (viewAdapter as FornecedorSelectableAdapter).setDataset(myDataset.toMutableList())
        }else{
            (viewAdapter as FornecedorAdapter).setDataset(myDataset.toMutableList())
        }

        if(myDataset == null || myDataset.isEmpty()){
            viewStub.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }else{
            viewStub.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}
