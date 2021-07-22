package br.com.rm.cfv.activities.dashboard

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.activities.interfaces.ILoadReportData
import br.com.rm.cfv.activities.reports.ReportsActivity
import br.com.rm.cfv.activities.SelectPeriodoViewModel
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.constants.MeioPagamento
import br.com.rm.cfv.constants.TipoPagamento
import br.com.rm.cfv.database.entities.dtos.BarChartItem
import br.com.rm.cfv.database.entities.dtos.ChartGrupoValor
import br.com.rm.cfv.database.entities.dtos.TotalBalanceteDTO
import br.com.rm.cfv.utils.charts.BarChartUtil
import br.com.rm.cfv.utils.charts.PieChartUtil
import br.com.rm.cfv.utils.charts.common.BarChartDataSet
import br.com.rm.cfv.utils.charts.common.MonthAxisValueFormatter
import br.com.rm.cfv.utils.charts.common.MyValueFormatter
import br.com.rm.numberUtils.DecimalFormatUtils
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.select_periodo_card.*
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class DashboardActivity : BaseActivity() , IPostExecuteSearch, ILoadReportData {

    private var firstLoad : Boolean = true

    lateinit var selectDataViewModel: SelectPeriodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_dashboard)

        var date = LocalDate.now()
        var defaultMes = date.month.value
        var defaultAno = date.year

        selectDataViewModel = ViewModelProvider(this).get(SelectPeriodoViewModel::class.java).apply {
            mes = intent.getIntExtra(ReportsActivity.ARG_MES, defaultMes)
            ano = intent.getIntExtra(ReportsActivity.ARG_ANO, defaultAno)
            buttonData = findViewById(R.id.button_data)
            baseActivity = this@DashboardActivity
            iLoadReportData = this@DashboardActivity
        }

        selectDataViewModel.init()

        fab.hide()
    }

    override fun onResume() {
        super.onResume()
        execute(selectDataViewModel.mes!!, selectDataViewModel.ano!!)
    }

    @SuppressLint("SetTextI18n")
    private fun setLabelButtonPeriodo(mes : Int, ano : Int){
        button_data.text = "${DateFormatSymbols.getInstance().months.get(mes)} ${ano}".toUpperCase()
    }

    override fun getToobarTitle(): String {
        return "Dashboard"
    }

    override fun getHomeIcon() : Int{
        return R.drawable.ic_menu
    }

    override fun afterSearch(result: Any?) {

        if(result != null){
            val map = result as Map<String, Any>

            textViewBalancetePorcento.text = ""

            if (map.get("totalBalancete") != null) {


                var totalbalancete = (map.get("totalBalancete") as TotalBalanceteDTO)

                textViewBalancete.text = getString(
                    R.string.currency_format,
                    DecimalFormatUtils.decimalFormatPtBR(totalbalancete.total())
                )

                if(totalbalancete.total() <= 0){
                    textViewBalancete.setTextColor(getColor(R.color.margentaColor))
                }

                textViewTotalReceitas.text = getString(
                    R.string.currency_format,
                    DecimalFormatUtils.decimalFormatPtBR(totalbalancete.totalReceitas)
                )

                textViewTotalDespesas.text = getString(
                    R.string.currency_format,
                    DecimalFormatUtils.decimalFormatPtBR(totalbalancete.totalDespesas)
                )

                if(totalbalancete.totalReceitas!!.compareTo(0) == 1) {
                    var percent =
                        (totalbalancete.total()!!.div(totalbalancete.totalReceitas!!)).times(100)

                    textViewBalancetePorcento.text = DecimalFormatUtils.decimalFormat(percent, 0 ,0) + "%"
                    progress_balancete.progress = percent.toInt()
                }
            }

            val totalMeioPagData = (map.get("totalMeioPagData") as List<ChartGrupoValor>)
            if(totalMeioPagData != null && !totalMeioPagData.isEmpty()){
                val entries = LinkedHashMap<String, Float>()
                totalMeioPagData.forEach { valor  ->
                    entries.put( MeioPagamento.valueOf(valor.grupo!!).descricao, valor.total.toFloat())
                }
                createPieChart(getString(R.string.chart_meio_pagamento), R.id.chart_total_meio_pag, entries)
            }else{
                val entries = mapOf(getString(R.string.chart_nenhum_venda_efetivada) to 1.0f)
                createPieChart(getString(R.string.chart_meio_pagamento), R.id.chart_total_meio_pag, entries)
            }

            val totalTipoPagData = (map.get("totalTipoPagData") as List<ChartGrupoValor>)
            if(totalTipoPagData != null && !totalTipoPagData.isEmpty()){
                val entries = LinkedHashMap<String, Float>()
                totalTipoPagData.forEach { valor  ->
                    entries.put(TipoPagamento.getDescricaoPeloNome(valor.grupo!!)!!, valor.total.toFloat())
                }
                createPieChart(getString(R.string.chart_tipo_pagamento), R.id.chart_total_tipo_pag, entries)
            }else{
                val entries = mapOf(getString(R.string.chart_nenhum_venda_efetivada) to 1.0f)
                createPieChart(getString(R.string.chart_tipo_pagamento), R.id.chart_total_tipo_pag, entries)
            }

            val totalCategoria = (map.get("totalCategoria") as List<ChartGrupoValor>)
            if(totalCategoria != null && !totalCategoria.isEmpty()){
                val entries = LinkedHashMap<String, Float>()
                totalCategoria.forEach { valor  ->
                    entries.put(valor.grupo!!!!, valor.total.toFloat())
                }
                createPieChart("Por Categoria", R.id.chart_total_categoria, entries)
            }else{
                val entries = mapOf(getString(R.string.chart_nenhum_venda_efetivada) to 1.0f)
                createPieChart("Por Categoria", R.id.chart_total_categoria, entries)
            }

            if(firstLoad) {

                val barchartDataSets = ArrayList<BarChartDataSet>()
                val barchart = BarChartUtil(this, R.id.chart_bar)
                val entriesReceber = (map.get("barchartTotalReceberdata")) as Map<Float, Float>
                barchart.build(MonthAxisValueFormatter(barchart.chart), MyValueFormatter("R$"))
                barchartDataSets.add(BarChartDataSet("Receber", R.color.secondaryColor, entriesReceber))

                val entriesPagar = (map.get("barchartTotalPagardata")) as Map<Float, Float>
                barchart.build(MonthAxisValueFormatter(barchart.chart), MyValueFormatter("R$"))
                barchartDataSets.add(BarChartDataSet("Pagar", R.color.margentaColor, entriesPagar))

                if (barchartDataSets.isNotEmpty()) {
                    barchart.setData(barchartDataSets)
                }
            }

            firstLoad = false
        }

    }

    private fun createPieChart(title: String, resId: Int, data: Map<String, Float>){
        val p = PieChartUtil(this, resId)
        p.build()
        p.setData(title, data)
    }

    class LoadDataAsync(private var iPostExecuteSearch: IPostExecuteSearch) : AsyncTask<Any, Any, Any>(){

        override fun doInBackground(vararg params: Any?): Any {

            val data: Calendar = if (params.isNotEmpty()) params[0] as Calendar else Calendar.getInstance()

            val dao = CfvApplication.database!!.chartsDAO()

            var map = HashMap<String, Any>()

            val dataInicio: Calendar = Calendar.getInstance()
            dataInicio.time = data.time
            dataInicio.set(Calendar.DAY_OF_MONTH, dataInicio.getMinimum(Calendar.DAY_OF_MONTH))
            dataInicio.set(Calendar.HOUR_OF_DAY, 0)
            dataInicio.set(Calendar.MINUTE, 0)
            dataInicio.set(Calendar.SECOND, 0)

            val dataFinal: Calendar = Calendar.getInstance()
            dataFinal.time = data.time
            dataFinal.set(Calendar.DAY_OF_MONTH, dataFinal.getMaximum(Calendar.DAY_OF_MONTH))
            dataFinal.set(Calendar.HOUR_OF_DAY, 23)
            dataFinal.set(Calendar.MINUTE, 59)
            dataFinal.set(Calendar.SECOND, 59)

            val totalBalancete = CfvApplication.database!!.itemBalanceteDAO()
                .getTotalBalanceteByMesAndAno(dataInicio.get(Calendar.MONTH)+1, dataInicio.get(Calendar.YEAR))
            map.put("totalBalancete", totalBalancete)

            val totalMeioPagData = dao.getTotalPorMeioPagamento(dataInicio.timeInMillis, dataFinal.timeInMillis)
            map.put("totalMeioPagData", totalMeioPagData)

            val totalTipoPagData = dao.getTotalPorTipoPagamento(dataInicio.timeInMillis, dataFinal.timeInMillis)
            map.put("totalTipoPagData", totalTipoPagData)

            val totalCategoria = dao.getTotalPorCategoria(dataInicio.timeInMillis, dataFinal.timeInMillis)
            map.put("totalCategoria", totalCategoria)

            dataInicio.set(Calendar.MONTH, 0)
            dataFinal.add(Calendar.MONTH, 12)


            val today = Calendar.getInstance()

            val barchartTotalReceberdata = dao.getTotalReceberAgrupadoPorMes(dataInicio.time, dataFinal.time)
            map.put("barchartTotalReceberdata", getBarchartData(barchartTotalReceberdata, today))

            val barchartTotalPagardata = dao.getTotalPagarAgrupadoPorMes(dataInicio.time, dataFinal.time)
            map.put("barchartTotalPagardata", getBarchartData(barchartTotalPagardata, today))

            return map
        }

        override fun onPostExecute(result: Any?) {
            super.onPostExecute(result)
            iPostExecuteSearch.afterSearch(result)
        }

        private fun getBarchartData(barcharItens: List<BarChartItem>, dataInicio: Calendar) : Map<Float, Float>{
            val entries = LinkedHashMap<Float, Float>()

            val mes = dataInicio.get(Calendar.MONTH)
            for ( x in (mes - 1)..(mes + 12)){
                entries.put(1.0f * x, 0.0f)
            }


            if (barcharItens.isNotEmpty()) {
                barcharItens.forEach { valor ->
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = valor.axisX!!
                    val fator = if (cal.get(Calendar.YEAR) > dataInicio.get(Calendar.YEAR)) 12.0f else 0.0f
                    val xAxis = (cal.get(Calendar.MONTH) * 1.0f) + fator
                    if (entries.containsKey(xAxis)) {
                        entries.put(xAxis, entries.get(xAxis)!!.plus(valor.axisY))
                    } else {
                        entries.put(xAxis, valor.axisY)
                    }
                }
            }

            return entries
        }

    }

    override fun execute(mes: Int, ano: Int) {
        var cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, mes.minus(1))
        cal.set(Calendar.YEAR, ano)
        LoadDataAsync(this).execute(cal)
    }
}