package br.com.rm.cfv.adapters.fornecedor

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FornecedorViewHolder(val view : View) : RecyclerView.ViewHolder(view){
    lateinit var textViewTelefone : TextView
    lateinit var  textViewNome : TextView
}