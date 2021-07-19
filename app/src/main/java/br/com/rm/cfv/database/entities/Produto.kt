package br.com.rm.cfv.database.entities

import androidx.room.*
import com.google.android.material.textfield.TextInputLayout
import com.google.common.base.Strings
import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.CsvIgnore
import java.io.Serializable
import java.util.*

@Entity(
    indices = arrayOf(Index(value = ["codigo"], unique = true))
)
@ForeignKey(entity = Departamento::class, parentColumns = ["nome"], childColumns = ["departamento"])
open class Produto (
    @CsvBindByName(column="uid") @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @CsvBindByName(column="nome") @ColumnInfo(name = "nome") var nome: String?,
    @CsvBindByName(column="codigo") @ColumnInfo(name = "codigo") var codigo: String? = UUID.randomUUID().toString(),
    @CsvBindByName(column="preco custo") @ColumnInfo(name = "preco_custo") var precoCusto: Double?,
    @CsvBindByName(column="preco venda") @ColumnInfo(name = "preco_venda") var precoVenda: Double?,
    @CsvBindByName(column="caminho imagem") @ColumnInfo(name = "caminho_imagem") var caminhoImagem: String?,
    @CsvBindByName(column="departamento") @ColumnInfo(name = "departamento") var departamento: String,
    @CsvBindByName(column="permite estoque negativo") @ColumnInfo(name = "permiteEstoqueNegativo") var permiteEstoqueNegativo : Boolean = false
) : Serializable{

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

        if(precoVenda == null){
            fields.getValue("precoVenda").error = String.format(messageNullable!!,"Preço de Revenda")
            hasError = true
        }

        if(precoCusto == null){
            fields.getValue("precoCusto").error = String.format(messageNullable!!,"Preço de Custo")
            hasError = true
        }

        if(departamento.isBlank()){
            fields.getValue("departamento").error = String.format(messageNullable!!,"Departamento")
            hasError = true
        }

        return !hasError
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}