package br.com.rm.cfv.database.daos.interfaces

import androidx.room.Dao
import androidx.room.Query
import br.com.rm.cfv.database.entities.dtos.BarChartItem
import br.com.rm.cfv.database.entities.dtos.ChartGrupoValor
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO
import java.util.*

@Dao
interface ChartsDAO {

    @Query("""SELECT SUM(coalesce(pd.valor, 0)) AS total 
            FROM contapagarreceber c JOIN pagamentodebito pd ON pd.conta_pagar_receber_id = c.uid  AND c.tipo_ref in ('RECEITAS', 'CLIENTE')
            where pd.data_vencimento >= :dataInicial and pd.data_vencimento <= :dataFinal and coalesce(pd.valor_pago, 0) = 0 LIMIT 1""")
    fun getTotalReceber(dataInicial: Long, dataFinal: Long): Double

    @Query("""SELECT pd.meio_pagamento AS grupo, SUM(coalesce(pd.valor, 0)) AS valor 
            FROM contapagarreceber c JOIN pagamentodebito pd ON pd.conta_pagar_receber_id = c.uid  AND c.tipo_ref in ('RECEITAS', 'CLIENTE')
            where pd.data_vencimento >= :dataInicial and pd.data_vencimento <= :dataFinal and coalesce(pd.valor_pago, 0) > 0
            group by pd.meio_pagamento 
            order by valor DESC""")
    fun getTotalPorMeioPagamento(dataInicial: Long, dataFinal: Long): List<ChartGrupoValor>

    @Query("""SELECT c.tipo_pagamento AS grupo, SUM(coalesce(c.total, 0)) AS valor 
            FROM contapagarreceber c 
            where c.data_hora >= :dataInicial and c.data_hora <= :dataFinal
            and c.tipo_ref in ('RECEITAS', 'CLIENTE')
            group by c.tipo_pagamento 
            order by valor DESC""")
    fun getTotalPorTipoPagamento(dataInicial: Long, dataFinal: Long): List<ChartGrupoValor>

    @Query("""SELECT dc.nome_ref AS grupo, SUM(pd.valor) AS valor 
            FROM contapagarreceber dc JOIN pagamentodebito pd ON pd.conta_pagar_receber_id = dc.uid 
            WHERE pd.data_vencimento <= :data and coalesce(pd.valor_pago, 0) = 0
            GROUP BY dc.nome_ref
            order by dc.nome_ref""" )
    fun getClientesComAtrasoNoPagamento(data: Long): List<ChartGrupoValor>

    @Query("""SELECT SUM(coalesce(pd.valor, 0)) AS axisY, pd.data_vencimento AS axisX
            FROM contapagarreceber r JOIN pagamentodebito pd ON r.uid = pd.conta_pagar_receber_id and r.tipo_ref in ('RECEITAS', 'CLIENTE')
            where pd.data_vencimento >= :dataInicial and pd.data_vencimento <= :dataFinal and coalesce(pd.valor_pago, 0) = 0 
            Group by axisX
            Order by axisX """)
    fun getTotalReceberAgrupadoPorMes(dataInicial: Date, dataFinal: Date): List<BarChartItem>

    @Query("""SELECT SUM(coalesce(pd.valor, 0)) AS axisY, pd.data_vencimento AS axisX
            FROM contapagarreceber r JOIN pagamentodebito pd ON r.uid = pd.conta_pagar_receber_id and r.tipo_ref in ('DESPESAS', 'FORNECEDOR')
            where pd.data_vencimento >= :dataInicial and pd.data_vencimento <= :dataFinal and coalesce(pd.valor_pago, 0) = 0 
            Group by axisX
            Order by axisX """)
    fun getTotalPagarAgrupadoPorMes(dataInicial: Date, dataFinal: Date): List<BarChartItem>

}