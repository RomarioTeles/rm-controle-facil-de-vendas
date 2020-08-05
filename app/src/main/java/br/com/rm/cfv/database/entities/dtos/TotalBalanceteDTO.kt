package br.com.rm.cfv.database.entities.dtos

import androidx.room.ColumnInfo
import br.com.rm.numberUtils.DecimalFormatUtils

open class TotalBalanceteDTO (
    @ColumnInfo(name = "total_despesas") var totalDespesas: Double?,
    @ColumnInfo(name = "total_receitas") var totalReceitas: Double?
){

    fun total() : Double{
        if(totalDespesas == null) totalDespesas = 0.0
        if(totalReceitas == null) totalReceitas = 0.0

        return totalReceitas!!.minus(totalDespesas!!)
    }

    /*fun getTotalDespesas(): Double{
        return if (totalDespesas == null) 0.0 else totalDespesas!!
    }

    fun getTotalReceitas(): Double{
        return if (totalReceitas == null) 0.0 else totalReceitas!!
    }*/

    override fun toString(): String {
        return DecimalFormatUtils.decimalFormatPtBR(total())
    }
}
