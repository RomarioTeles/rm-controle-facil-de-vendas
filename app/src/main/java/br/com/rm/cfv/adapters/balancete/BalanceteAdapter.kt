package br.com.rm.cfv.adapters.balancete

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.balancete.BalanceteActivity
import br.com.rm.cfv.activities.balancete.BalanceteTipoFragment
import br.com.rm.cfv.database.entities.Balancete
import java.text.DateFormatSymbols


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
            .inflate(R.layout.recycler_view_item_default_1, parent, false) as View

        val textViewData = view.findViewById<TextView>(R.id.textView1)

        var holder = BalanceteViewHolder(view)

        holder.textViewData = textViewData

        return holder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: BalanceteViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        val item = myDataset[position]

        var mes : Int = item.mes!!.minus(1)

        var nomeMes = DateFormatSymbols().months.get(mes)

        holder.textViewData.text = "${nomeMes} ${item.ano}"

        holder.view.setOnClickListener {
            var intent =  Intent(context, BalanceteActivity::class.java)
            intent.putExtra(BalanceteTipoFragment.ARG_BALANCETE, item )
            context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}