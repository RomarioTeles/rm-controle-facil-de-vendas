package br.com.rm.cfv.database.entities.dtos

import androidx.room.ColumnInfo

class ChartGrupoValor {
    @ColumnInfo(name="valor")
    var total: Double = 0.0
    @ColumnInfo(name="grupo")
    var grupo: String? = null
}
