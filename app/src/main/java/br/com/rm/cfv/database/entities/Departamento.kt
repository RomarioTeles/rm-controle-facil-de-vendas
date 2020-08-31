package br.com.rm.cfv.database.entities

import androidx.room.*
import com.opencsv.bean.CsvBindByName

@Entity(
    indices = arrayOf(Index(value = ["nome"], unique = true))
)
@ForeignKey(entity = Departamento::class, parentColumns = ["nome"], childColumns = ["departamento_pai"])
data class Departamento(
    @CsvBindByName(column = "uid") @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @CsvBindByName(column = "Nome") @ColumnInfo(name = "nome") var nome: String?,
    @ColumnInfo(name = "departamento_pai")  var departamentoPai : String?
)