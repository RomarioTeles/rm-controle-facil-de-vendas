package br.com.rm.cfv.database.entities.dtos

import androidx.room.ColumnInfo
import br.com.rm.cfv.constants.StatusPagamento

open class PagamentoDebitoSubtotalDTO (
    @ColumnInfo(name = "valorPago") var valorPago: Double?,
    @ColumnInfo(name = "total") var total: Double?
){

    fun getStatus(): String{
        if(valorPago!!.compareTo(total!!) >= 0){
            return StatusPagamento.PAGO
        }else{
            return StatusPagamento.PENDENTE
        }
    }

}
