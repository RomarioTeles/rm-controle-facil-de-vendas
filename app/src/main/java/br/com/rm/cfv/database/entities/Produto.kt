package br.com.rm.cfv.database.entities

import androidx.room.*
import com.google.android.material.textfield.TextInputLayout

@Entity(
    indices = arrayOf(Index(value = ["codigo"], unique = true))
)
@ForeignKey(entity = Departamento::class, parentColumns = ["nome"], childColumns = ["categoria"])
open class Produto (
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "nome") var nome: String?,
    @ColumnInfo(name = "codigo") var codigo: String?,
    @ColumnInfo(name = "preco_tabela") var precoTabela: Double?,
    @ColumnInfo(name = "preco_custo") var precoCusto: Double?,
    @ColumnInfo(name = "preco_venda") var precoVenda: Double?,
    @ColumnInfo(name = "caminho_imagem") var caminhoImagem: String?,
    @ColumnInfo(name = "categoria") var categoria: String
){
    val messageNullable : String?
        get() = "%s não pode ser vazio."


    val messageInvalid : String?
        get() = "Informe um(a) %s válido(a)."

    fun validate(fields : Map<String, TextInputLayout>): Boolean {
        var hasError = false
        if(nome == null || nome!!.isBlank()){
            fields.getValue("nome").error = String.format(messageNullable!!,"Nome")
            hasError = true
        }
        if(codigo == null || codigo!!.isBlank()){
            fields.getValue("codigo").error = String.format(messageNullable!!,"Código")
            hasError = true
        }

        if(precoTabela == null){
            fields.getValue("precoTabela").error = String.format(messageNullable!!,"Preço de Tabela")
            hasError = true
        }

        if(precoVenda == null){
            fields.getValue("precoVenda").error = String.format(messageNullable!!,"Preço de Revenda")
            hasError = true
        }

        if(precoCusto == null){
            fields.getValue("precoCusto").error = String.format(messageNullable!!,"Preço de Custo")
            hasError = true
        }

        if(categoria == null || categoria!!.isBlank()){
            fields.getValue("categoria").error = String.format(messageNullable!!,"Departamento")
            hasError = true
        }

        return !hasError
    }
}