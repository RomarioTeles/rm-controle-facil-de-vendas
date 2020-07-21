package br.com.rm.cfv.database.daos.interfaces

import androidx.room.*
import br.com.rm.cfv.database.entities.ContaPagarReceber
import br.com.rm.cfv.database.entities.dtos.DebitoClienteDTO
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO

@Dao
interface ContaPagarReceberDAO{

    @Query("""SELECT dc.uid AS id, SUM(pd.valor_pago) AS valorPago, dc.total AS total, dc.data_hora AS dataHora  
            FROM contapagarreceber dc JOIN pagamentodebito pd ON pd.conta_pagar_receber_id = dc.uid 
            WHERE dc.id_ref = :idRef 
            GROUP BY dc.uid""" )
    fun getSubtotal(idRef : Int): List<PagamentoDebitoSubtotalDTO>

    @Query("SELECT * FROM contapagarreceber")
    fun getAll(): List<ContaPagarReceber>

    @Query("""SELECT dc.id_ref as id_ref, dc.nome_ref as nome_ref, SUM(coalesce(pd.valor, 0)) AS total 
            FROM contapagarreceber dc JOIN pagamentodebito pd ON pd.conta_pagar_receber_id = dc.uid 
            where dc.id_ref = :idRef AND coalesce(pd.valor_pago, 0) = 0 LIMIT 1""")
    fun findByClienteIdAndStatus(idRef: Int): DebitoClienteDTO

    @Query("SELECT * FROM contapagarreceber WHERE id_ref = :cliente_id")
    fun findByIdRef(cliente_id: Int) : List<ContaPagarReceber>

    @Query("SELECT * FROM contapagarreceber WHERE codigo = :codigo")
    fun findByCodigo(codigo: String) : ContaPagarReceber

    @Query("SELECT * FROM contapagarreceber WHERE uid = :id")
    fun findById(id: Int) : ContaPagarReceber

    @Insert
    fun insertAll(vararg users: ContaPagarReceber)

    @Delete
    fun delete(contapagarreceber: ContaPagarReceber)

    @Update
    fun update(contapagarreceber: ContaPagarReceber)
}