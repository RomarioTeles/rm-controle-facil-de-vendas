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
import br.com.rm.cfv.database.entities.ItemProduto
import br.com.rm.numberUtils.DecimalFormatUtils

class ItemProdutoAdapter(
    private var context: Context,
    private var listener: IOnClickItemProdutoListener,
    private var myDataset: MutableList<ItemProduto>
) : RecyclerView.Adapter<ItemProdutoAdapter.ProdutoViewHolder>() {

    class ProdutoViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        lateinit var textViewSubtotal: TextView
        lateinit var textViewNome: TextView
        lateinit var textViewCodigo: TextView
        lateinit var textViewQuantidade: TextView
        lateinit var textViewPrecoOriginal: TextView
        lateinit var textViewDescAcresc: TextView
        lateinit var imageViewProduto: ImageView
    }

    fun setDataset(dataset: MutableList<ItemProduto>) {
        this.myDataset = dataset
        notifyDataSetChanged()
    }

    fun getListaProdutos(): List<ItemProduto> {
        return myDataset.toList()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_cesta_alt, parent, false) as View

        val textViewNome = view.findViewById<TextView>(R.id.textView1)

        val textViewPrecoRevenda = view.findViewById<TextView>(R.id.textViewPrecoRevenda)

        val textViewCodigo = view.findViewById<TextView>(R.id.textView2)

        val textViewQuantidade = view.findViewById<TextView>(R.id.textViewQuantidade)
        val textViewPrecoOriginal = view.findViewById<TextView>(R.id.textViewPrecoOriginal)
        val textViewDescAcresc = view.findViewById<TextView>(R.id.textViewDescAcresc)
        val imageViewProduto = view.findViewById<ImageView>(R.id.imageViewProduto)

        var holder = ProdutoViewHolder(view)

        holder.textViewSubtotal = textViewPrecoRevenda
        holder.textViewNome = textViewNome
        holder.textViewCodigo = textViewCodigo
        holder.textViewQuantidade = textViewQuantidade
        holder.textViewPrecoOriginal = textViewPrecoOriginal
        holder.textViewDescAcresc = textViewDescAcresc
        holder.imageViewProduto = imageViewProduto

        return holder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {

        val item = myDataset.get(position)
        holder.textViewNome.text = item.nomeProduto
        holder.textViewCodigo.text = item.codigoProduto
        holder.textViewSubtotal.text = context.getString(
            R.string.currency_format,
            DecimalFormatUtils.decimalFormatPtBR(item.subtotal)
        )
        holder.textViewQuantidade.text = item.getQuantidade().toString()
        holder.textViewPrecoOriginal.text = context.getString(
            R.string.currency_format,
            DecimalFormatUtils.decimalFormatPtBR(item.precoUnitario)
        )
        holder.textViewDescAcresc.text = context.getString(
            R.string.currency_format,
            DecimalFormatUtils.decimalFormatPtBR(item.getDescAcresc())
        )

        val bitmap = ImageUtilsActivity.getBitmapFromAbsolutePath(item.imagePath)
        if(bitmap != null) {
            holder.imageViewProduto.setImageBitmap(bitmap)
        }

        holder.itemView.setOnClickListener{ listener.onItemProdutoClick(item) }
    }

    override fun getItemCount() = myDataset.size

    fun getItem(postition: Int) : ItemProduto{
        return myDataset[postition]
    }
}
