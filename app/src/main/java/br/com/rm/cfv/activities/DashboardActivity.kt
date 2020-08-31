package br.com.rm.cfv.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.balancete.ListaBalanceteActivity
import br.com.rm.cfv.activities.cliente.ListaClientesActivity
import br.com.rm.cfv.activities.contaPagarReceber.compra_venda_produtos.RegistrarCompraVendaActivity
import br.com.rm.cfv.activities.departamento.DepartamentoActivity
import br.com.rm.cfv.activities.estoque.ListaEstoqueActivity
import br.com.rm.cfv.activities.fornecedor.ListaFornecedorActivity
import br.com.rm.cfv.activities.produto.ListaProdutosActivity
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.database.entities.dtos.TotalBalanceteDTO
import br.com.rm.numberUtils.DecimalFormatUtils
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.HashMap

class DashboardActivity : BaseActivity(), IPostExecuteSearch{

    override fun getToobarTitle(): String {
        return getString(R.string.dashboard)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_dashboard)

        val cal = Calendar.getInstance()
        val mes : Int = cal.get(Calendar.MONTH)
        val nomeMes = DateFormatSymbols().months.get(mes)
        val periodo = "${nomeMes} ${cal.get(Calendar.YEAR)}"
        textViewPeriodo.text = periodo
        textViewPeriodoReceber.text = periodo

        setEventListeners()

        hideFab()

        LoadDataAsync(this).execute()
    }

    fun setEventListeners(){

        button_nova_venda.setOnClickListener { v ->
            startActivity(Intent(this, RegistrarCompraVendaActivity::class.java))
        }

        button_estoque.setOnClickListener { v ->
            startActivity(Intent(this, ListaEstoqueActivity::class.java))
        }

        button_clientes.setOnClickListener { v ->
            startActivity(Intent(this, ListaClientesActivity::class.java))
        }

        button_fornecedores.setOnClickListener { v ->
            startActivity(Intent(this, ListaFornecedorActivity::class.java))
        }

        button_produtos.setOnClickListener { v ->
            startActivity(Intent(this, ListaProdutosActivity::class.java))
        }

        button_categorias.setOnClickListener { v ->
            startActivity(Intent(this, DepartamentoActivity::class.java))
        }

        textViewRodape.setOnClickListener{v -> startActivity(Intent(this, ListaBalanceteActivity::class.java))}

        textViewRodapeReceber.setOnClickListener{v -> startActivity(Intent(this, ChartsActivity::class.java))}

        textViewRodapePagar.setOnClickListener{v -> startActivity(Intent(this, ChartsActivity::class.java))}

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun afterSearch(result: Any?) {
        if(result != null) {
            val map = result as Map<String, Any>

            if (map.get("totalReceberData") != null) {
                textViewSaldoReceber.text = getString(
                    R.string.currency_format,
                    DecimalFormatUtils.decimalFormatPtBR((map.get("totalReceberData") as Double))
                )
            }

            if (map.get("totalPagarData") != null) {
                textViewSaldoPagar.text = getString(
                    R.string.currency_format,
                    DecimalFormatUtils.decimalFormatPtBR((map.get("totalPagarData") as Double))
                )
            }

            if (map.get("totalBalancete") != null) {
                textViewBalancete.text = getString(
                    R.string.currency_format,
                    DecimalFormatUtils.decimalFormatPtBR((map.get("totalBalancete") as TotalBalanceteDTO).total())
                )
            }
        }
    }

    class LoadDataAsync(private var iPostExecuteSearch: IPostExecuteSearch) : AsyncTask<Any, Any, Any>(){

        override fun doInBackground(vararg params: Any?): Any {

            val dataFinal: Calendar = if (params.isNotEmpty()) params[0] as Calendar else Calendar.getInstance()

            val dao = CfvApplication.database!!.chartsDAO()

            var map = HashMap<String, Any>()

            val dataInicio: Calendar = Calendar.getInstance()
            dataInicio.set(Calendar.DAY_OF_MONTH, dataInicio.getMinimum(Calendar.DAY_OF_MONTH))
            dataInicio.set(Calendar.HOUR_OF_DAY, 0)
            dataInicio.set(Calendar.MINUTE, 0)
            dataInicio.set(Calendar.SECOND, 0)

            dataFinal.set(Calendar.DAY_OF_MONTH, dataFinal.getMaximum(Calendar.DAY_OF_MONTH))
            dataFinal.set(Calendar.HOUR_OF_DAY, 23)
            dataFinal.set(Calendar.MINUTE, 59)
            dataFinal.set(Calendar.SECOND, 59)

            val totalReceberData = dao.getTotalReceber(dataInicio.timeInMillis, dataFinal.timeInMillis)
            map.put("totalReceberData", totalReceberData)

            val totalPagarData = dao.getTotalPagar(dataInicio.timeInMillis, dataFinal.timeInMillis)
            map.put("totalPagarData", totalPagarData)

            val totalBalancete = CfvApplication.database!!.itemBalanceteDAO()
                .getTotalBalanceteByMesAndAno(dataInicio.get(Calendar.MONTH)+1, dataInicio.get(Calendar.YEAR))
            map.put("totalBalancete", totalBalancete)


            return map
        }

        override fun onPostExecute(result: Any?) {
            super.onPostExecute(result)
            iPostExecuteSearch.afterSearch(result)
        }

    }

}
