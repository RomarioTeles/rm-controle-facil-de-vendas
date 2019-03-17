package br.com.rm.cfv.adapters.cliente

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.database.entities.Cliente
import br.com.rm.cfv.R

class ClienteAdapter(private var myDataset: List<Cliente>) :
    RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ClienteViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        lateinit var textViewTelefone : TextView
        lateinit var  textViewNome : TextView
    }

    fun setDataset(dataset : List<Cliente>){
        this.myDataset = dataset
        notifyDataSetChanged()
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ClienteAdapter.ClienteViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_default, parent, false) as View
        // set the view's size, margins, paddings and layout parameters

        val textViewNome = view.findViewById<TextView>(R.id.textViewPrimary)

        val textViewTelefone = view.findViewById<TextView>(R.id.textViewItemSecondary)

        var holder = ClienteViewHolder(view)

        holder.textViewTelefone = textViewTelefone
        holder.textViewNome = textViewNome

        return holder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textViewNome.text = myDataset.get(position)!!.nome

        holder.textViewTelefone.text = myDataset.get(position)!!.telefone
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}