package br.com.rm.cfv.activities.balancete

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.adapters.balancete.BalanceteAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.balancete.SelectAllBalancetesCoroutine
import br.com.rm.cfv.database.entities.Balancete
import kotlinx.android.synthetic.main.activity_base.*

class ListaBalanceteActivity : BaseActivity(), IPostExecuteSearch{

    override fun getToobarTitle(): String {
        return getString(R.string.listar_balancetes)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: BalanceteAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var myDataset : List<Balancete> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_lista_default)

        viewManager = LinearLayoutManager(this)

        viewAdapter = BalanceteAdapter(this, myDataset)

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

    override fun onResume() {
        super.onResume()
        nav_view.setCheckedItem(R.id.nav_balancete)
    }

    override fun getHomeIcon() : Int{
        return R.drawable.ic_menu
    }

    fun getAllBalancetes(){
        var task =
            SelectAllBalancetesCoroutine(
                getCfvApplication().getDataBase()!!.balanceteDAO(),
                this
            )
        task.execute()
    }

    override fun afterSearch(result: Any?) {
        myDataset = result as List<Balancete>
        viewAdapter.setDataset(myDataset)
    }
}
