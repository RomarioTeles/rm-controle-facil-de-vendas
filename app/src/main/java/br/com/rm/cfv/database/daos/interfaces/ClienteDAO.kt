package br.com.rm.cfv.database.daos.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.com.rm.cfv.database.entities.Cliente

@Dao
public interface ClienteDAO {

    @Query("SELECT * FROM cliente")
    fun getAll(): List<Cliente>

    @Query("SELECT * FROM cliente WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Cliente>

    @Query("SELECT * FROM cliente WHERE cidade = :cidade AND uf = :uf")
    fun findAllByCidadeAndUf(cidade: String, uf : String): List<Cliente>

    @Query("SELECT * FROM cliente WHERE nome LIKE :first LIMIT 1")
    fun findByNome(first: String) : Cliente

    @Query("SELECT * FROM cliente WHERE cpf LIKE :cpf LIMIT 1")
    fun findCpf(cpf: String) : Cliente

    @Insert
    fun insertAll(vararg users: Cliente)

    @Delete
    fun delete(cliente: Cliente)
}
