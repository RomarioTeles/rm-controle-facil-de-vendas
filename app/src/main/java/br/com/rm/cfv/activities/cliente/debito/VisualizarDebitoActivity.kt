package br.com.rm.cfv.activities.cliente.debito

import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.database.entities.DebitoCliente
import br.com.rm.numberUtils.DecimalFormatUtils
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_visualizar_debito.*

class VisualizarDebitoActivity : BaseActivity() {

    companion object{
        const val ARG_DEBITO_CLIENTE = "DEBITO_CLIENTE"
    }

    override fun getToobarTitle(): String {
        return "Visualizar Débito"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_debito)
        hideFab()
        val debitoCliente = intent.getParcelableExtra<DebitoCliente>(ARG_DEBITO_CLIENTE)

        if(debitoCliente == null){
            finish()
        }else {
            Log.d("DEBITO", debitoCliente.toString())
            textViewTotalValor.text = DecimalFormatUtils.decimalFormatPtBR(debitoCliente.total)
            val sectionsPagerAdapter = VisualizarDebitoPagerAdapter(
                this,
                debitoCliente,
                supportFragmentManager
            )
            val viewPager: ViewPager = findViewById(R.id.view_pager)
            viewPager.adapter = sectionsPagerAdapter
            val tabs: TabLayout = findViewById(R.id.tabs)
            tabs.setupWithViewPager(viewPager)
        }
    }
}