package br.com.rm.cfv.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import br.com.rm.cfv.constants.MeioPagamento
import br.com.rm.cfv.constants.StatusPagamento
import br.com.rm.cfv.constants.TipoPagamento
import com.google.android.material.textfield.TextInputLayout
import java.util.*

@Entity
@ForeignKey(entity = Cliente::class, parentColumns = ["uid"], childColumns = ["cliente_id"], onDelete = ForeignKey.SET_NULL)
open class DebitoCliente() : Parcelable{
    @PrimaryKey(autoGenerate = true) var uid: Int? = null
    @ColumnInfo(name = "cliente_id")  var clienteId : Int? = null
    @ColumnInfo(name = "cliente_nome")  var clienteNome : String? = null
    @ColumnInfo(name = "data_hora")  var dataHora : Long = Date().time
    @ColumnInfo(name = "status_pagamento")  var statusPagamento : String = StatusPagamento.PENDENTE
    @ColumnInfo(name = "tipo_pagamento")  var tipoPagamento : String = TipoPagamento.A_VISTA
    @ColumnInfo(name = "data_prevista_pagamento")  var dataPrevistaPagamento : Long? = Date().time
    @ColumnInfo(name = "total")  var total : Double = 0.0
    @ColumnInfo(name = "codigo") var codigo: String = UUID.randomUUID().toString()
    @ColumnInfo(name = "qtde_parcelas")  var qtdeParcelas : Int = 1
    @ColumnInfo(name = "percentual_juros_parcelas")  var percentualJurosParcelas : Double = 0.0
    @Ignore var meioPagamento : String = MeioPagamento.DINHEIRO.name
    @Ignore var itemProdutoList: MutableList<ItemProduto> = mutableListOf()

    val messageNullable : String?
        get() = "%s não pode ser vazio."


    val messageInvalid : String?
        get() = "Informe um(a) %s válido(a)."

    constructor(parcel: Parcel) : this() {
        uid = parcel.readValue(Int::class.java.classLoader) as? Int
        clienteId = parcel.readValue(Int::class.java.classLoader) as? Int
        clienteNome = parcel.readString()
        dataHora = parcel.readLong()
        statusPagamento = parcel.readString()
        tipoPagamento = parcel.readString()
        dataPrevistaPagamento = parcel.readValue(Long::class.java.classLoader) as? Long
        total = parcel.readDouble()
        codigo = parcel.readString()
        qtdeParcelas = parcel.readInt()
        percentualJurosParcelas = parcel.readDouble()
        meioPagamento = parcel.readString()
    }

    fun validate(fields : Map<String, TextInputLayout>): Boolean {
        var hasError = false

        if(qtdeParcelas == null){
            qtdeParcelas = 1
        }

        if(dataHora == null){
            dataHora = Date().time
        }

        if(codigo == null){
            codigo = UUID.randomUUID().toString()
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

    fun getSubtotal(): Double {
        var subtotal = 0.0

        itemProdutoList.forEach {
            subtotal += it.subtotal
        }

        if(percentualJurosParcelas > 0) {
            return subtotal + (subtotal * (percentualJurosParcelas / 100))
        }else{
            return subtotal
        }
    }

    fun atualizaTotal(){
        total = getSubtotal()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(uid)
        parcel.writeValue(clienteId)
        parcel.writeString(clienteNome)
        parcel.writeLong(dataHora)
        parcel.writeString(statusPagamento)
        parcel.writeString(tipoPagamento)
        parcel.writeValue(dataPrevistaPagamento)
        parcel.writeDouble(total)
        parcel.writeString(codigo)
        parcel.writeInt(qtdeParcelas)
        parcel.writeDouble(percentualJurosParcelas)
        parcel.writeString(meioPagamento)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DebitoCliente> {
        override fun createFromParcel(parcel: Parcel): DebitoCliente {
            return DebitoCliente(parcel)
        }

        override fun newArray(size: Int): Array<DebitoCliente?> {
            return arrayOfNulls(size)
        }
    }


}