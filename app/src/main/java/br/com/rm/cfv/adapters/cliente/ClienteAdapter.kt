package br.com.rm.cfv.adapters.cliente

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.cliente.CadastrarClienteActivity
import br.com.rm.cfv.activities.cliente.ListaDebitosClienteActivity
import br.com.rm.cfv.activities.cliente.RegistrarDebitoActivity
import br.com.rm.cfv.database.entities.Cliente


class ClienteAdapter(private var context : Context, private var myDataset: List<Cliente>) :
    RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ClienteViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        lateinit var textViewTelefone : TextView
        lateinit var  textViewNome : TextView
    }

    fun setDataset(dataset : List<Cliente>){
        this.myDataset = dataset
        notifyDataSetChanged()
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ClienteAdapter.ClienteViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_default, parent, false) as View
        // set the view's size, margins, paddings and layout parameters

        val textViewNome = view.findViewById<TextView>(R.id.textViewNome)

        val textViewTelefone = view.findViewById<TextView>(R.id.textViewItemCodigo)

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
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.pick_option)
                .setItems(R.array.list_item_client_options) { dialog, which ->
                    when (which){
                        0 ->{
                            // Registrar debitos
                            val intent = Intent(context, RegistrarDebitoActivity::class.java)
                            intent.putExtra("cliente", item)
                            context.startActivity(intent)
                        }
                        1 ->{
                            //Listar debitos
                            val intent = Intent(context, ListaDebitosClienteActivity::class.java)
                            intent.putExtra("cliente", item)
                            context.startActivity(intent)
                        }
                        2 ->{
                            //Editar registro
                            val intent = Intent(context, CadastrarClienteActivity::class.java)
                            intent.putExtra("cliente", item)
                            context.startActivity(intent)
                        }
                        3 ->{
                            //Remover registro
                            dialog.dismiss()
                        }
                        4 ->{
                            // cancelar ação
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