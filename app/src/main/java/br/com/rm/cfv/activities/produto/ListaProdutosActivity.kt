package br.com.rm.cfv.activities.produto

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.adapters.produto.ProdutoAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.produto.SelectAllProdutosAsyncTask
import br.com.rm.cfv.database.entities.Produto
import br.com.rm.cfv.R
import kotlinx.android.synthetic.main.app_bar_main.*

class ListaProdutosActivity : BaseActivity(), IPostExecuteSearch{

    override fun getToobarTitle(): String {
        return getString(R.string.listar_produtos_title)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ProdutoAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var myDataset : List<Produto> = ArrayList<Produto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_default)
        toolbar.title = "Listar Produtos"
        viewManager = LinearLayoutManager(this)

        viewAdapter = ProdutoAdapter(this, myDataset)

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

        fab().setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, CadastrarProdutoActivity::class.java))
        })
    }

    fun getAllProdutos(){
        var task =
            SelectAllProdutosAsyncTask(
                getCfvApplication().getDataBase()!!.produtoDAO(),
                this
            )
        task.execute()
    }

    override fun afterSearch(result: Any?) {
        myDataset = result as List<Produto>
        viewAdapter.setDataset(myDataset)
    }
}
