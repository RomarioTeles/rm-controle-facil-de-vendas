package br.com.rm.cfv.database.entities.dtos

import androidx.room.ColumnInfo
import androidx.room.Ignore

class DebitoClienteDTO {
    @ColumnInfo(name = "id_ref")
    var idRef: Int? = null
    @ColumnInfo(name = "nome_ref")
    var nome: String? = null
    @ColumnInfo(name = "total")
    var total: Double? = 0.0
    @Ignore var debitos: List<PagamentoDebitoSubtotalDTO>? = null

    fun getTotalFaltaPagar() : Double?{
        return if (total == null)  0.0 else total
    }
}