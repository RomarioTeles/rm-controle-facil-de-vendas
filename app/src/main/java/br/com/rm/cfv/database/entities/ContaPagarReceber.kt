package br.com.rm.cfv.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import br.com.rm.cfv.constants.MeioPagamento
import br.com.rm.cfv.constants.StatusPagamento
import br.com.rm.cfv.constants.TipoPagamento
import br.com.rm.cfv.constants.TipoReferencia
import com.google.android.material.textfield.TextInputLayout
import java.util.*

@Entity
open class ContaPagarReceber() : Parcelable{
    @PrimaryKey(autoGenerate = true) var uid: Int? = null
    @ColumnInfo(name = "id_ref")  var idRef : Int? = null
    @ColumnInfo(name = "nome_ref")  var nomeRef : String? = null
    @ColumnInfo(name = "data_hora")  var dataHora : Long = Date().time
    @ColumnInfo(name = "tipo_ref")  var tipoRef : String? = TipoReferencia.DESPESAS
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
        idRef = parcel.readValue(Int::class.java.classLoader) as? Int
        nomeRef = parcel.readString()
        tipoRef = parcel.readString()
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

        if(tipoRef == null){
            hasError = true
            fields.getValue("tipoRef").error = String.format(messageNullable!!,"tipoRef")
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
        parcel.writeValue(idRef)
        parcel.writeString(nomeRef)
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

    companion object CREATOR : Parcelable.Creator<ContaPagarReceber> {
        override fun createFromParcel(parcel: Parcel): ContaPagarReceber {
            return ContaPagarReceber(parcel)
        }

        override fun newArray(size: Int): Array<ContaPagarReceber?> {
            return arrayOfNulls(size)
        }
    }


}