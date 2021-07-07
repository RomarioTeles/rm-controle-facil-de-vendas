package br.com.rm.cfv.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.rm.cfv.constants.MeioPagamento
import br.com.rm.dateutils.DateFormatUtils
import com.google.android.material.textfield.TextInputLayout
import java.util.*

@Entity
@ForeignKey(entity = ContaPagarReceber::class, parentColumns = ["uid"], childColumns = ["conta_pagar_receber_id"], onDelete = ForeignKey.SET_NULL)
open class PagamentoDebito : Parcelable {

    @PrimaryKey(autoGenerate = true) var uid: Int? = null
    @ColumnInfo(name = "data_hora")  var dataHora : Date = Date()
    @ColumnInfo(name = "data_vencimento")  var dataVencimento : Date = Date()
    @ColumnInfo(name = "meio_pagamento")  var meioPagamento : String = MeioPagamento.DINHEIRO.name
    @ColumnInfo(name = "valor_pago")  var valorPago : Double = 0.0
    @ColumnInfo(name = "valor")  var valor : Double = 0.0
    @ColumnInfo(name = "codigo") var codigo: String = UUID.randomUUID().toString()
    @ColumnInfo(name = "conta_pagar_receber_id")  var contaPagarReceberId : Int? = null
    @ColumnInfo(name = "observacao") var observacao : String? = null

    val messageNullable : String?
        get() = "%s não pode ser vazio."


    val messageInvalid : String?
        get() = "Informe um(a) %s válido(a)."

    constructor(parcel: Parcel) : this() {
        uid = parcel.readValue(Int::class.java.classLoader) as? Int
        dataHora = parcel.readSerializable() as Date
        dataVencimento = parcel.readSerializable() as Date
        meioPagamento = parcel.readString()!!
        valorPago = parcel.readDouble()
        valor = parcel.readDouble()
        codigo = parcel.readString()!!
        contaPagarReceberId = parcel.readValue(Int::class.java.classLoader) as? Int
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
            novoPagamento.contaPagarReceberId = this.contaPagarReceberId
            val data = Date(this.dataVencimento.time)
            val parcelaorigem = DateFormatUtils.format(data, "MM/yyyy")
            novoPagamento.observacao = "Débito de pagamento parcial efetivado. Parcela origem: ${parcelaorigem}."

            return novoPagamento

        }else{
            return null
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(uid)
        parcel.writeSerializable(dataHora)
        parcel.writeSerializable(dataVencimento)
        parcel.writeString(meioPagamento)
        parcel.writeDouble(valorPago)
        parcel.writeDouble(valor)
        parcel.writeString(codigo)
        parcel.writeValue(contaPagarReceberId)
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