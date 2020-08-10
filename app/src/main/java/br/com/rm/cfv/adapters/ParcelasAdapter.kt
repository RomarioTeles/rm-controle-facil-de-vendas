package br.com.rm.cfv.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import br.com.rm.cfv.R
import br.com.rm.numberUtils.DecimalFormatUtils
import java.math.BigDecimal
import java.math.RoundingMode

class ParcelasAdapter(context: Context) : ArrayAdapter<Int>(context, R.layout.list_view_item_default) {

    private var itens = mutableMapOf<Int, String>()

    var selected : Int? = 0

    fun init(totalVenda: Double, parcelas : Int, parcelaComJuros: Int, juros: Double){
        val qtdeParcelas = parcelas
        val percentualJurosParcelas = BigDecimal(juros)
        val total = BigDecimal(totalVenda)
        for (i in 1..qtdeParcelas) {
            if (i < parcelaComJuros) {
                val valorSemJuros: Double = totalVenda / i
                val label = context.getString(R.string.parcela_x_currency_format, i, DecimalFormatUtils.decimalFormatPtBR(valorSemJuros))
                itens.put(i, label)
            } else {
                val valorComJuros = (total.plus(    percentualJurosParcelas.divide(BigDecimal(100)).multiply(total)) )
                    .divide(BigDecimal(i), 2, RoundingMode.CEILING)
                val label = "$i x R$ ${valorComJuros}"
                itens.put(i, label)
            }
        }
    }

    private fun getItems() : Map<Int, String>{
        return itens
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_view_item_default, null)
        } else {
            view = convertView
        }

        val item = itens.get(position+1)
        var textview = view!!.findViewById<TextView>(R.id.textView)
        if(textview != null) {
            textview.text = item!!
        }

        if(selected == position){
           view!!.setBackgroundColor(ContextCompat.getColor(context, R.color.secondaryLightColor))
        }else{
           view!!.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
        }

        return view
    }

    override fun getCount(): Int {
        return getItems().size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}
