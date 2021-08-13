package br.com.rm.cfv.database.entities

import android.os.Parcel
import android.os.Parcelable

class DefaultReferencia(
    private var id: Int?,
    private var nome: String?,
    private var tipo: String?
) : IReferencia, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    )

    override fun getNomeRef(): String? {
        return nome
    }

    override fun getIdRef(): Int? {
        return id
    }

    override fun getTipoRef(): String? {
        return tipo
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(nome)
        parcel.writeString(tipo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DefaultReferencia> {
        override fun createFromParcel(parcel: Parcel): DefaultReferencia {
            return DefaultReferencia(parcel)
        }

        override fun newArray(size: Int): Array<DefaultReferencia?> {
            return arrayOfNulls(size)
        }
    }

}