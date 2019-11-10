package br.com.rm.cfv.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import br.com.rm.cfv.R
import br.com.rm.cfv.constants.MeioPagamento

class MeioPagamentoAdapter(context: Context) : ArrayAdapter<MeioPagamento>(context, R.layout.list_view_item_meio_pagamento) {

    private val itens : List<MeioPagamento> = MeioPagamento.values().toList()

    var selected : MeioPagamento = MeioPagamento.DINHEIRO

    private fun getItems() : List<MeioPagamento>{
        return itens
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        if (convertView == null) {
            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_view_item_meio_pagamento, null)
        } else {
            view = convertView
        }

        val item = getItem(position)
        view!!.findViewById<TextView>(R.id.textViewItemValor).text = item!!.descricao
        view!!.findViewById<ImageView>(R.id.imageViewIcon).setImageResource(item.res)

        if(selected == item){
            view.setBackgroundColor(context.resources.getColor(R.color.accent_active))
        }else{
            view.setBackgroundColor(context.resources.getColor(android.R.color.transparent))
        }

        return view
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
}
