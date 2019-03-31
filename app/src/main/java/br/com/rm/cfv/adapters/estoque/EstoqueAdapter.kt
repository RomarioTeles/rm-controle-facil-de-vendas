package br.com.rm.cfv.adapters.estoque

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.activities.interfaces.IMovimentacaoEstoque
import br.com.rm.cfv.database.entities.dtos.EstoqueDTO
import br.com.rm.cfv.R

class EstoqueAdapter(private var iMovimentacaoEstoque: IMovimentacaoEstoque, private var myDataset: List<EstoqueDTO>) :
    RecyclerView.Adapter<EstoqueAdapter.ProdutoViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ProdutoViewHolder(val view : View) : RecyclerView.ViewHolder(view){

        lateinit var textViewProduto : TextView
        lateinit var  textViewQuantidade : TextView
    }

    fun setDataset(dataset : List<EstoqueDTO>){
        this.myDataset = dataset
        notifyDataSetChanged()
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): EstoqueAdapter.ProdutoViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_default, parent, false) as View
        // set the view's size, margins, paddings and layout parameters

        val textViewProduto = view.findViewById<TextView>(R.id.textViewNome)

        val textViewQuantidade = view.findViewById<TextView>(R.id.textViewItemCodigo)

        var holder = ProdutoViewHolder(view)

        holder.textViewQuantidade = textViewQuantidade
        holder.textViewProduto = textViewProduto

        return holder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        var item = myDataset.get(position)

        holder.textViewQuantidade.text = """Quantidade em estoque: ${item.quantidade()}"""

        holder.textViewProduto.text = item.toString()

        holder.view.setOnClickListener {
            iMovimentacaoEstoque.preparaMovimentacao(item)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}