package br.com.rm.cfv.activities.contaPagarReceber

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.activities.contaPagarReceber.compra_venda_produtos.RegistrarCompraVendaActivity
import br.com.rm.cfv.activities.receita.CadastrarReceitaDespesaActivity
import br.com.rm.cfv.adapters.cliente.DebitoClienteAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.contaPagarReceber.SelectAllDebitosClienteAsyncTask
import br.com.rm.cfv.constants.TipoReferencia
import br.com.rm.cfv.database.entities.Cliente
import br.com.rm.cfv.database.entities.DefaultReferencia
import br.com.rm.cfv.database.entities.Fornecedor
import br.com.rm.cfv.database.entities.IReferencia
import br.com.rm.cfv.database.entities.dtos.DebitoClienteDTO
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO
import br.com.rm.numberUtils.DecimalFormatUtils
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_lista_debitos_cliente.*
import kotlinx.android.synthetic.main.app_bar_main.*

class ListaContasPagarReceberActivity : BaseActivity(), IPostExecuteSearch{

    override fun getToobarTitle(): String {
        return getString(R.string.listar_clientes_title)
    }

    override fun getReportFileName(): String {
        return referencia.reportFileName()
    }

    override fun getDataSet(): List<Any> {
        return myDataset
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: DebitoClienteAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var myDataset : List<PagamentoDebitoSubtotalDTO> = ArrayList()
    private lateinit var referencia : IReferencia

    companion object{
        val ARG_REFERENCIA = "referencia"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_lista_debitos_cliente)

        referencia = intent.getParcelableExtra<Parcelable>(ARG_REFERENCIA) as IReferencia

        toolbar.title = referencia.getNomeRef()

        viewManager = LinearLayoutManager(this)

        viewAdapter = DebitoClienteAdapter(this, referencia.getTipoRef()!!, myDataset.toMutableList())

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewItens).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        hideFab()
        hideFabOnScroll(recyclerView, fabAdicionar)

        if(referencia.getIdRef()!!.coerceAtLeast(-1) == -1) {
            fabAdicionar.setOnClickListener {
                val intent = Intent(this, CadastrarReceitaDespesaActivity::class.java)
                intent.putExtra(CadastrarReceitaDespesaActivity.ARG_TIPO_REF, referencia.getTipoRef())
                startActivity(intent)
            }
        }else{
            fabAdicionar.setOnClickListener {
                val intent = Intent(this, RegistrarCompraVendaActivity::class.java)
                if(referencia.getTipoRef() == TipoReferencia.CLIENTE){
                    intent.putExtra(RegistrarCompraVendaActivity.ARG_REFERENCIA, referencia as Cliente)
                }else{
                    intent.putExtra(RegistrarCompraVendaActivity.ARG_REFERENCIA, referencia as Fornecedor)
                }
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getAllDebitos()
        if(referencia.getTipoRef() in TipoReferencia.getListDespesas()) {
            nav_view.setCheckedItem(R.id.nav_despesas)
        }else{
            nav_view.setCheckedItem(R.id.nav_receitas)
        }
    }

    override fun getHomeIcon() : Int{
        return R.drawable.ic_menu
    }

    fun getAllDebitos(){
        var task =
            SelectAllDebitosClienteAsyncTask(
                getCfvApplication().getDataBase()!!.contaPagarReceberDAO(),
                this
            )
        task.execute(referencia)
    }

    override fun afterSearch(result: Any?) {
        val dto = result as DebitoClienteDTO
        myDataset = dto.debitos!!
        viewAdapter.setDataset(myDataset.toMutableList())
        textViewTotalValor.text = getString(R.string.currency_format, DecimalFormatUtils.decimalFormatPtBR(dto.getTotalFaltaPagar()))
    }
}
