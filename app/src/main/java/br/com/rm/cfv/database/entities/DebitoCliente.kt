package br.com.rm.cfv.database.entities

import androidx.room.*
import br.com.rm.cfv.constants.MeioPagamento
import br.com.rm.cfv.constants.StatusPagamento
import br.com.rm.cfv.constants.TipoPagamento
import com.google.android.material.textfield.TextInputLayout
import java.util.*

@Entity
@ForeignKey(entity = Cliente::class, parentColumns = ["uid"], childColumns = ["cliente_id"], onDelete = ForeignKey.SET_NULL)
open class DebitoCliente{
    @PrimaryKey(autoGenerate = true) var uid: Int? = null
    @ColumnInfo(name = "cliente_id")  var clienteId : Int? = null
    @ColumnInfo(name = "cliente_nome")  var clienteNome : String? = null
    @ColumnInfo(name = "data_hora")  var dataHora : Long = Date().time
    @ColumnInfo(name = "status_pagamento")  var statusPagamento : String = StatusPagamento.PENDENTE
    @ColumnInfo(name = "tipo_pagamento")  var tipoPagamento : String = TipoPagamento.A_VISTA
    @ColumnInfo(name = "meio_pagamento")  var meioPagamento : String = MeioPagamento.DINHEIRO
    @ColumnInfo(name = "data_prevista_pagamento")  var dataPrevistaPagamento : Long? = null
    @ColumnInfo(name = "data_pagamento")  var dataPagamento : Long? = null
    @ColumnInfo(name = "total")  var total : Double? = 0.0
    @Relation(parentColumn = "uid", entityColumn = "debito_cliente_id") var itemProdutoList: MutableList<ItemProduto> = mutableListOf()

    val messageNullable : String?
        get() = "%s não pode ser vazio."


    val messageInvalid : String?
        get() = "Informe um(a) %s válido(a)."

    fun validate(fields : Map<String, TextInputLayout>): Boolean {
        var hasError = false

        if(dataHora == null){
            dataHora = Date().time
        }

        if(clienteId == null || clienteNome == null){
            hasError = true
            fields.getValue("clienteId").error = String.format(messageNullable!!,"Cliente")
        }

        if(statusPagamento == null){
            hasError = true
            fields.getValue("statusPagamento").error = String.format(messageNullable!!,"Status do pagamento")
        }

        if(tipoPagamento == null){
            hasError = true
            fields.getValue("tipoPagamento").error = String.format(messageNullable!!,"Tipo do pagamento")

        }else if(TipoPagamento.A_PRAZO == tipoPagamento && dataPrevistaPagamento == null){
            fields.getValue("dataPrevistaPagamento").error = String.format(messageNullable!!,"Data prevista do pagamento")
            hasError = true
        }

        if(itemProdutoList.isEmpty()){
            hasError = true
        }

        return !hasError
    }

    fun getSubtotal(): Double? {
        var subtotal = 0.0

        itemProdutoList.forEach {
            subtotal += it.subtotal
        }
        return subtotal
    }

}