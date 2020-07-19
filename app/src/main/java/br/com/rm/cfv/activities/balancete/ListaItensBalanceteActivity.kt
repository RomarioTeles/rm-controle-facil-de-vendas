package br.com.rm.cfv.activities.balancete

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.adapters.balancete.BalanceteAdapter
import br.com.rm.cfv.adapters.balancete.ItemBalanceteAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.balancete.SelectAllItemBalanceteAsyncTask
import br.com.rm.cfv.database.entities.Balancete
import br.com.rm.cfv.database.entities.ItemBalancete

class ListaItensBalanceteActivity : BaseActivity(), IPostExecuteSearch{

    override fun getToobarTitle(): String {
        return getString(R.string.listar_itens_balancetes_title)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ItemBalanceteAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var balancete : Balancete
    private var myDataset : List<ItemBalancete> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_lista_default)

        balancete = intent.extras!!.getSerializable("balancete") as Balancete

        if(balancete == null){
            finish()
        }

        viewManager = LinearLayoutManager(this)

        viewAdapter = ItemBalanceteAdapter(this, myDataset)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewItens).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        getAllBalancetes()

        fab().hide()
    }

    fun getAllBalancetes(){
        var task =
            SelectAllItemBalanceteAsyncTask(
                getCfvApplication().getDataBase()!!.itemBalanceteDAO(),
                this
            )
        task.execute(balancete)
    }

    override fun afterSearch(result: Any?) {
        myDataset = result as List<ItemBalancete>
        viewAdapter.setDataset(myDataset)
    }
}
