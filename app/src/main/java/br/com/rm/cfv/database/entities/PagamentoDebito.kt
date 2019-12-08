package br.com.rm.cfv.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.rm.cfv.constants.MeioPagamento
import com.google.android.material.textfield.TextInputLayout
import java.time.Instant
import java.util.*

@Entity
@ForeignKey(entity = DebitoCliente::class, parentColumns = ["uid"], childColumns = ["debito_cliente_id"], onDelete = ForeignKey.SET_NULL)
open class PagamentoDebito : Parcelable {

    @PrimaryKey(autoGenerate = true) var uid: Int? = null
    @ColumnInfo(name = "data_hora")  var dataHora : Long = Date().time
    @ColumnInfo(name = "data_vencimento")  var dataVencimento : Long = Date().time
    @ColumnInfo(name = "meio_pagamento")  var meioPagamento : String = MeioPagamento.DINHEIRO.name
    @ColumnInfo(name = "valor_pago")  var valorPago : Double = 0.0
    @ColumnInfo(name = "valor")  var valor : Double = 0.0
    @ColumnInfo(name = "codigo") var codigo: String = UUID.randomUUID().toString()
    @ColumnInfo(name = "debito_cliente_id")  var debitoClienteId : Int? = null


    val messageNullable : String?
        get() = "%s não pode ser vazio."


    val messageInvalid : String?
        get() = "Informe um(a) %s válido(a)."

    constructor(parcel: Parcel) : this() {
        uid = parcel.readValue(Int::class.java.classLoader) as? Int
        dataHora = parcel.readLong()
        dataVencimento = parcel.readLong()
        meioPagamento = parcel.readString()
        valorPago = parcel.readDouble()
        valor = parcel.readDouble()
        codigo = parcel.readString()
        debitoClienteId = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    constructor()

    fun validate(fields : Map<String, TextInputLayout>): Boolean {
        var hasError = false
        return !hasError
    }

    fun verificaDebito(): PagamentoDebito?{
        if(valorPago < valor) {
            val novoPagamento = PagamentoDebito()
            novoPagamento.uid = null
            novoPagamento.dataHora = this.dataHora
            novoPagamento.dataVencimento = this.dataVencimento
            novoPagamento.meioPagamento = MeioPagamento.DINHEIRO.name
            novoPagamento.valorPago = 0.0
            novoPagamento.valor = valor.minus(valorPago)
            novoPagamento.codigo = "$codigo-R"
            novoPagamento.debitoClienteId = this.debitoClienteId
            return novoPagamento
        }else{
            return null
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(uid)
        parcel.writeLong(dataHora)
        parcel.writeLong(dataVencimento)
        parcel.writeString(meioPagamento)
        parcel.writeDouble(valorPago)
        parcel.writeDouble(valor)
        parcel.writeString(codigo)
        parcel.writeValue(debitoClienteId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PagamentoDebito> {
        override fun createFromParcel(parcel: Parcel): PagamentoDebito {
            return PagamentoDebito(parcel)
        }

        override fun newArray(size: Int): Array<PagamentoDebito?> {
            return arrayOfNulls(size)
        }
    }
}