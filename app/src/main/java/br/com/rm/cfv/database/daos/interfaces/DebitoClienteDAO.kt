package br.com.rm.cfv.database.daos.interfaces

import androidx.room.*
import br.com.rm.cfv.database.entities.DebitoCliente
import br.com.rm.cfv.database.entities.dtos.DebitoClienteDTO
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO

@Dao
interface DebitoClienteDAO{

    @Query("SELECT dc.uid AS id, SUM(pd.valor_pago) AS valorPago, dc.total AS total, dc.data_hora AS dataHora  " +
            "FROM DebitoCliente dc JOIN pagamentodebito pd ON pd.debito_cliente_id = dc.uid " +
            "WHERE dc.cliente_id = :clienteId " +
            "GROUP BY dc.uid" )
    fun getSubtotal(clienteId : Int): List<PagamentoDebitoSubtotalDTO>

    @Query("SELECT * FROM debitocliente")
    fun getAll(): List<DebitoCliente>

    @Query("SELECT dc.cliente_id as cliente_id, dc.cliente_nome as nome, SUM(coalesce(pd.valor, 0)) AS total  " +
            "FROM DebitoCliente dc JOIN pagamentodebito pd ON pd.debito_cliente_id = dc.uid " +
            "where dc.cliente_id = :cliente_id AND coalesce(pd.valor_pago, 0) = 0 LIMIT 1")
    fun findByClienteIdAndStatus(cliente_id: Int): DebitoClienteDTO

    @Query("SELECT * FROM debitocliente WHERE cliente_id = :cliente_id")
    fun findByClienteId(cliente_id: Int) : List<DebitoCliente>

    @Query("SELECT * FROM debitocliente WHERE codigo = :codigo")
    fun findByCodigo(codigo: String) : DebitoCliente

    @Query("SELECT * FROM debitocliente WHERE uid = :id")
    fun findById(id: Int) : DebitoCliente

    @Insert
    fun insertAll(vararg users: DebitoCliente)

    @Delete
    fun delete(debitocliente: DebitoCliente)

    @Update
    fun update(debitocliente: DebitoCliente)
}