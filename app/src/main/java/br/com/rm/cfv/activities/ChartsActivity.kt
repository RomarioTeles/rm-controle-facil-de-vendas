package br.com.rm.cfv.activities

import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.R
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.constants.MeioPagamento
import br.com.rm.cfv.constants.TipoPagamento
import br.com.rm.cfv.database.entities.dtos.BarChartItem
import br.com.rm.cfv.database.entities.dtos.ChartGrupoValor
import br.com.rm.cfv.utils.charts.BarChartUtil
import br.com.rm.cfv.utils.charts.PieChartUtil
import br.com.rm.cfv.utils.charts.common.BarChartDataSet
import br.com.rm.cfv.utils.charts.common.MyValueFormatter
import br.com.rm.cfv.utils.charts.common.MonthAxisValueFormatter
import br.com.rm.numberUtils.DecimalFormatUtils
import kotlinx.android.synthetic.main.activity_charts.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class ChartsActivity : BaseActivity() , IPostExecuteSearch{

    private var ano : Int? = null
    private var mes : Int? = null
    private var firstLoad : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charts)

        val cal = Calendar.getInstance()
        ano = cal.get(Calendar.YEAR)
        mes = cal.get(Calendar.MONTH)

        setLabelButtonPeriodo(mes!!, ano!!)

        LoadDataAsync(this).execute(cal)

        button_data.setOnClickListener { view ->
            abrirDialogAnoMesPicker()
        }

        fab.hide()
    }

    private fun setLabelButtonPeriodo( mes : Int, ano : Int){
        button_data.text = "${DateFormatSymbols.getInstance().months.get(mes)} ${ano}".toUpperCase()
    }

    override fun getToobarTitle(): String {
        return "Gráficos"
    }

    override fun afterSearch(result: Any?) {

        if(result != null){
            val map = result as Map<String, Any>

            if(map.get("totalReceberData") != null){
                textViewTotalReceber.text = getString(R.string.currency_format, DecimalFormatUtils.decimalFormatPtBR((map.get("totalReceberData") as Double)))
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

            val totalReceberData = dao.getTotalReceber(dataInicio.timeInMillis, dataFinal.timeInMillis)
            map.put("totalReceberData", totalReceberData)

            val totalMeioPagData = dao.getTotalPorMeioPagamento(dataInicio.timeInMillis, dataFinal.timeInMillis)
            map.put("totalMeioPagData", totalMeioPagData)

            val totalTipoPagData = dao.getTotalPorTipoPagamento(dataInicio.timeInMillis, dataFinal.timeInMillis)
            map.put("totalTipoPagData", totalTipoPagData)


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
}
