package br.com.rm.cfv.activities.fornecedor

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.adapters.fornecedor.FornecedorAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.fornecedor.SelectAllFornecedorsAsyncTask
import br.com.rm.cfv.database.entities.Fornecedor

class ListaFornecedorActivity : BaseActivity(), IPostExecuteSearch{

    override fun getToobarTitle(): String {
        return getString(R.string.listar_fornecedores_title)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: FornecedorAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var myDataset : List<Fornecedor> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_lista_default)

        viewManager = LinearLayoutManager(this)

        viewAdapter = FornecedorAdapter(this, myDataset)

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

    fun getAllFornecedors(){
        var task =
            SelectAllFornecedorsAsyncTask(
                getCfvApplication().getDataBase()!!.fornecedorDAO(),
                this
            )
        task.execute()
    }

    override fun afterSearch(result: Any?) {
        myDataset = result as List<Fornecedor>
        viewAdapter.setDataset(myDataset)
    }
}
