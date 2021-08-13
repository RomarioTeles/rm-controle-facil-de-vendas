package br.com.rm.cfv.activities.produto

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewStub
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.ImageUtilsActivity
import br.com.rm.cfv.adapters.produto.ProdutoAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteDelete
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.produto.DeleteProdutoAsyncTask
import br.com.rm.cfv.asyncTasks.produto.SelectAllProdutosAsyncTask
import br.com.rm.cfv.bottomsheets.BottomSheetDialogSettings
import br.com.rm.cfv.bottomsheets.IBottomSheetOptions
import br.com.rm.cfv.bottomsheets.ItemOptionsBottomSheetDialog
import br.com.rm.cfv.database.entities.Produto
import br.com.rm.cfv.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_base.*


class ListaProdutosActivity : ImageUtilsActivity(), IPostExecuteSearch, IOnClickProdutoListener, IBottomSheetOptions, IPostExecuteDelete{

    override fun getReportFileName(): String {
        return "produtos.csv"
    }

    override fun getDataSet(): List<Any> {
        return myDataset
    }

    override fun getToobarTitle(): String {
        return getString(R.string.listar_produtos_title)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ProdutoAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewStub: ViewStub
    private var myDataset : MutableList<Produto> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_default)

        viewStub = findViewById(R.id.viewStub)
        viewStub.inflate()
        viewStub.visibility = View.GONE

        viewManager = GridLayoutManager(this, 1)

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

        hideFabOnScroll(recyclerView)

        getAllProdutos()

        fab().setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, CadastrarProdutoActivity::class.java))
        })
    }

    override fun onResume() {
        super.onResume()
        nav_view.setCheckedItem(R.id.nav_produto)
    }

    override fun getHomeIcon() : Int{
        return R.drawable.ic_menu
    }

    fun getAllProdutos(query: String? = "", showProgress: Boolean = true){
        val task =
            SelectAllProdutosAsyncTask(
                getCfvApplication().getDataBase()!!.produtoDAO(),
                this, showProgress
            )
        task.execute(query)
    }

    override fun afterSearch(result: Any?) {
        myDataset = result as MutableList<Produto>
        viewAdapter.setDataset(myDataset)
        if(myDataset == null || myDataset.isEmpty()){
            viewStub.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }else{
            viewStub.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onPostCaptureCompleted(bitmap: Bitmap?, path: String) {}

    override fun getCaptureTrigger(): View? {
        return null
    }

    override fun onProdutoClick(produto: Produto,  position: Int?, isLongClick: Boolean) {
        val settings = BottomSheetDialogSettings(
            produto.nome,
            false,
            true,
            false,
            true
        )

        ItemOptionsBottomSheetDialog().openDialog( this, produto, position, settings, this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_search,menu)

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

    override fun buttonSheetLista(item: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun buttonSheetAdiciona(item: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun buttonSheetRemove(item: Any?, position: Int) {
        DeleteProdutoAsyncTask(getCfvApplication().getDataBase()!!.produtoDAO(), this)
            .execute((item as Produto).uid, position)
    }

    override fun buttonSheetEdita(item: Any?) {
        var intent = Intent(this, CadastrarProdutoActivity::class.java)
        intent.putExtra("produto", item as Produto)
        startActivity(intent)
    }

    override fun afterDelete(result: Any?) {
        if(result as Int > -1) {
            ToastUtils.showToast(
                this,
                getString(R.string.mensagem_sucesso)
            )
            myDataset.removeAt(result)
            viewAdapter.notifyDataSetChanged()
        }else{
            ToastUtils.showToastError(
                this,
                getString(R.string.mensagem_erro)
            )
        }
    }
}
