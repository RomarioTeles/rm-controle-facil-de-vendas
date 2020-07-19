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
import br.com.rm.cfv.activities.contaPagarReceber.RegistrarContaPagarReceberActivity
import br.com.rm.cfv.activities.fornecedor.CadastrarFornecedorActivity
import br.com.rm.cfv.bottomsheets.BottomSheetDialogSettings
import br.com.rm.cfv.bottomsheets.IBottomSheetOptions
import br.com.rm.cfv.bottomsheets.ItemOptionsBottomSheetDialog
import br.com.rm.cfv.database.entities.Fornecedor


class FornecedorAdapter(private var context : Context, private var myDataset: List<Fornecedor>) :
    RecyclerView.Adapter<FornecedorAdapter.FornecedorViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class FornecedorViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        lateinit var textViewTelefone : TextView
        lateinit var  textViewNome : TextView
    }

    fun setDataset(dataset : List<Fornecedor>){
        this.myDataset = dataset
        notifyDataSetChanged()
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): FornecedorAdapter.FornecedorViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_default, parent, false) as View
        // set the view's size, margins, paddings and layout parameters

        val textViewNome = view.findViewById<TextView>(R.id.textViewNome)

        val textViewTelefone = view.findViewById<TextView>(R.id.textViewItemCodigo)

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
                true,
                true,
                false
            )

            settings.textoAdicionar = "Registrar Despesa"

            ItemOptionsBottomSheetDialog().openDialog(
                context as Activity,
                item,
                settings,
                object : IBottomSheetOptions {
                    override fun buttonSheetAdiciona(item: Any?) {
                        val intent = Intent(context, RegistrarContaPagarReceberActivity::class.java)
                        intent.putExtra(RegistrarContaPagarReceberActivity.ARG_REFERENCIA, item as Fornecedor)
                        context.startActivity(intent)
                    }

                    override fun buttonSheetEdita(item: Any?) {
                        val intent = Intent(context, CadastrarFornecedorActivity::class.java)
                        intent.putExtra("fornecedor", item as Fornecedor)
                        context.startActivity(intent)
                    }

                    override fun buttonSheetLista(item: Any?) {

                    }

                    override fun buttonSheetRemove(item: Any?) {
                    }
                }
            )

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}