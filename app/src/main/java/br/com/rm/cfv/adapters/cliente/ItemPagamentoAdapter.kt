package br.com.rm.cfv.adapters.cliente

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.contaPagarReceber.contaPagarReceberViewer.detalhe.DetalheParcelaActivity
import br.com.rm.cfv.database.entities.PagamentoDebito
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO
import br.com.rm.dateutils.DateFormatUtils
import br.com.rm.dateutils.DateOperationsUtils
import br.com.rm.numberUtils.DecimalFormatUtils
import java.util.*

class ItemPagamentoAdapter(private var context: Context, private var pagamentoDebitoSubtotal: PagamentoDebitoSubtotalDTO, private var myDataset: MutableList<PagamentoDebito>) : RecyclerView.Adapter<ItemPagamentoAdapter.PagamentoViewHolder>() {

    class PagamentoViewHolder(val view : View) : RecyclerView.ViewHolder(view){

        lateinit var  textViewDescricao : TextView
        lateinit var  textViewValor : TextView
        lateinit var  textViewValorPago : TextView
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
            .inflate(R.layout.recycler_view_item_promissoria_alt, parent, false) as View

        val textViewDescricao = view.findViewById<TextView>(R.id.textViewDescricao)

        val textViewValor = view.findViewById<TextView>(R.id.textViewValor)

        val textViewValorPago = view.findViewById<TextView>(R.id.textViewValorPago)

        val textViewStatus = view.findViewById<TextView>(R.id.textViewStatus)

       val textViewVencimento     = view.findViewById<TextView>(R.id.textViewVencimento)


        var holder = PagamentoViewHolder(view)

        holder.textViewDescricao = textViewDescricao
        holder.textViewValor = textViewValor
        holder.textViewStatus = textViewStatus
        holder.textViewDataVencimento = textViewVencimento
        holder.textViewValorPago = textViewValorPago

        return holder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PagamentoViewHolder, position: Int) {

        var item = myDataset.get(position)

        holder.textViewDescricao.text = pagamentoDebitoSubtotal.descricao
        holder.textViewValor.text = context.getString(R.string.currency_format,DecimalFormatUtils.decimalFormatPtBR(item.valor))
        holder.textViewValorPago.text = context.getString(R.string.currency_format,DecimalFormatUtils.decimalFormatPtBR(item.valorPago))
        holder.textViewValorPago.visibility = View.VISIBLE
        var data = DateFormatUtils.format(item.dataVencimento, "dd MMMM yyyy")

        holder.textViewDataVencimento.text = data.uppercase(Locale.getDefault())

        if (item.valorPago >= item.valor){
            holder.textViewStatus.text = "PAGO"
            holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.color_success))
        }else if(item.valorPago > 0 && item.valorPago < item.valor){
            holder.textViewStatus.text = "PAG. PARCIAL"
            holder.textViewStatus.setTextColor(ContextCompat.getColor(context,R.color.color_success))
        }else if(item.observacao != null){
            holder.textViewDescricao.text = item.observacao
            holder.textViewStatus.text = "PENDENTE"
            holder.textViewStatus.setTextColor(ContextCompat.getColor(context,R.color.color_alert))
        }
        else{
            holder.textViewStatus.text = "PENDENTE"
            holder.textViewStatus.setTextColor(ContextCompat.getColor(context,R.color.color_alert))

            var hoje = Date()
            hoje = DateOperationsUtils.removeTimeFromDate(hoje)
            var vencimento = Date(item.dataVencimento.time)
            vencimento = DateOperationsUtils.removeTimeFromDate(vencimento)
            if(DateOperationsUtils.getMin(hoje, vencimento) == vencimento){
                holder.textViewDataVencimento.setTextColor(ContextCompat.getColor(context,R.color.color_error))
            }
        }

        holder.view.setOnClickListener {
            var intent = Intent(context, DetalheParcelaActivity::class.java)
            intent.putExtra("PagamentoDebito", item)
            intent.putExtra("descricao", pagamentoDebitoSubtotal.descricao)
            context.startActivity(intent)
        }

    }

    override fun getItemCount() = myDataset.size
}
