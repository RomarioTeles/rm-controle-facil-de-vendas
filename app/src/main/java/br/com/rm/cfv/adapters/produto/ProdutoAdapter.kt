package br.com.rm.cfv.adapters.produto

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.ImageUtilsActivity
import br.com.rm.cfv.activities.produto.IOnClickProdutoListener
import br.com.rm.cfv.database.entities.Produto
import br.com.rm.numberUtils.DecimalFormatUtils

class ProdutoAdapter(private var context: ImageUtilsActivity, private var iOnClickProdutoListener: IOnClickProdutoListener, private var myDataset: List<Produto>) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    class ProdutoViewHolder(val view : View) : RecyclerView.ViewHolder(view){

        lateinit var textViewPrecoRevenda : TextView
        lateinit var  textViewNome : TextView
        lateinit var  textViewCodigo : TextView
        lateinit var  imageViewProduto : ImageView
    }

    fun setDataset(dataset : List<Produto>){
        this.myDataset = dataset
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_produto, parent, false) as View

        val textViewNome = view.findViewById<TextView>(R.id.textViewNome)

        val textViewPrecoRevenda = view.findViewById<TextView>(R.id.textViewSubtotal)

        val textViewCodigo = view.findViewById<TextView>(R.id.textViewCodigo)

        val imageViewProduto = view.findViewById<ImageView>(R.id.imageViewProduto)

        var holder = ProdutoViewHolder(view)

        holder.textViewPrecoRevenda = textViewPrecoRevenda
        holder.textViewNome = textViewNome
        holder.textViewCodigo = textViewCodigo
        holder.imageViewProduto = imageViewProduto

        return holder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {

        var item = myDataset.get(position)

        holder.textViewNome.text = item.nome

        holder.textViewCodigo.text = item.codigo

        holder.textViewPrecoRevenda.text = """R$ ${DecimalFormatUtils.decimalFormatPtBR(item!!.precoVenda)}"""

        if (item.caminhoImagem != null && !item.caminhoImagem!!.isBlank()) {
            holder.imageViewProduto.setImageBitmap(context.getBitmapFromAbsolutePath(item.caminhoImagem, false))
        }

        holder.view.setOnClickListener{
            iOnClickProdutoListener.onProdutoClick(item, false)
        }

        holder.view.setOnLongClickListener{
            iOnClickProdutoListener.onProdutoClick(item, true)
            true
        }
    }

    override fun getItemCount() = myDataset.size
}
