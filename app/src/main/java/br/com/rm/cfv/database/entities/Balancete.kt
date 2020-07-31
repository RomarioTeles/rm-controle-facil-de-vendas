package br.com.rm.cfv.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    indices = arrayOf(Index(value = ["mes","ano"], unique = true))
)
data class Balancete(
    @PrimaryKey(autoGenerate = true) var uid: Int?,
    @ColumnInfo(name = "mes") var mes: Int?,
    @ColumnInfo(name = "ano") var ano: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(uid)
        parcel.writeValue(mes)
        parcel.writeValue(ano)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Balancete> {
        override fun createFromParcel(parcel: Parcel): Balancete {
            return Balancete(parcel)
        }

        override fun newArray(size: Int): Array<Balancete?> {
            return arrayOfNulls(size)
        }
    }
}