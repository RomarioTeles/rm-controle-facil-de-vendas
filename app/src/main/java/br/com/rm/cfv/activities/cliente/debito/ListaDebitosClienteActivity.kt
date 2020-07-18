package br.com.rm.cfv.activities.cliente.debito

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.adapters.cliente.DebitoClienteAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.debitoCliente.SelectAllDebitosClienteAsyncTask
import br.com.rm.cfv.database.entities.Cliente
import br.com.rm.cfv.database.entities.dtos.DebitoClienteDTO
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO
import br.com.rm.numberUtils.DecimalFormatUtils
import kotlinx.android.synthetic.main.activity_lista_debitos_cliente.*
import kotlinx.android.synthetic.main.app_bar_main.*

class ListaDebitosClienteActivity : BaseActivity(), IPostExecuteSearch{

    override fun getToobarTitle(): String {
        return getString(R.string.listar_clientes_title)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: DebitoClienteAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var myDataset : List<PagamentoDebitoSubtotalDTO> = ArrayList()
    private lateinit var cliente : Cliente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_debitos_cliente)

        cliente = intent.getSerializableExtra("cliente") as Cliente

        toolbar.title = cliente.nome

        viewManager = LinearLayoutManager(this)

        viewAdapter = DebitoClienteAdapter(this, myDataset.toMutableList())

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

        hideFab()
    }

    fun getAllClientes(){
        var task =
            SelectAllDebitosClienteAsyncTask(
                getCfvApplication().getDataBase()!!.debitoClienteDAO(),
                this
            )
        task.execute(cliente.uid)
    }

    override fun afterSearch(result: Any?) {
        val dto = result as DebitoClienteDTO
        myDataset = dto.debitos!!
        viewAdapter.setDataset(myDataset.toMutableList())
        textViewTotalValor.text = "R$ "+ DecimalFormatUtils.decimalFormatPtBR(dto.getTotalFaltaPagar())
    }
}