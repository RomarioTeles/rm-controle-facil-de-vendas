package br.com.rm.cfv.activities

import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
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
import br.com.rm.cfv.utils.charts.PieChatUtil
import br.com.rm.numberUtils.DecimalFormatUtils
import kotlinx.android.synthetic.main.activity_charts.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class ChartsActivity : BaseActivity() , IPostExecuteSearch{

    private var ano : Int? = null
    private var mes : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charts)

        val cal = Calendar.getInstance()
        ano = cal.get(Calendar.YEAR)
        mes = cal.get(Calendar.MONTH) + 1

        setLabelButtonPeriodo(mes!!.minus(1), ano!!)

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

            val barchartdata = (map.get("barchartdata")) as List<BarChartItem>
            if(barchartdata != null && !barchartdata.isEmpty()){
                val entries = LinkedHashMap<Float, Float>()
                barchartdata.forEach { valor  ->
                    var cal = Calendar.getInstance()
                    cal.timeInMillis = valor.axisX!!

                    val month = cal.get(Calendar.MONTH) * 1.0F
                    if(entries.containsKey(month)){
                        entries.put(month, entries.get(month)!!.plus(valor.axisY))
                    }else{
                        entries.put(month, valor.axisY)
                    }
                }
                val p = BarChartUtil(this, R.id.chart_bar)
                p.build()
                p.setData("", entries)
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
        monthPicker.value = mes!!

        var year = cal.get(Calendar.YEAR)
        yearPicker.minValue = year - 5
        yearPicker.maxValue = year + 1
        yearPicker.value = ano!!

        monthPicker.setOnValueChangedListener { picker, oldVal, newVal -> mes = newVal  }

        yearPicker.setOnValueChangedListener { picker, oldVal, newVal -> ano = newVal }

        builder.setView(dialog).setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            Log.i("Positive Button", which.toString())
            val cal = Calendar.getInstance()

            linearLayoutTotalReceber.visibility = View.GONE
            if(cal.get(Calendar.YEAR) == ano && mes!! >= (cal.get(Calendar.MONTH) + 1) ){
                linearLayoutTotalReceber.visibility = View.VISIBLE
            }

            cal.set(Calendar.YEAR, ano!!)
            cal.set(Calendar.MONTH, mes!!)

            setLabelButtonPeriodo(mes!!.minus(1), ano!!)

            LoadDataAsync(this).execute(cal)
        })

        builder.show()
    }

    private fun createPieChart(title: String, resId: Int, data: Map<String, Float>){
        val p = PieChatUtil(this, resId)
        p.build()
        p.setData(title, data)
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

            val totalMeioPagData = dao.getTotalPorMeioPagamento(dataInicio.timeInMillis, dataFinal.timeInMillis)
            map.put("totalMeioPagData", totalMeioPagData)

            val totalTipoPagData = dao.getTotalPorTipoPagamento(dataInicio.timeInMillis, dataFinal.timeInMillis)
            map.put("totalTipoPagData", totalTipoPagData)


            dataInicio.set(Calendar.MONTH, 0)
            dataFinal.set(Calendar.MONTH, 11)

            val barchartdata = dao.getTotalReceberAgrupadoPorMes(dataInicio.time, dataFinal.time)
            map.put("barchartdata", barchartdata)

            return map
        }

        override fun onPostExecute(result: Any?) {
            super.onPostExecute(result)
            iPostExecuteSearch.afterSearch(result)
        }

    }
}
