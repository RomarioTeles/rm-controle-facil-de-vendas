package br.com.rm.cfv.adapters.cliente

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.activities.cliente.CadastrarClienteActivity
import br.com.rm.cfv.activities.contaPagarReceber.ListaContasPagarReceberActivity
import br.com.rm.cfv.activities.contaPagarReceber.compra_venda_produtos.RegistrarCompraVendaActivity
import br.com.rm.cfv.asyncTasks.IPostExecuteDelete
import br.com.rm.cfv.asyncTasks.cliente.DeleteClienteAsyncTask
import br.com.rm.cfv.bottomsheets.BottomSheetDialogSettings
import br.com.rm.cfv.bottomsheets.IBottomSheetOptions
import br.com.rm.cfv.bottomsheets.ItemOptionsBottomSheetDialog
import br.com.rm.cfv.database.daos.interfaces.ClienteDAO
import br.com.rm.cfv.database.entities.Cliente
import br.com.rm.cfv.utils.ToastUtils


class ClienteAdapter(private var context : Context, private var clienteDAO: ClienteDAO, private var myDataset: MutableList<Cliente>) :
    RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>(), IPostExecuteDelete{

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ClienteViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        lateinit var textViewTelefone : TextView
        lateinit var  textViewNome : TextView
    }

    fun setDataset(dataset : MutableList<Cliente>){
        this.myDataset = dataset
        notifyDataSetChanged()
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ClienteAdapter.ClienteViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_default_2, parent, false) as View
        // set the view's size, margins, paddings and layout parameters

        val textViewNome = view.findViewById<TextView>(R.id.textView1)

        val textViewTelefone = view.findViewById<TextView>(R.id.textView2)

        var holder = ClienteViewHolder(view)

        holder.textViewTelefone = textViewTelefone
        holder.textViewNome = textViewNome

        return holder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        val item = myDataset[position]

        holder.textViewNome.text = item.nome

        holder.textViewTelefone.text = item.telefone

        holder.view.setOnClickListener{

            val settings = BottomSheetDialogSettings(
                item.nome,
                true,
                true,
                true,
                true
            )
            settings.textoAdicionar = "Registrar venda"
            settings.textoListar = "Vendas realizadas"

            val ipostExecuteDelete = this

            ItemOptionsBottomSheetDialog().openDialog(
                context as Activity,
                item,
                position,
                settings,
                object : IBottomSheetOptions {
                    override fun buttonSheetAdiciona(item: Any?) {
                        val intent = Intent(context, RegistrarCompraVendaActivity::class.java)
                        intent.putExtra(RegistrarCompraVendaActivity.ARG_REFERENCIA, item as Cliente)
                        context.startActivity(intent)
                    }

                    override fun buttonSheetEdita(item: Any?) {
                        val intent = Intent(context, CadastrarClienteActivity::class.java)
                        intent.putExtra(RegistrarCompraVendaActivity.ARG_REFERENCIA, item as Cliente)
                        context.startActivity(intent)
                    }

                    override fun buttonSheetLista(item: Any?) {
                        val intent = Intent(context, ListaContasPagarReceberActivity::class.java)
                        intent.putExtra(RegistrarCompraVendaActivity.ARG_REFERENCIA, item as Cliente)
                        context.startActivity(intent)
                    }

                    override fun buttonSheetRemove(item: Any?, position: Int) {
                        DeleteClienteAsyncTask(clienteDAO, ipostExecuteDelete).execute((item as Cliente).uid, position)
                    }
                }
            )

        }
    }

    override fun afterDelete(result: Any?) {
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

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}