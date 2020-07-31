package br.com.rm.cfv.adapters.fornecedor

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
import br.com.rm.cfv.activities.contaPagarReceber.ListaContasPagarReceberActivity
import br.com.rm.cfv.activities.contaPagarReceber.compra_venda_produtos.RegistrarCompraVendaActivity
import br.com.rm.cfv.activities.fornecedor.CadastrarFornecedorActivity
import br.com.rm.cfv.asyncTasks.IPostExecuteDelete
import br.com.rm.cfv.asyncTasks.fornecedor.DeleteFornecedorAsyncTask
import br.com.rm.cfv.bottomsheets.BottomSheetDialogSettings
import br.com.rm.cfv.bottomsheets.IBottomSheetOptions
import br.com.rm.cfv.bottomsheets.ItemOptionsBottomSheetDialog
import br.com.rm.cfv.database.daos.interfaces.FornecedorDAO
import br.com.rm.cfv.database.entities.Fornecedor
import br.com.rm.cfv.utils.ToastUtils


class FornecedorAdapter(private var context : Context,private var fornecedorDAO: FornecedorDAO, private var myDataset: MutableList<Fornecedor>) :
    RecyclerView.Adapter<FornecedorAdapter.FornecedorViewHolder>(), IPostExecuteDelete{

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class FornecedorViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        lateinit var textViewTelefone : TextView
        lateinit var  textViewNome : TextView
    }

    fun setDataset(dataset : MutableList<Fornecedor>){
        this.myDataset = dataset
        notifyDataSetChanged()
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): FornecedorAdapter.FornecedorViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_default_2, parent, false) as View
        // set the view's size, margins, paddings and layout parameters

        val textViewNome = view.findViewById<TextView>(R.id.textView1)

        val textViewTelefone = view.findViewById<TextView>(R.id.textView2)

        var holder = FornecedorViewHolder(view)

        holder.textViewTelefone = textViewTelefone
        holder.textViewNome = textViewNome

        return holder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: FornecedorViewHolder, position: Int) {
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

            settings.textoAdicionar = "Registrar Compra"
            settings.textoListar = "Compras Realizadas"

            val ipostExecuteDelete = this

            ItemOptionsBottomSheetDialog().openDialog(
                context as Activity,
                item, position,
                settings,
                object : IBottomSheetOptions {
                    override fun buttonSheetAdiciona(item: Any?) {
                        val intent = Intent(context, RegistrarCompraVendaActivity::class.java)
                        intent.putExtra(RegistrarCompraVendaActivity.ARG_REFERENCIA, item as Fornecedor)
                        context.startActivity(intent)
                    }

                    override fun buttonSheetEdita(item: Any?) {
                        val intent = Intent(context, CadastrarFornecedorActivity::class.java)
                        intent.putExtra("fornecedor", item as Fornecedor)
                        context.startActivity(intent)
                    }

                    override fun buttonSheetLista(item: Any?) {
                        val intent = Intent(context, ListaContasPagarReceberActivity::class.java)
                        intent.putExtra(RegistrarCompraVendaActivity.ARG_REFERENCIA, item as Fornecedor)
                        context.startActivity(intent)
                    }

                    override fun buttonSheetRemove(item: Any?, position: Int) {
                        DeleteFornecedorAsyncTask(fornecedorDAO, ipostExecuteDelete).execute((item as Fornecedor).uid, position)
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