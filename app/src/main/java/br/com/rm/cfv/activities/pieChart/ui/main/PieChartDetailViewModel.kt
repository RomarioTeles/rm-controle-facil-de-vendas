package br.com.rm.cfv.activities.pieChart.ui.main

import androidx.lifecycle.ViewModel
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.utils.charts.PieChartUtil
import com.github.mikephil.charting.charts.PieChart

class PieChartDetailViewModel : ViewModel() {

    lateinit var title: String

    lateinit var keys: ArrayList<String>

    lateinit var values: FloatArray

    lateinit var baseActivity: BaseActivity

    lateinit var pieChart: PieChart

    fun createPieChart(){

        var data = LinkedHashMap<String, Float>()

        keys.forEachIndexed { index, key ->
            data[key] = values[index]
        }

        val p = PieChartUtil(pieChart, baseActivity)
        p.build()
        p.setData("", data)
    }

}