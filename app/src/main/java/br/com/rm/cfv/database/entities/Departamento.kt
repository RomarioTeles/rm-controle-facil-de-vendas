package br.com.rm.cfv.database.entities

import androidx.room.*

@Entity(
    indices = arrayOf(Index(value = ["nome"], unique = true))
)
data class Departamento(
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "nome") var nome: String?
)