package br.com.rm.cfv.database.entities

import androidx.room.*
import br.com.rm.cfv.constants.TipoMovimentacaoEstoque
import br.com.rm.dateutils.DateFormatUtils
import com.google.android.material.textfield.TextInputLayout
import java.util.*

@Entity(tableName = "estoque")
@ForeignKey(entity = Produto::class, parentColumns = ["codigo"], childColumns = ["codigo_produto"])
open class MovimentacaoEstoque(
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "codigo_produto") var codigo_produto: String,
    @ColumnInfo(name = "motivo") var motivo: String?,
    @ColumnInfo(name = "data_hora") var dataHora: Long?,
    @ColumnInfo(name = "tipo") var tipo: String = TipoMovimentacaoEstoque.ENTRADA,
    @ColumnInfo(name = "quantidade") var quantidade: Int = 0
    ){

    fun parseDataHoraToDate() : Date{
        var date =  Date()
        date.time = dataHora!!
        return date
    }

    fun formatDataHoraToDate() : String{
        var date = parseDataHoraToDate()
        return DateFormatUtils.format(date, "dd/MM/yyyy HH:mm")
    }

    val messageNullable : String?
        get() = "%s não pode ser vazio."


    val messageInvalid : String?
        get() = "Informe um(a) %s válido(a)."

    fun validate(fields : Map<String, TextInputLayout>): Boolean {
        var hasError = false

        if(dataHora == null){
            dataHora = Date().time
        }

        if(motivo == null || motivo!!.isBlank()){
            fields.getValue("motivo").error = String.format(messageNullable!!,"Motivo")
            hasError = true
        }

        if(codigo_produto.isBlank()){
            fields.getValue("codigo_produto").error = String.format(messageNullable!!,"Código do Produto")
            hasError = true
        }

        if(quantidade < 0){
            fields.getValue("quantidade").error = String.format(messageInvalid!!,"Quantidade")
            hasError = true
        }

        return !hasError
    }
}