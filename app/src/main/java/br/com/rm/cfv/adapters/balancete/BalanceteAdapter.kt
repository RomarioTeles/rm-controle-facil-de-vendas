package br.com.rm.cfv.adapters.balancete

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.activities.balancete.ListaItensBalanceteActivity
import br.com.rm.cfv.database.entities.Balancete


class BalanceteAdapter(private var context : Context, private var myDataset: List<Balancete>) :
    RecyclerView.Adapter<BalanceteAdapter.BalanceteViewHolder>() {

    class BalanceteViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        lateinit var textViewData : TextView
    }

    fun setDataset(dataset : List<Balancete>){
        this.myDataset = dataset
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): BalanceteAdapter.BalanceteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false) as View

        val textViewData = view.findViewById<TextView>(android.R.id.text1)

        var holder = BalanceteViewHolder(view)

        holder.textViewData = textViewData

        return holder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: BalanceteViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        val item = myDataset[position]

        var mes : String = item.mes.toString()

        if(mes.length < 2){
            mes = "0$mes"
        }

        holder.textViewData.text = "${mes}/${item.ano}"

        holder.view.setOnClickListener {
            var intent =  Intent(context, ListaItensBalanceteActivity::class.java)
            intent.putExtra("balancete", item )
            context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}