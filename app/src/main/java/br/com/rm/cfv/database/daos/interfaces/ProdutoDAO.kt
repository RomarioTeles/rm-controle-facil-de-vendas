package br.com.rm.cfv.database.daos.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.com.rm.cfv.database.entities.Produto

@Dao
interface ProdutoDAO{
    
    @Query("SELECT * FROM produto")
    fun getAll(): List<Produto>

    @Query("SELECT * FROM produto WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Produto>

    @Query("SELECT * FROM produto WHERE nome LIKE :first LIMIT 1")
    fun findByNome(first: String) : Produto

    @Query("SELECT * FROM produto WHERE codigo LIKE :codigo LIMIT 1")
    fun findByCodigo(codigo: String) : Produto

    @Insert
    fun insertAll(vararg users: Produto)

    @Delete
    fun delete(produto: Produto)
}