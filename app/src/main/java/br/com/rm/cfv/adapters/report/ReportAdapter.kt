package br.com.rm.cfv.adapters.report

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.database.entities.dtos.ReportFields

class ReportAdapter(val context: Context, var myDataset: MutableList<ReportFields>) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val textViewDescricao = view.findViewById<TextView>(R.id.textViewDescricao)
        val textViewValor = view.findViewById<TextView>(R.id.textViewValor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_report, parent, false) as View
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val item = myDataset[position]

        holder.textViewDescricao.text = item.descricao
        holder.textViewValor.text = item.valor
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }
}