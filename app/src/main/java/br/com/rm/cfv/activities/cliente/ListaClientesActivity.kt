package br.com.rm.cfv.activities.cliente

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.adapters.cliente.ClienteAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.cliente.SelectAllClientesAsyncTask
import br.com.rm.cfv.database.entities.Cliente
import br.com.rm.cfv.R

class ListaClientesActivity : BaseActivity(), IPostExecuteSearch{

    override fun getToobarTitle(): String {
        return getString(R.string.listar_clientes_title)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ClienteAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var myDataset : List<Cliente> = ArrayList<Cliente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_default)

        viewManager = LinearLayoutManager(this)

        viewAdapter = ClienteAdapter(myDataset)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewItens).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        getAllClientes()

        fab().setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, CadastrarClienteActivity::class.java))
        })
    }

    fun getAllClientes(){
        var task =
            SelectAllClientesAsyncTask(
                getCfvApplication().getDataBase()!!.clienteDAO(),
                this
            )
        task.execute()
    }

    override fun afterSearch(result: Any?) {
        myDataset = result as List<Cliente>
        viewAdapter.setDataset(myDataset)
    }
}
