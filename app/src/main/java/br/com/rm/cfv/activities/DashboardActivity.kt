package br.com.rm.cfv.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
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
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_dashboard.button_data
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.HashMap

class DashboardActivity : BaseActivity(), IPostExecuteSearch{

    private var ano : Int? = null
    private var mes : Int? = null

    override fun getToobarTitle(): String {
        return getString(R.string.dashboard)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_dashboard)

        val cal = Calendar.getInstance()
        ano = cal.get(Calendar.YEAR)
        mes = cal.get(Calendar.MONTH)

        setLabelButtonPeriodo(mes!!, ano!!)

        setEventListeners()

        hideFab()

        button_data.setOnClickListener { view ->
            abrirDialogAnoMesPicker()
        }

    }

    override fun onResume() {
        super.onResume()
        nav_view.setCheckedItem(R.id.nav_main)
        LoadDataAsync(this).execute()
    }

    override fun getHomeIcon() : Int{
        return R.drawable.ic_menu
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

    private fun abrirDialogAnoMesPicker(){
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
        val inflater = this.layoutInflater

        val cal = Calendar.getInstance()

        var dialog = inflater.inflate(R.layout.ano_mes_picker, null)
        val monthPicker = dialog.findViewById<NumberPicker>(R.id.numberpicker_mes)
        val yearPicker = dialog.findViewById<NumberPicker>(R.id.numberpicker_ano)

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = mes!!.plus(1)

        var year = cal.get(Calendar.YEAR)
        yearPicker.minValue = year - 5
        yearPicker.maxValue = year
        yearPicker.value = ano!!

        monthPicker.setOnValueChangedListener { picker, oldVal, newVal -> mes = newVal.minus(1)  }

        yearPicker.setOnValueChangedListener { picker, oldVal, newVal -> ano = newVal }

        builder.setView(dialog).setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            Log.i("Positive Button", which.toString())
            val cal = Calendar.getInstance()

            cal.set(Calendar.YEAR, ano!!)
            cal.set(Calendar.MONTH, mes!!)

            setLabelButtonPeriodo(mes!!, ano!!)

            LoadDataAsync(this).execute(cal)

        })

        builder.show()
    }

    private fun setLabelButtonPeriodo( mes : Int, ano : Int){
        button_data.text = "${DateFormatSymbols.getInstance().months.get(mes)} ${ano}".toUpperCase()
    }

    class LoadDataAsync(private var iPostExecuteSearch: IPostExecuteSearch) : AsyncTask<Any, Any, Any>(){

        override fun doInBackground(vararg params: Any?): Any {

            val cal: Calendar = if (params.isNotEmpty()) params[0] as Calendar else Calendar.getInstance()

            val dao = CfvApplication.database!!.chartsDAO()

            var map = HashMap<String, Any>()

            val dataInicio: Calendar = Calendar.getInstance()
            dataInicio.set(Calendar.MONTH, cal.get(Calendar.MONTH))
            dataInicio.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DAY_OF_MONTH))
            dataInicio.set(Calendar.HOUR_OF_DAY, 0)
            dataInicio.set(Calendar.MINUTE, 0)
            dataInicio.set(Calendar.SECOND, 0)

            cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DAY_OF_MONTH))
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)

            val totalReceberData = dao.getTotalReceber(dataInicio.timeInMillis, cal.timeInMillis)
            map.put("totalReceberData", totalReceberData)

            val totalPagarData = dao.getTotalPagar(dataInicio.timeInMillis, cal.timeInMillis)
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
