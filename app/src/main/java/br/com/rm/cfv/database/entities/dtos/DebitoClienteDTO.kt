package br.com.rm.cfv.database.entities.dtos

import androidx.room.ColumnInfo
import androidx.room.Ignore
import br.com.rm.cfv.database.entities.DebitoCliente

class DebitoClienteDTO () {
    @ColumnInfo(name = "cliente_id")
    var clienteId: Int? = null
    @ColumnInfo(name = "nome")
    var nome: String? = null
    @ColumnInfo(name = "total")
    var total: Double? = 0.0
    @Ignore var debitos: List<DebitoCliente>? = null

    fun getTotalNullSafed() : Double{
        return if (total == null) 0.0 else total!!
    }
}