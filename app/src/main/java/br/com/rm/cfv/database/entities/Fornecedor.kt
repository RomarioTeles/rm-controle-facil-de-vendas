package br.com.rm.cfv.database.entities

import android.os.Parcel
import android.os.Parcelable
import android.widget.EditText
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.rm.cfv.constants.TipoReferencia
import br.com.rm.cfv.utils.EmailValidate
import br.com.rm.cnpjutils.CNPJUtils
import br.com.rm.cpfutils.CPFUtils
import com.opencsv.bean.CsvBindByName
import java.io.Serializable

@Entity(
    indices = arrayOf(Index(value = ["cpf_cnpj"], unique = true))
)
open class Fornecedor (

    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @CsvBindByName(column = "Nome") @ColumnInfo(name = "nome") var nome: String? = null,
    @CsvBindByName(column = "CPF_CNPJ") @ColumnInfo(name = "cpf_cnpj") var cpfCnpj: String? = null,
    @CsvBindByName(column = "Telefone") @ColumnInfo(name = "telefone") var telefone: String? = null,
    @CsvBindByName(column = "E_mail") @ColumnInfo(name = "email") var email: String? = "",
    @CsvBindByName(column = "Endereco") @ColumnInfo(name = "endereco") var endereco: String? = "",
    @CsvBindByName(column = "Numero")  @ColumnInfo(name = "numero") var numero: String? = "",
    @CsvBindByName(column = "Complemento") @ColumnInfo(name = "complemento") var complemento: String? = "",
    @CsvBindByName(column = "Bairro") @ColumnInfo(name = "bairro") var bairro: String? = "",
    @CsvBindByName(column = "Cidade")  @ColumnInfo(name = "cidade") var cidade: String? = "",
    @CsvBindByName(column = "UF") @ColumnInfo(name = "uf") var uf: String? = ""

): Parcelable, IReferencia{

    val messageNullable : String?
    get() = "%s não pode ser vazio."


    val messageInvalid : String?
    get() = "Informe um(a) %s válido(a)."

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(uid)
        parcel.writeString(nome)
        parcel.writeString(cpfCnpj)
        parcel.writeString(telefone)
        parcel.writeString(email)
        parcel.writeString(endereco)
        parcel.writeString(numero)
        parcel.writeString(complemento)
        parcel.writeString(bairro)
        parcel.writeString(cidade)
        parcel.writeString(uf)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Fornecedor> {
        override fun createFromParcel(parcel: Parcel): Fornecedor {
            return Fornecedor(parcel)
        }

        override fun newArray(size: Int): Array<Fornecedor?> {
            return arrayOfNulls(size)
        }
    }
}