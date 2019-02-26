package br.com.rm.cfv.adapters.departamento

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import br.com.rm.cfv.database.entities.Departamento
import com.rm.cfv.R

class DepartamentoAdapter( context : Context, items : MutableList<Departamento>) : ArrayAdapter<Departamento>(context, R.layout.list_view_item_default, items) {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val rowView = inflater.inflate(R.layout.list_view_item_default, parent, false)

        var value = rowView.findViewById<TextView>(R.id.textViewItemValor)

        value.text = getItem(position).nome

        return rowView
    }
}