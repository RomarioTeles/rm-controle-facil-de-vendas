package br.com.rm.cfv.database.daos.interfaces

import androidx.room.Dao
import androidx.room.Query
import br.com.rm.cfv.database.entities.dtos.EstoqueDTO
import br.com.rm.cfv.database.entities.dtos.ReportFields
import java.util.*

@Dao
interface ReportDAO {

    @Query("""SELECT dc.id_ref as codigo, dc.nome_ref as descricao, SUM(coalesce(pd.valor, 0)) AS valor
            FROM contapagarreceber dc JOIN pagamentodebito pd ON pd.conta_pagar_receber_id = dc.uid 
            where dc.tipo_ref in ('CLIENTE') AND dc.id_ref > -1 AND coalesce(pd.valor_pago, 0) <= 0 and pd.data_vencimento < :date group by dc.id_ref, dc.nome_ref order by descricao""")
    fun getClientesEmAtraso(date : Date = Date()): List<ReportFields>

    @Query("""SELECT dc.id_ref as codigo, dc.nome_ref as descricao, SUM(coalesce(pd.valor, 0)) AS valor
            FROM contapagarreceber dc JOIN pagamentodebito pd ON pd.conta_pagar_receber_id = dc.uid 
            where dc.tipo_ref in ('CLIENTE') AND dc.id_ref > -1 AND coalesce(pd.valor_pago, 0) <= 0 group by dc.id_ref, dc.nome_ref order by descricao""")
    fun getSaldoClientes(): List<ReportFields>

    @Query("""SELECT  distinct p.codigo AS codigo, p.nome AS descricao,
                            (( SELECT coalesce(SUM(sq.quantidade), 0) FROM estoque sq WHERE sq.tipo = 'ENTRADA' and sq.codigo_produto = p.codigo) - 
                            ( SELECT coalesce(SUM(sq2.quantidade), 0) FROM estoque sq2 WHERE sq2.tipo = 'SAIDA' and sq2.codigo_produto = p.codigo)) AS valor
                    FROM produto p LEFT JOIN estoque e ON p.codigo = e.codigo_produto order by p.nome""" )
    fun getEstoqueProduto(): List<ReportFields>

    @Query("""SELECT  distinct report.codigo, report.descricao, report.valor
                    FROM (
                        SELECT
                                p.codigo AS codigo, p.nome AS descricao,
                                (( SELECT coalesce(SUM(sq.quantidade), 0) FROM estoque sq WHERE sq.tipo = 'ENTRADA' and sq.codigo_produto = p.codigo) - 
                                ( SELECT coalesce(SUM(sq2.quantidade), 0) FROM estoque sq2 WHERE sq2.tipo = 'SAIDA' and sq2.codigo_produto = p.codigo)) AS valor
                        FROM produto p LEFT JOIN estoque e ON p.codigo = e.codigo_produto
                    ) AS report
                    where report.valor <= 0
                    order by report.descricao""" )
    fun getEstoqueBaixoProduto(): List<ReportFields>

    @Query("""SELECT distinct report.codigo as codigo, (report.descricao || ', Qtde: ' || max(report.valor, 0) || ' x ' || report.preco_custo) as descricao, (max(report.valor, 0) * report.preco_custo) as valor
                    FROM (
                        SELECT
                                p.codigo AS codigo, p.nome AS descricao, p.preco_custo as preco_custo,
                                (( SELECT coalesce(SUM(sq.quantidade), 0) FROM estoque sq WHERE sq.tipo = 'ENTRADA' and sq.codigo_produto = p.codigo) - 
                                ( SELECT coalesce(SUM(sq2.quantidade), 0) FROM estoque sq2 WHERE sq2.tipo = 'SAIDA' and sq2.codigo_produto = p.codigo)) AS valor
                        FROM produto p LEFT JOIN estoque e ON p.codigo = e.codigo_produto
                    ) AS report
                    order by report.descricao""" )
    fun getInventarioProdutos(): List<ReportFields>

    @Query("""SELECT dc.codigo as codigo, dc.descricao as descricao, SUM(coalesce(pd.valor, 0)) AS valor
            FROM contapagarreceber dc JOIN pagamentodebito pd ON pd.conta_pagar_receber_id = dc.uid 
            where dc.tipo_ref in ('RECEITAS') AND coalesce(pd.valor_pago, 0) <= 0 and pd.data_vencimento < :date group by dc.codigo, dc.descricao order by descricao""")
    fun getReceitasEmAtraso(date : Date = Date()): List<ReportFields>

    @Query("""SELECT dc.codigo as codigo, dc.descricao as descricao, SUM(coalesce(pd.valor, 0)) AS valor
            FROM contapagarreceber dc JOIN pagamentodebito pd ON pd.conta_pagar_receber_id = dc.uid 
            where dc.tipo_ref in ('DESPESAS') AND coalesce(pd.valor_pago, 0) <= 0 and pd.data_vencimento < :date group by dc.codigo, dc.descricao order by descricao""")
    fun getDespesasEmAtraso(date : Date = Date()): List<ReportFields>

    @Query("""SELECT dc.id_ref as codigo, dc.nome_ref as descricao, SUM(coalesce(pd.valor, 0)) AS valor
            FROM contapagarreceber dc JOIN pagamentodebito pd ON pd.conta_pagar_receber_id = dc.uid 
            where dc.tipo_ref in ('FORNECEDOR') AND dc.id_ref > -1 AND coalesce(pd.valor_pago, 0) <= 0 and pd.data_vencimento < :date group by dc.id_ref, dc.nome_ref order by descricao""")
    fun getFornecedorEmAtraso(date : Date = Date()): List<ReportFields>

}