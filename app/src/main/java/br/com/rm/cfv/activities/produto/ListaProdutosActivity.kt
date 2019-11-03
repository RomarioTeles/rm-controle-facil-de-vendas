package br.com.rm.cfv.activities.produto

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.ImageUtilsActivity
import br.com.rm.cfv.adapters.produto.ProdutoAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.produto.SelectAllProdutosAsyncTask
import br.com.rm.cfv.database.entities.Produto

class ListaProdutosActivity : ImageUtilsActivity(), IPostExecuteSearch, IOnClickProdutoListener {

    override fun getToobarTitle(): String {
        return getString(R.string.listar_produtos_title)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ProdutoAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var myDataset : List<Produto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_default)
        viewManager = LinearLayoutManager(this)

        viewAdapter = ProdutoAdapter(this, this, myDataset)

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

    override fun onPostCaptureCompleted(bitmap: Bitmap?) {}

    override fun getCaptureTrigger(): View? {
        return null
    }

    override fun onProdutoClick(produto: Produto, isLongClick: Boolean) {
        var intent = Intent(this, CadastrarProdutoActivity::class.java)
        intent.putExtra("produto", produto)
        startActivity(intent)
    }
}
