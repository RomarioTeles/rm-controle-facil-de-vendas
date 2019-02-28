package br.com.rm.cfv.database.entities

import android.widget.EditText
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.rm.cfv.utils.EmailValidate
import br.com.rm.cpfutils.CPFUtils
import br.com.rm.dateutils.DateFormatUtils
import br.com.rm.dateutils.DateOperationsUtils
import java.lang.Exception
import java.util.*

@Entity(
    indices = arrayOf(Index(value = ["cpf"], unique = true))
)
open class Cliente (@PrimaryKey(autoGenerate = true) var uid: Int? = null,
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
){
    val messageNullable : String?
    get() = "%s não pode ser vazio."


    val messageInvalid : String?
    get() = "Informe um(a) %s válido(a)."

    fun validate(fields : Map<String, EditText>): Boolean {
        var hasError = false
        if(nome == null || nome!!.isBlank()){
            fields!!["nome"]!!.error = String.format(messageNullable!!,"Nome")
            hasError = true
        }
        if(cpf == null || cpf!!.isBlank()){
            fields!!["cpf"]!!.error = String.format(messageNullable!!,"CPF")
            hasError = true
        }else if(!CPFUtils.isCPF(cpf)){
            fields!!["cpf"]!!.error = String.format(messageInvalid!!,"CPF")
            hasError = true
        }

        if(telefone == null || telefone!!.isBlank()){
            fields!!["telefone"]!!.error = String.format(messageNullable!!,"Telefone")
            hasError = true
        }

        if(email == null || email!!.isBlank()){
            fields!!["email"]!!.error = String.format(messageNullable!!,"E-mail")
            hasError = true
        }else if(!EmailValidate.validate(email!!)){
            fields!!["email"]!!.error = String.format(messageInvalid!!,"E-mail")
            hasError = true
        }

        if(dataNascimento != null && dataNascimento!!.isNotBlank()){
            try {
                var data: Date = DateFormatUtils.getDateFromString(dataNascimento)
                var dtNow = Date()
                dtNow = DateOperationsUtils.removeTimeFromDate(dtNow)
                if (data == null) {
                    fields!!["dataNascimento"]!!.error = String.format(messageNullable!!, "Data de nascimento")
                    hasError = true
                } else {
                    if(dtNow == data){
                        fields!!["dataNascimento"]!!.error = String.format(messageInvalid!!, "Data de nascimento")
                        hasError = true
                    }
                    else if (DateOperationsUtils.getMin(data, dtNow) == dtNow) {
                        fields!!["dataNascimento"]!!.error = String.format(messageInvalid!!, "Data de nascimento")
                        hasError = true
                    }
                }
            }catch (e : Exception){
                fields!!["dataNascimento"]!!.error = String.format(messageInvalid!!, "Data de nascimento")
                hasError = true
            }
        }

        return !hasError
    }
}