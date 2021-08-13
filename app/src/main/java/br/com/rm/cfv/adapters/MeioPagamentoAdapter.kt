package br.com.rm.cfv.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import br.com.rm.cfv.R
import br.com.rm.cfv.constants.MeioPagamento
import br.com.rm.cfv.constants.TipoPagamento

class MeioPagamentoAdapter(context: Context, private val spinnerMode: Boolean = false) :
    ArrayAdapter<MeioPagamento>(context, R.layout.list_view_item_meio_pagamento, R.id.textView) {

    private var itens: List<MeioPagamento> = MeioPagamento.getMeiosPagamentosAvista()

    var selected: MeioPagamento? = MeioPagamento.DINHEIRO

    private var tipoPagamento = TipoPagamento.A_VISTA

    fun getItems(): List<MeioPagamento> {
        return itens.toList()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_view_item_meio_pagamento, null)
        } else {
            view = convertView
        }

        val item = getItem(position)
        val textview = view!!.findViewById<TextView>(R.id.textView)
        textview.text = item!!.descricao
        view.findViewById<ImageView>(R.id.imageViewIcon).setImageResource(item.res)

        if (selected == item && !spinnerMode) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.secondaryColor))
            textview.setTextColor(ContextCompat.getColor(context, R.color.primaryTextColor))
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            textview.setTextColor(ContextCompat.getColor(context, R.color.secondaryTextColor))
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    override fun getCount(): Int {
        return getItems().size
    }

    override fun getItem(position: Int): MeioPagamento? {
        return getItems()[position]
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)!!.ordinal.toLong()
    }

    fun setTipoPagamento(tipoPagamento: String) {
        this.tipoPagamento = tipoPagamento
        if (this.tipoPagamento == TipoPagamento.A_PRAZO) {
            itens = MeioPagamento.getMeiosPagamentosPrazo()
            selected = MeioPagamento.PARCELAMENTO_DA_LOJA
        } else {
            itens = MeioPagamento.getMeiosPagamentosAvista()
            selected = MeioPagamento.DINHEIRO
        }
        notifyDataSetChanged()
    }

    fun getTipoPagamento(): String {
        return tipoPagamento
    }
}
