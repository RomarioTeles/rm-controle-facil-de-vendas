package br.com.rm.cfv.utils.charts

import android.content.Context
import androidx.core.content.ContextCompat
import br.com.rm.cfv.R
import com.github.mikephil.charting.utils.ColorTemplate

class MyColorTemplate(val context: Context) : ColorTemplate() {

    fun getCustomColors(): IntArray {

        val var1 = ContextCompat.getColor(context, R.color.chart_variante_1)
        val var2 = ContextCompat.getColor(context, R.color.chart_variante_2)
        val var3 = ContextCompat.getColor(context, R.color.chart_variante_3)
        val var4 = ContextCompat.getColor(context, R.color.chart_variante_4)
        val var5 = ContextCompat.getColor(context, R.color.chart_variante_5)
        val var6 = ContextCompat.getColor(context, R.color.chart_variante_6)
        val var7 = ContextCompat.getColor(context, R.color.chart_variante_7)
        val var8 = ContextCompat.getColor(context, R.color.chart_variante_8)
        val var9 = ContextCompat.getColor(context, R.color.chart_variante_9)
        val var10 = ContextCompat.getColor(context, R.color.chart_variante_10)


        val colors = intArrayOf(
            var1,
            var2,
            var3,
            var4,
            var5,
            var6,
            var7,
            var8,
            var9,
            var10
        )

        return colors
    }

}