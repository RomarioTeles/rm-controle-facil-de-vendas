package br.com.rm.cfv.activities.contaPagarReceber

import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.activities.contaPagarReceber.contaPagarReceberViewer.VisualizarContaPagarReceberPagerAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.contaPagarReceber.SelectValorPagarByPagamentoDebitoIdAsyncTask
import br.com.rm.cfv.constants.TipoReferencia
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO
import br.com.rm.numberUtils.DecimalFormatUtils
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_visualizar_debito.*

class VisualizarContaPagarReceberActivity : BaseActivity(), IPostExecuteSearch{

    var debitoCliente : PagamentoDebitoSubtotalDTO? = null

    companion object{
        const val ARG_DEBITO_CLIENTE = "DEBITO_CLIENTE"
    }

    override fun getToobarTitle(): String {
        if (debitoCliente != null){
            if(debitoCliente!!.tipoRef == TipoReferencia.RECEITAS.name){
                return getString(R.string.title_visualizar_receitas)
            }
        }
        return getString(R.string.title_visualizar_debitos)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_debito)
        hideFab()
        debitoCliente = intent.getParcelableExtra(ARG_DEBITO_CLIENTE)!!

        if(debitoCliente == null){
            finish()
        }else {
            Log.d("DEBITO", debitoCliente.toString())
            textViewTotalValor.text = DecimalFormatUtils.decimalFormatPtBR(debitoCliente!!.total)
            val sectionsPagerAdapter =
                VisualizarContaPagarReceberPagerAdapter(
                    this,
                    debitoCliente!!,
                    supportFragmentManager
                )
            val viewPager: ViewPager = findViewById(R.id.view_pager)
            viewPager.adapter = sectionsPagerAdapter
            val tabs: TabLayout = findViewById(R.id.tabs)
            tabs.setupWithViewPager(viewPager)

        }
    }

    override fun onResume() {
        super.onResume()
        SelectValorPagarByPagamentoDebitoIdAsyncTask(getCfvApplication().getDataBase()!!.pagamentoDebitoDAO(), this).execute(debitoCliente)
    }

    override fun afterSearch(result: Any?) {
        if(result != null){
            var dto = result as PagamentoDebitoSubtotalDTO
            textViewTotalValor.text = getString(R.string.currency_format, DecimalFormatUtils.decimalFormatPtBR(dto.total))
            textViewTotalValorPago.text = getString(R.string.currency_format_negative, DecimalFormatUtils.decimalFormatPtBR(dto.valorPago))
            textViewTotalValorTotal.text = getString(R.string.currency_format, DecimalFormatUtils.decimalFormatPtBR(dto.getValorFaltaPagar()))
        }
    }
}