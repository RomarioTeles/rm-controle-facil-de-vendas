package br.com.rm.cfv.database.entities.dtos

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.opencsv.bean.CsvBindByName

open class EstoqueDTO(
    @ColumnInfo(name = "uid") var uid: Int?,
    @CsvBindByName(column="Produto")@ColumnInfo(name = "nome") var nome: String?,
    @CsvBindByName(column="Codigo") @ColumnInfo(name = "codigo") var codigo: String?,
    @CsvBindByName(column="Qtde. Entrada") @ColumnInfo(name = "qtdEntrada") var qtdEntrada: Int?,
    @CsvBindByName(column="Qtde. Saida") @ColumnInfo(name = "qtdSaida") var qtdSaida: Int?,
    @ColumnInfo(name = "image_path") var imagePath: String?
    ){

    @CsvBindByName(column = "Total") @Ignore var total: Int = quantidade()

    fun quantidade() : Int{
        if(qtdEntrada == null) qtdEntrada = 0
        if(qtdSaida == null) qtdSaida = 0

        return qtdEntrada!! - qtdSaida!!
    }

    override fun toString(): String {
        return "$codigo | $nome"
    }
}