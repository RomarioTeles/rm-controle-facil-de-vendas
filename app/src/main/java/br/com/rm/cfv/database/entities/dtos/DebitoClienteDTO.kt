package br.com.rm.cfv.database.entities.dtos

import androidx.room.ColumnInfo

data class DebitoClienteDTO (
    @ColumnInfo(name = "cliente_id") var clienteId: Int?,
    @ColumnInfo(name = "nome") var nome: String?,
    @ColumnInfo(name = "total") var total: Double?
)