package br.com.rm.cfv.adapters.cliente

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.activities.cliente.debito.VisualizarDebitoActivity
import br.com.rm.cfv.activities.cliente.debito.VisualizarDebitoActivity.Companion.ARG_DEBITO_CLIENTE
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.debitoCliente.DeleteDebitoClienteAsyncTask
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO
import br.com.rm.cfv.utils.DialogConfig
import br.com.rm.cfv.utils.DialogUtils
import br.com.rm.cfv.utils.ToastUtils
import br.com.rm.dateutils.DateFormatUtils
import br.com.rm.numberUtils.DecimalFormatUtils


class DebitoClienteAdapter(private var context : Context, private var myDataset: MutableList<PagamentoDebitoSubtotalDTO>) :
    RecyclerView.Adapter<DebitoClienteAdapter.ClienteViewHolder>(), IPostExecuteSearch {

    override fun afterSearch(result: Any?) {
        if(result as Int > -1) {
            ToastUtils.showToastSuccess(
                context,
                context.resources.getString(R.string.mensagem_sucesso)
            )
            notifyItemRemoved(result)
            myDataset.removeAt(result)
        }else{
            ToastUtils.showToastError(
                context,
                context.resources.getString(R.string.mensagem_erro)
            )
        }
    }

    override fun showProgress(text: String) {
        (context as BaseActivity).showProgress(text)
    }

    override fun hideProgress() {
        (context as BaseActivity).hideProgress()
    }

    class ClienteViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        lateinit var textViewDataHora : TextView
        lateinit var  textViewTotal : TextView
        lateinit var  textViewStatus : TextView
        lateinit var textViewObs : TextView
    }

    fun setDataset(dataset : MutableList<PagamentoDebitoSubtotalDTO>){
        this.myDataset = dataset
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ClienteViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_promissoria, parent, false) as View
        // set the view's size, margins, paddings and layout parameters

        val textViewTotal = view.findViewById<TextView>(R.id.textViewValor)

        val textViewDataHora = view.findViewById<TextView>(R.id.textViewVencimento)

        val textViewObs = view.findViewById<TextView>(R.id.textViewObservacao)

        val textViewStatus = view.findViewById<TextView>(R.id.textViewStatus)

        var holder = ClienteViewHolder(view)

        holder.textViewDataHora = textViewDataHora
        holder.textViewTotal = textViewTotal
        holder.textViewStatus = textViewStatus
        holder.textViewObs = textViewObs

        return holder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        val item = myDataset[position]

        holder.textViewDataHora.text = DateFormatUtils.format(item.getDataHora(), "dd\nMMMM\nyyyy").toUpperCase()

        holder.textViewTotal.text = "R$ ${DecimalFormatUtils.decimalFormatPtBR(item.total)}"

        holder.textViewStatus.text = item.getStatus()

        if("PAGO".equals(item.getStatus())){
            holder.textViewStatus.setTextColor(context.resources.getColor(R.color.color_success))
        }

        holder.textViewObs.visibility = View.INVISIBLE

        holder.view.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.pick_option)
                .setItems(R.array.list_item_debito_cliente) { dialog, which ->
                    when (which){
                        0 ->{
                            val intent = Intent(context, VisualizarDebitoActivity::class.java)
                            intent.putExtra(ARG_DEBITO_CLIENTE, item)
                            context.startActivity(intent)
                        }
                        2 ->{
                            var dialogConfig = DialogConfig()
                            dialogConfig.negativeButtonListener = Runnable {
                                dialog.dismiss()
                            }
                            dialogConfig.positiveButtonListener = Runnable {
                                var task = DeleteDebitoClienteAsyncTask(CfvApplication.database!!.debitoClienteDAO(), this)
                                task.execute(item.id, position)
                            }
                            dialogConfig.showSubtitle = true
                            dialogConfig.showNegativeButton = true

                            DialogUtils.showDialogAlert(context, R.string.toast_title_confirm, R.string.mensagem_confirmacao_remocao_debito, dialogConfig )
                        }
                        3 ->{
                            dialog.dismiss()
                        }
                    }
                }
            builder.show()
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}