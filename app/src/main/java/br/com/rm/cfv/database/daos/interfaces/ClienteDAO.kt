package br.com.rm.cfv.database.daos.interfaces

import androidx.room.*
import br.com.rm.cfv.database.entities.Cliente
import br.com.rm.cfv.database.entities.Fornecedor

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

    @Query("SELECT * FROM cliente WHERE uid = :id LIMIT 1")
    fun findById(id: Int) : Cliente

    @Query("SELECT * FROM cliente WHERE cpf LIKE :cpf LIMIT 1")
    fun findCpf(cpf: String) : Cliente

    @Insert
    fun insertAll(vararg users: Cliente)

    @Update
    fun update(cliente: Cliente)

    @Delete
    fun delete(cliente: Cliente)

    @Query("SELECT * FROM cliente WHERE nome LIKE :search or cpf LIKE :search order by nome")
    fun search(search: String?): List<Cliente>
}
