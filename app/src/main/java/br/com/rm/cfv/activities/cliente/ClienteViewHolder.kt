package br.com.rm.cfv.activities.cliente

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClienteViewHolder(val view : View) : RecyclerView.ViewHolder(view){
    lateinit var textViewTelefone : TextView
    lateinit var  textViewNome : TextView
}