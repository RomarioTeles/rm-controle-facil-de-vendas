package br.com.rm.cfv.database.daos.interfaces

import androidx.room.*
import br.com.rm.cfv.database.entities.Fornecedor

@Dao
interface FornecedorDAO {

    @Query("SELECT * FROM fornecedor order by nome")
    fun getAll(): List<Fornecedor>

    @Query("SELECT * FROM fornecedor WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Fornecedor>

    @Query("SELECT * FROM fornecedor WHERE cidade = :cidade AND uf = :uf")
    fun findAllByCidadeAndUf(cidade: String, uf : String): List<Fornecedor>

    @Query("SELECT * FROM fornecedor WHERE nome LIKE :first LIMIT 1")
    fun findByNome(first: String) : Fornecedor

    @Query("SELECT * FROM fornecedor WHERE uid = :id LIMIT 1")
    fun findById(id: Int) : Fornecedor

    @Query("SELECT * FROM fornecedor WHERE cpf_cnpj LIKE :cpfCnpj LIMIT 1")
    fun findCpfCnpj(cpfCnpj: String) : Fornecedor

    @Insert
    fun insert(fornecedor: Fornecedor) : Long

    @Update
    fun update(fornecedor: Fornecedor) : Int

    @Delete
    fun delete(fornecedor: Fornecedor)

    @Query("SELECT * FROM fornecedor WHERE nome LIKE :search or cpf_cnpj LIKE :search order by nome")
    fun search(search: String?): List<Fornecedor>
}
