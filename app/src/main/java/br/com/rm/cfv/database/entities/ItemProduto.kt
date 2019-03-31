package br.com.rm.cfv.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
@ForeignKey(entity = DebitoCliente::class, parentColumns = ["uid"], childColumns = ["debito_cliente_id"], onDelete = ForeignKey.CASCADE)
open class ItemProduto {

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

    fun atualizaSubtotal(){
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


}
