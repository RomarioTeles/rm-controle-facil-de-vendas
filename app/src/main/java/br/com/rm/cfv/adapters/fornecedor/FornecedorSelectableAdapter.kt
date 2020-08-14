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
import br.com.rm.cfv.database.entities.Fornecedor
import br.com.rm.cfv.utils.ToastUtils


class FornecedorSelectableAdapter(private var context : Context, private var myDataset: MutableList<Fornecedor>) :
    RecyclerView.Adapter<FornecedorViewHolder>(){

    fun setDataset(dataset : MutableList<Fornecedor>){
        this.myDataset = dataset
        notifyDataSetChanged()
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): FornecedorViewHolder {
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
                false,
                false,
                false,
                false
            )
            settings.isShowSelecionar = true

            ItemOptionsBottomSheetDialog().openDialog(
                context as Activity,
                item, position,
                settings,
                object : IBottomSheetOptions {

                    override fun buttonSheetSeleciona(item: Any?) {
                        val returnIntent = Intent()
                        returnIntent.putExtra("result", item as Fornecedor)
                        (context as Activity).setResult(Activity.RESULT_OK, returnIntent)
                        (context as Activity).finish()
                    }

                }
            )

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}