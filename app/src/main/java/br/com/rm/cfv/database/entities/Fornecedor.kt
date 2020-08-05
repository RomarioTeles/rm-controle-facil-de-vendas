package br.com.rm.cfv.database.entities

import android.widget.EditText
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.rm.cfv.constants.TipoReferencia
import br.com.rm.cfv.utils.EmailValidate
import br.com.rm.cnpjutils.CNPJUtils
import br.com.rm.cpfutils.CPFUtils
import java.io.Serializable

@Entity(
    indices = arrayOf(Index(value = ["cpf_cnpj"], unique = true))
)
open class Fornecedor (

    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "nome") var nome: String? = null,
    @ColumnInfo(name = "cpf_cnpj") var cpfCnpj: String? = null,
    @ColumnInfo(name = "telefone") var telefone: String? = null,
    @ColumnInfo(name = "email") var email: String? = "",
    @ColumnInfo(name = "endereco") var endereco: String? = "",
    @ColumnInfo(name = "numero") var numero: String? = "",
    @ColumnInfo(name = "complemento") var complemento: String? = "",
    @ColumnInfo(name = "bairro") var bairro: String? = "",
    @ColumnInfo(name = "cidade") var cidade: String? = "",
    @ColumnInfo(name = "uf") var uf: String? = ""

): Serializable, IReferencia{

    val messageNullable : String?
    get() = "%s não pode ser vazio."


    val messageInvalid : String?
    get() = "Informe um(a) %s válido(a)."

    override fun getNomeRef() : String?{
        return nome
    }

    override fun getIdRef() : Int?{
        return uid
    }

    override fun getTipoRef(): String? {
        return TipoReferencia.FORNECEDOR
    }

    fun validate(fields : Map<String, EditText>): Boolean {
        var hasError = false
        if(nome == null || nome!!.isBlank()){
            fields["nome"]!!.error = String.format(messageNullable!!,"Nome")
            hasError = true
        }
        if(cpfCnpj == null || cpfCnpj!!.isBlank()){
            fields["cpfCnpj"]!!.error = String.format(messageNullable!!,"CPF/CNPJ")
            hasError = true
        }

        if(telefone == null || telefone!!.isBlank()){
            fields!!["telefone"]!!.error = String.format(messageNullable!!,"Telefone")
            hasError = true
        }

        if(email != null && email!!.isNotBlank() && !EmailValidate.validate(email!!)){
            fields!!["email"]!!.error = String.format(messageInvalid!!,"E-mail")
            hasError = true
        }

        return !hasError
    }
}