package br.com.rm.cfv.database.daos.interfaces

import androidx.room.*
import br.com.rm.cfv.database.entities.PagamentoDebito
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO

@Dao
interface PagamentoDebitoDAO{

    @Query("SELECT SUM(pd.valor_pago) AS valorPago, dc.total AS total  FROM pagamentodebito pd JOIN DebitoCliente dc ON pd.debito_cliente_id = dc.uid where pd.debito_cliente_id = :debitoClienteId")
    fun getSubtotal(debitoClienteId : Int): List<PagamentoDebitoSubtotalDTO>

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