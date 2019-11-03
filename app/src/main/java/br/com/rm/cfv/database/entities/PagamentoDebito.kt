package br.com.rm.cfv.database.entities

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
data class PagamentoDebito (
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "data_hora")  var dataHora : Long = Date().time,
    @ColumnInfo(name = "data_vencimento")  var dataVencimento : Long = Date().time,
    @ColumnInfo(name = "meio_pagamento")  var meioPagamento : String = MeioPagamento.DINHEIRO.name,
    @ColumnInfo(name = "valor_pago")  var valorPago : Double = 0.0,
    @ColumnInfo(name = "valor")  var valor : Double = 0.0,
    @ColumnInfo(name = "codigo") var codigo: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "debito_cliente_id")  var debitoClienteId : Int? = null
){
    val messageNullable : String?
        get() = "%s não pode ser vazio."


    val messageInvalid : String?
        get() = "Informe um(a) %s válido(a)."

    fun validate(fields : Map<String, TextInputLayout>): Boolean {
        var hasError = false
        return !hasError
    }

    fun verificaDebito(): PagamentoDebito?{
        if(valorPago < valor) {
            val novoPagamento: PagamentoDebito = this.copy()
            novoPagamento.valor = valor.minus(valorPago)
            novoPagamento.valorPago = 0.0
            novoPagamento.codigo = "$codigo-R"
            novoPagamento.uid = null
            novoPagamento.meioPagamento = MeioPagamento.DINHEIRO.name
            return novoPagamento
        }else{
            return null
        }
    }
}