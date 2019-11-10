package br.com.rm.cfv.adapters.produto

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.ImageUtilsActivity
import br.com.rm.cfv.activities.produto.IOnClickProdutoListener
import br.com.rm.cfv.database.entities.ItemProduto
import br.com.rm.numberUtils.DecimalFormatUtils
import com.google.common.base.Strings

class ItemProdutoAdapter(private var context: Context, private var myDataset: MutableList<ItemProduto>) : RecyclerView.Adapter<ItemProdutoAdapter.ProdutoViewHolder>() {

    class ProdutoViewHolder(val view : View) : RecyclerView.ViewHolder(view){

        lateinit var textViewSubtotal : TextView
        lateinit var  textViewNome : TextView
        lateinit var  textViewCodigo : TextView
        lateinit var textViewQuantidade : TextView
        lateinit var textViewPrecoOriginal : TextView
        lateinit var textViewDescAcresc : TextView
    }

    fun setDataset(dataset : MutableList<ItemProduto>){
        this.myDataset = dataset
        notifyDataSetChanged()
    }

    fun getListaProdutos() : List<ItemProduto> {
        return myDataset.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_cesta, parent, false) as View

        val textViewNome = view.findViewById<TextView>(R.id.textViewNome)

        val textViewPrecoRevenda = view.findViewById<TextView>(R.id.textViewSubtotal)

        val textViewCodigo = view.findViewById<TextView>(R.id.textViewItemCodigo)

       val textViewQuantidade     = view.findViewById<TextView>(R.id.textViewQuantidade)
       val textViewPrecoOriginal = view.findViewById<TextView>(R.id.textViewPrecoOriginal)
       val textViewDescAcresc       = view.findViewById<TextView>(R.id.textViewDescAcresc)

        var holder = ProdutoViewHolder(view)

        holder.textViewSubtotal = textViewPrecoRevenda
        holder.textViewNome = textViewNome
        holder.textViewCodigo = textViewCodigo
        holder.textViewQuantidade = textViewQuantidade
        holder.textViewPrecoOriginal = textViewPrecoOriginal
        holder.textViewDescAcresc = textViewDescAcresc

        return holder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {

        var item = myDataset.get(position)
        holder.textViewNome.text = item.nomeProduto
        holder.textViewCodigo.text = item.codigoProduto
        holder.textViewSubtotal.text = DecimalFormatUtils.decimalFormatPtBR(item.subtotal)
        holder.textViewQuantidade.text = item.getQuantidade().toString()
        holder.textViewPrecoOriginal.text = DecimalFormatUtils.decimalFormatPtBR(item.precoUnitario)
        holder.textViewDescAcresc.text = DecimalFormatUtils.decimalFormatPtBR(item.getDescAcresc())
    }

    override fun getItemCount() = myDataset.size
}
