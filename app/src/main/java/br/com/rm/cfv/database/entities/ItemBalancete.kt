package br.com.rm.cfv.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.com.rm.cfv.constants.TipoItemBalancete
import java.util.*

@Entity
@ForeignKey(entity = Balancete::class, parentColumns = ["uid"], childColumns = ["balancete_id"], onDelete = ForeignKey.SET_NULL)
data class ItemBalancete(
    @PrimaryKey(autoGenerate = true) var uid: Int?,
    @ColumnInfo(name = "data_hora") var dataHora: Date?,
    @ColumnInfo(name = "tipo") var tipo: String = TipoItemBalancete.RECEITA,
    @ColumnInfo(name = "valor") var valor: Double = 0.0,
    @ColumnInfo(name = "nome_ref") var nomeRef: String? = null,
    @ColumnInfo(name = "id_ref") var idRef: Int? = null,
    @ColumnInfo(name = "balancete_id")  var balanceteId : Int? = null
)