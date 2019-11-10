package br.com.rm.cfv.adapters.cliente

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.database.entities.PagamentoDebito
import br.com.rm.dateutils.DateFormatUtils
import br.com.rm.dateutils.DateOperationsUtils
import br.com.rm.numberUtils.DecimalFormatUtils
import java.util.*

class ItemPagamentoAdapter(private var context: Context, private var myDataset: MutableList<PagamentoDebito>) : RecyclerView.Adapter<ItemPagamentoAdapter.PagamentoViewHolder>() {

    class PagamentoViewHolder(val view : View) : RecyclerView.ViewHolder(view){

        lateinit var  textViewParcela : TextView
        lateinit var  textViewValor : TextView
        lateinit var textViewStatus : TextView
        lateinit var textViewDataVencimento : TextView
    }

    fun setDataset(dataset : MutableList<PagamentoDebito>){
        this.myDataset = dataset
        notifyDataSetChanged()
    }

    fun getListaPagamentos() : List<PagamentoDebito> {
        return myDataset.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PagamentoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_promissoria, parent, false) as View

        val textViewParcela = view.findViewById<TextView>(R.id.textViewParcela)

        val textViewValor = view.findViewById<TextView>(R.id.textViewValor)

        val textViewStatus = view.findViewById<TextView>(R.id.textViewStatus)

       val textViewVencimento     = view.findViewById<TextView>(R.id.textViewVencimento)


        var holder = PagamentoViewHolder(view)

        holder.textViewParcela = textViewParcela
        holder.textViewValor = textViewValor
        holder.textViewStatus = textViewStatus
        holder.textViewDataVencimento = textViewVencimento

        return holder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PagamentoViewHolder, position: Int) {

        var item = myDataset.get(position)

        holder.textViewParcela.text = String.format("%dª Parcela", position+1)
        holder.textViewValor.text = DecimalFormatUtils.decimalFormatPtBR(item.valor)
        holder.textViewDataVencimento.text = DateFormatUtils.format(Date(item.dataVencimento), "dd/MM/yyyy")

        if (item.valorPago >= item.valor){
            holder.textViewStatus.text = "PAGO"
            holder.textViewStatus.setTextColor(context.resources.getColor(R.color.color_success))
        }else{
            holder.textViewStatus.text = "PENDENTE"
            holder.textViewStatus.setTextColor(context.resources.getColor(R.color.color_alert))

            var hoje = Date()
            hoje = DateOperationsUtils.removeTimeFromDate(hoje)
            var vencimento = Date(item.dataVencimento)
            vencimento = DateOperationsUtils.removeTimeFromDate(vencimento)
            if(DateOperationsUtils.getMin(hoje, vencimento) == vencimento){
                holder.textViewDataVencimento.setTextColor(context.resources.getColor(R.color.color_error))
            }
        }

    }

    override fun getItemCount() = myDataset.size
}
