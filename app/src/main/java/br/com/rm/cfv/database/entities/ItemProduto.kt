package br.com.rm.cfv.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
@ForeignKey(entity = ContaPagarReceber::class, parentColumns = ["uid"], childColumns = ["debito_cliente_id"], onDelete = ForeignKey.CASCADE)
open class ItemProduto() : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var uid: Int? = null
    @ColumnInfo(name = "debito_cliente_id")
    var debitoClienteId: Int? = null
    @ColumnInfo(name = "nome_produto")
    var nomeProduto: String? = null
    @ColumnInfo(name = "codigo_produto")
    var codigoProduto: String? = null
    @ColumnInfo(name = "preco_unitario")
    var precoUnitario: Double = 0.0
    @ColumnInfo(name = "quantidade")
    private var quantidade: Int = 1
    @ColumnInfo(name = "subtotal")
    var subtotal: Double = 0.0
    @ColumnInfo(name = "desconto")
    private var desconto: Double = 0.0
    @ColumnInfo(name = "acrescimo")
    private var acrescimo: Double = 0.0

    constructor(parcel: Parcel) : this() {
        uid = parcel.readValue(Int::class.java.classLoader) as? Int
        debitoClienteId = parcel.readValue(Int::class.java.classLoader) as? Int
        nomeProduto = parcel.readString()
        codigoProduto = parcel.readString()
        precoUnitario = parcel.readDouble()
        quantidade = parcel.readInt()
        subtotal = parcel.readDouble()
        desconto = parcel.readDouble()
        acrescimo = parcel.readDouble()
    }

    fun atualizaSubtotal(){
        this.subtotal = (quantidade * precoUnitario) + acrescimo - desconto
    }

    fun atualizaSubtotalDeCusto(){
        this.subtotal = (quantidade * precoUnitario) + acrescimo - desconto
    }

    fun setQuantidade(quantidade : Int){
        this.quantidade = quantidade
        atualizaSubtotal()
    }

    fun setDesconto(desconto : Double){
        this.desconto = desconto
        this.subtotal = this.subtotal - desconto
    }

    fun setAcrescimo(acrescimo : Double){
        this.acrescimo = acrescimo
        this.subtotal = this.subtotal + acrescimo
    }

    fun getAcrescimo(): Double {
        return acrescimo
    }

    fun getDesconto() : Double{
        return desconto
    }

    fun getQuantidade() : Int{
        return quantidade
    }

    fun getDescAcresc(): Double? {
        return acrescimo - desconto
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemProduto

        if (nomeProduto != other.nomeProduto) return false
        if (codigoProduto != other.codigoProduto) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nomeProduto?.hashCode() ?: 0
        result = 31 * result + (codigoProduto?.hashCode() ?: 0)
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(uid)
        parcel.writeValue(debitoClienteId)
        parcel.writeString(nomeProduto)
        parcel.writeString(codigoProduto)
        parcel.writeDouble(precoUnitario)
        parcel.writeInt(quantidade)
        parcel.writeDouble(subtotal)
        parcel.writeDouble(desconto)
        parcel.writeDouble(acrescimo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemProduto> {
        override fun createFromParcel(parcel: Parcel): ItemProduto {
            return ItemProduto(parcel)
        }

        override fun newArray(size: Int): Array<ItemProduto?> {
            return arrayOfNulls(size)
        }
    }
}
