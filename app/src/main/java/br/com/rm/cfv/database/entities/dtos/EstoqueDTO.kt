package br.com.rm.cfv.database.entities.dtos

import androidx.room.ColumnInfo

open class EstoqueDTO(
    @ColumnInfo(name = "uid") var uid: Int?,
    @ColumnInfo(name = "nome") var nome: String?,
    @ColumnInfo(name = "codigo") var codigo: String?,
    @ColumnInfo(name = "qtdEntrada") var qtdEntrada: Int?,
    @ColumnInfo(name = "qtdSaida") var qtdSaida: Int?
    ){

    fun quantidade() : Int{
        if(qtdEntrada == null) qtdEntrada = 0
        if(qtdSaida == null) qtdSaida = 0

        return qtdEntrada!! - qtdSaida!!
    }

    override fun toString(): String {
        return "$codigo | $nome"
    }
}