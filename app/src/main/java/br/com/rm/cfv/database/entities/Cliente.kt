package br.com.rm.cfv.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.rm.cfv.constants.TipoReferencia
import br.com.rm.cfv.utils.EmailValidate
import br.com.rm.dateutils.DateFormatUtils
import br.com.rm.dateutils.DateOperationsUtils
import com.google.android.material.textfield.TextInputLayout
import com.opencsv.bean.CsvBindByName
import java.util.*

@Entity
open class Cliente (@PrimaryKey(autoGenerate = true) var uid: Int? = null,
                    @CsvBindByName(column = "Nome") @ColumnInfo(name = "nome") var nome: String?,
                    @CsvBindByName(column = "CPF") @ColumnInfo(name = "cpf") var cpf: String?,
                    @CsvBindByName(column = "Telefone") @ColumnInfo(name = "telefone") var telefone: String?,
                    @CsvBindByName(column = "E_mail") @ColumnInfo(name = "email") var email: String? = "",
                    @CsvBindByName(column = "Data Nascimento") @ColumnInfo(name = "data_nascimento") var dataNascimento: String? = "",
                    @CsvBindByName(column = "Endereco") @ColumnInfo(name = "endereco") var endereco: String? = "",
                    @CsvBindByName(column = "Numero") @ColumnInfo(name = "numero") var numero: String? = "",
                    @CsvBindByName(column = "Complemento") @ColumnInfo(name = "complemento") var complemento: String? = "",
                    @CsvBindByName(column = "Bairro") @ColumnInfo(name = "bairro") var bairro: String? = "",
                    @CsvBindByName(column = "Cidade") @ColumnInfo(name = "cidade") var cidade: String? = "",
                    @CsvBindByName(column = "UF") @ColumnInfo(name = "uf") var uf: String? = ""
) : Parcelable, IReferencia{
    
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
        return TipoReferencia.CLIENTE.name
    }

    fun validate(fields : Map<String, TextInputLayout>): Boolean {
        var hasError = false
        if(nome == null || nome!!.isBlank()){
            fields["nome"]!!.error = String.format(messageNullable!!,"Nome")
            hasError = true
        }
        /*if(cpf == null || cpf!!.isBlank()){
            fields["cpf"]!!.error = String.format(messageNullable!!,"CPF")
            hasError = true
        }*/

        if(telefone == null || telefone!!.isBlank()){
            fields["telefone"]!!.error = String.format(messageNullable!!,"Telefone")
            hasError = true
        }

        if(email != null && email!!.isNotBlank() && !EmailValidate.validate(email!!)){
            fields["email"]!!.error = String.format(messageInvalid!!,"E-mail")
            hasError = true
        }

        if(dataNascimento != null && dataNascimento!!.isNotBlank()){
            try {
                var data: Date = DateFormatUtils.getDateFromString(dataNascimento)
                var dtNow = Date()
                dtNow = DateOperationsUtils.removeTimeFromDate(dtNow)
                if (data == null) {
                    fields["dataNascimento"]!!.error = String.format(messageNullable!!, "Data de nascimento")
                    hasError = true
                } else {
                    if(dtNow == data){
                        fields["dataNascimento"]!!.error = String.format(messageInvalid!!, "Data de nascimento")
                        hasError = true
                    }
                    else if (DateOperationsUtils.getMin(data, dtNow) == dtNow) {
                        fields["dataNascimento"]!!.error = String.format(messageInvalid!!, "Data de nascimento")
                        hasError = true
                    }
                }
            }catch (e : Exception){
                fields["dataNascimento"]!!.error = String.format(messageInvalid!!, "Data de nascimento")
                hasError = true
            }
        }

        return !hasError
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(uid)
        parcel.writeString(nome)
        parcel.writeString(cpf)
        parcel.writeString(telefone)
        parcel.writeString(email)
        parcel.writeString(dataNascimento)
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

    companion object CREATOR : Parcelable.Creator<Cliente> {
        override fun createFromParcel(parcel: Parcel): Cliente {
            return Cliente(parcel)
        }

        override fun newArray(size: Int): Array<Cliente?> {
            return arrayOfNulls(size)
        }
    }
}