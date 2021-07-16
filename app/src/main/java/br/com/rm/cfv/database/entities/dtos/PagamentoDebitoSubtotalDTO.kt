package br.com.rm.cfv.database.entities.dtos

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import br.com.rm.cfv.constants.StatusPagamento
import java.lang.Exception
import java.util.*

open class PagamentoDebitoSubtotalDTO() : Parcelable{

    @ColumnInfo(name = "id") var id: Int? = null
    @ColumnInfo(name = "descricao") var descricao: String? = null
    @ColumnInfo(name = "valorPago") var valorPago: Double? = null
    @ColumnInfo(name = "total") var total: Double? = null
    @ColumnInfo(name = "dataHora") var dataHora: Long? = null
    @Ignore var tipoRef: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        valorPago = parcel.readValue(Double::class.java.classLoader) as? Double
        total = parcel.readValue(Double::class.java.classLoader) as? Double
        dataHora = parcel.readValue(Long::class.java.classLoader) as? Long
        tipoRef = parcel.readValue(String::class.java.classLoader) as? String
        descricao = parcel.readValue(String::class.java.classLoader) as? String
    }

    fun getStatus(): String{
        if(valorPago!!.compareTo(total!!) >= 0){
            return StatusPagamento.PAGO
        }else{
            return StatusPagamento.PENDENTE
        }
    }

    fun getValorFaltaPagar(): Double?{
        try{
            return total!!.minus(valorPago!!)
        }catch (e: Exception){
            return null
        }
    }

    fun getDataHora(): Date?{
        try{
            return Date(dataHora!!)
        }catch (e: Exception){
            return null
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id!!)
        parcel.writeValue(valorPago)
        parcel.writeValue(total)
        parcel.writeValue(dataHora)
        parcel.writeValue(tipoRef)
        parcel.writeValue(descricao)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PagamentoDebitoSubtotalDTO> {
        override fun createFromParcel(parcel: Parcel): PagamentoDebitoSubtotalDTO {
            return PagamentoDebitoSubtotalDTO(parcel)
        }

        override fun newArray(size: Int): Array<PagamentoDebitoSubtotalDTO?> {
            return arrayOfNulls(size)
        }
    }

}
