package br.com.rm.cfv.adapters.produto

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.database.entities.Produto
import br.com.rm.numberUtils.DecimalFormatUtils
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.produto.CadastrarProdutoActivity

class ProdutoAdapter(private var context: Context, private var myDataset: List<Produto>) :
    RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ProdutoViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        lateinit var textViewPrecoRevenda : TextView
        lateinit var  textViewNome : TextView
    }

    fun setDataset(dataset : List<Produto>){
        this.myDataset = dataset
        notifyDataSetChanged()
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ProdutoAdapter.ProdutoViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_default, parent, false) as View
        // set the view's size, margins, paddings and layout parameters

        val textViewNome = view.findViewById<TextView>(R.id.textViewPrimary)

        val textViewPrecoRevenda = view.findViewById<TextView>(R.id.textViewItemSecondary)

        var holder = ProdutoViewHolder(view)

        holder.textViewPrecoRevenda = textViewPrecoRevenda
        holder.textViewNome = textViewNome

        return holder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        var item = myDataset.get(position)

        holder.textViewNome.text = item!!.nome

        holder.textViewPrecoRevenda.text = "R$ "+ DecimalFormatUtils.decimalFormatPtBR(item!!.precoVenda)

        holder.view.setOnClickListener{
            var intent = Intent(context, CadastrarProdutoActivity::class.java)
            intent.putExtra("produto", item)
            context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}