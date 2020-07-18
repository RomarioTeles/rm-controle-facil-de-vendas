package br.com.rm.cfv.database.daos.interfaces

import androidx.room.*
import br.com.rm.cfv.database.entities.PagamentoDebito
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO

@Dao
interface PagamentoDebitoDAO{

    @Query("SELECT pd.debito_cliente_id AS id, SUM(pd.valor_pago) AS valorPago, dc.total AS total, pd.data_hora AS dataHora " +
            "FROM pagamentodebito pd JOIN DebitoCliente dc ON pd.debito_cliente_id = dc.uid " +
            "WHERE pd.debito_cliente_id = :debitoClienteId " +
            "GROUP BY pd.debito_cliente_id")
    fun getSubtotalByDebitoClienteId(debitoClienteId : Int): PagamentoDebitoSubtotalDTO

    @Query("SELECT * FROM pagamentodebito where debito_cliente_id = :debitoClienteId  order by data_hora")
    fun getAll(debitoClienteId : Int): List<PagamentoDebito>

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