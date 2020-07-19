package br.com.rm.cfv.database.daos.interfaces

import androidx.room.*
import br.com.rm.cfv.database.entities.PagamentoDebito
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO

@Dao
interface PagamentoDebitoDAO{

    @Query("""SELECT pd.conta_pagar_receber_id AS id, SUM(pd.valor_pago) AS valorPago, dc.total AS total, pd.data_hora AS dataHora 
            FROM pagamentodebito pd JOIN ContaPagarReceber dc ON pd.conta_pagar_receber_id = dc.uid 
            WHERE pd.conta_pagar_receber_id = :contaPagarReceberId 
            GROUP BY pd.conta_pagar_receber_id""")
    fun getSubtotalByDebitoClienteId(contaPagarReceberId : Int): PagamentoDebitoSubtotalDTO

    @Query("SELECT * FROM pagamentodebito where conta_pagar_receber_id = :contaPagarReceberId  order by data_hora")
    fun getAll(contaPagarReceberId : Int): List<PagamentoDebito>

    @Query("SELECT * FROM pagamentodebito")
    fun getAll(): List<PagamentoDebito>

    @Query("SELECT * FROM PagamentoDebito WHERE codigo = :codigo")
    fun findByCodigo(codigo: String) : PagamentoDebito

    @Insert
    fun insertAll(vararg pagamentoDebito: PagamentoDebito)

    @Delete
    fun delete(pagamentoDebito: PagamentoDebito)

    @Update
    fun update(pagamentoDebito: PagamentoDebito)
}