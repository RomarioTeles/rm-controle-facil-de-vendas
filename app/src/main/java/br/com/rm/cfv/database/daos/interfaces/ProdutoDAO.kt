package br.com.rm.cfv.database.daos.interfaces

import androidx.room.*
import br.com.rm.cfv.database.entities.Produto

@Dao
interface ProdutoDAO{
    
    @Query("SELECT * FROM produto order by nome")
    fun getAll(): List<Produto>

    @Query("SELECT * FROM produto WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Produto>

    @Query("SELECT * FROM produto WHERE nome LIKE :first LIMIT 1")
    fun findByNome(first: String) : Produto

    @Query("SELECT * FROM produto WHERE codigo LIKE :codigo or UPPER(nome) LIKE UPPER(:codigo) LIMIT 1")
    fun findByCodigo(codigo: String) : Produto

    @Query("SELECT * FROM produto WHERE uid = :id LIMIT 1")
    fun findById(id: Int) : Produto

    @Insert
    fun insertAll(vararg users: Produto)

    @Delete
    fun delete(produto: Produto)

    @Update
    fun update(produto: Produto)

    @Query("SELECT * FROM produto WHERE nome LIKE :search or codigo LIKE :search order by codigo, nome")
    fun search(search: String?): List<Produto>
}