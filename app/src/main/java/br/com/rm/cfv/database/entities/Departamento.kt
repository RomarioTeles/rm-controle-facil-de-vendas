package br.com.rm.cfv.database.entities

import androidx.room.*

@Entity(
    indices = arrayOf(Index(value = ["nome"], unique = true))
)
@ForeignKey(entity = Departamento::class, parentColumns = ["nome"], childColumns = ["departamento_pai"])
data class Departamento(
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "nome") var nome: String?,
    @ColumnInfo(name = "departamento_pai")  var departamentoPai : String?
)