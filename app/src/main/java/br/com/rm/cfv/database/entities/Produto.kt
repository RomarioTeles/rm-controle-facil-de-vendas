package br.com.rm.cfv.database.entities

import androidx.room.*

@Entity
data class Produto (
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "nome") var nome: String?,
    @ColumnInfo(name = "codigo") var codigo: String?,
    @ColumnInfo(name = "preco_tabela") var precoTabela: Double?,
    @ColumnInfo(name = "caminho_imagem") var caminhoImagem: String?,
    @Relation(parentColumn = "uid", entityColumn = "departamentoId", entity = Departamento::class)
        @ColumnInfo(name = "departamento_id") var departamentoId: Int?
)