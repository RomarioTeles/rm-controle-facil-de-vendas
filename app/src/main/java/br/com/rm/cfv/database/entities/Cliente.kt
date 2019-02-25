package br.com.rm.cfv.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = arrayOf(Index(value = ["cpf"], unique = true))
)
data class Cliente (@PrimaryKey(autoGenerate = true) var uid: Int? = null,
                    @ColumnInfo(name = "nome") var nome: String?,
                    @ColumnInfo(name = "cpf") var cpf: String?,
                    @ColumnInfo(name = "telefone") var telefone: String?,
                    @ColumnInfo(name = "email") var email: String? = "",
                    @ColumnInfo(name = "data_nascimento") var dataNascimento: String? = "",
                    @ColumnInfo(name = "endereco") var endereco: String? = "",
                    @ColumnInfo(name = "numero") var numero: String? = "",
                    @ColumnInfo(name = "complemento") var complemento: String? = "",
                    @ColumnInfo(name = "bairro") var bairro: String? = "",
                    @ColumnInfo(name = "cidade") var cidade: String? = "",
                    @ColumnInfo(name = "uf") var uf: String = ""
)