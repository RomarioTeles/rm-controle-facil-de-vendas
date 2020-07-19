package br.com.rm.cfv.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    indices = arrayOf(Index(value = ["mes","ano"], unique = true))
)
data class Balancete(
    @PrimaryKey(autoGenerate = true) var uid: Int?,
    @ColumnInfo(name = "mes") var mes: Int?,
    @ColumnInfo(name = "ano") var ano: Int?
) : Serializable {}