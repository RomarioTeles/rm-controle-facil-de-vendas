package br.com.rm.cfv.adapters.balancete

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.constants.TipoItemBalancete
import br.com.rm.cfv.constants.TipoReferencia
import br.com.rm.cfv.database.entities.ItemBalancete
import br.com.rm.dateutils.DateFormatUtils
import br.com.rm.dateutils.DateOperationsUtils
import br.com.rm.numberUtils.DecimalFormatUtils
import java.util.*


class ItemBalanceteAdapter(private var context : Context, private var myDataset: List<ItemBalancete>) :
    RecyclerView.Adapter<ItemBalanceteAdapter.BalanceteViewHolder>() {

    class BalanceteViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        lateinit var textViewData : TextView
        lateinit var textViewTipo : TextView
        lateinit var textViewValor : TextView
        lateinit var textViewRef : TextView
        lateinit var imageViewIcon: ImageView
    }

    fun setDataset(dataset : List<ItemBalancete>){
        this.myDataset = dataset
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ItemBalanceteAdapter.BalanceteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_balancete, parent, false) as View

        val textViewData = view.findViewById<TextView>(R.id.textViewData)
        val textViewTipo  = view.findViewById<TextView>(R.id.textViewTipo)
        val textViewValor = view.findViewById<TextView>(R.id.textViewValor)
        val textViewRef  = view.findViewById<TextView>(R.id.textViewRef)
        val imageViewIcon = view.findViewById<ImageView>(R.id.imageViewIcon)
        var holder = BalanceteViewHolder(view)

        holder.textViewData = textViewData
        holder.textViewRef = textViewRef
        holder.textViewTipo = textViewTipo
        holder.textViewValor = textViewValor
        holder.imageViewIcon = imageViewIcon

        return holder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: BalanceteViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        val item = myDataset[position]

        var data = DateFormatUtils.format(item.dataHora, "dd") + "\n"
        data += DateFormatUtils.format(item.dataHora, "MMMM") + "\n"
        data += DateFormatUtils.format(item.dataHora, "HH:mm") + "\n"

        holder.textViewData.text = data.toUpperCase()
        holder.textViewValor.text = DecimalFormatUtils.decimalFormatPtBR(item.valor)
        holder.textViewRef.text = "${item.nomeRef} - ${item.idRef}"
        holder.textViewTipo.text = item.tipo
        if(item.tipo == TipoItemBalancete.RECEITA){
            holder.imageViewIcon.setImageResource(R.drawable.plus_green_24dp)
        }else{
            holder.imageViewIcon.setImageResource(R.drawable.minus_red_24dp)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}