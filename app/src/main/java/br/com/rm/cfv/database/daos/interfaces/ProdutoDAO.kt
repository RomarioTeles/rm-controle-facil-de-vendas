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

    @Query("SELECT * FROM produto WHERE codigo LIKE :codigo LIMIT 1")
    fun findByCodigo(codigo: String) : Produto

    @Query("SELECT * FROM produto WHERE uid = :id LIMIT 1")
    fun findById(id: Int) : Produto

    @Query("SELECT COUNT(*) FROM produto")
    fun count(): Int

    @Insert
    fun insertAll(vararg users: Produto)

    @Delete
    fun delete(produto: Produto)

    @Update
    fun update(produto: Produto)

    @Query("""SELECT * FROM produto p 
                   WHERE (p.nome LIKE :search or p.codigo LIKE :search) 
                   order by codigo, nome""")
    fun search(search: String?): List<Produto>

    @Query("""SELECT * FROM produto p WHERE (p.nome LIKE :search or p.codigo LIKE :search)
            and p.codigo in (
                SELECT _estoque.codigo 
                FROM (
                    SELECT p2.codigo, p2.permiteEstoqueNegativo,
                    ((SELECT COALESCE(SUM(sq.quantidade), 0) FROM estoque sq WHERE sq.tipo = 'ENTRADA' and sq.codigo_produto = p2.codigo) - 
                    ( SELECT COALESCE(SUM(sq2.quantidade), 0) FROM estoque sq2 WHERE sq2.tipo = 'SAIDA' and sq2.codigo_produto = p2.codigo)) AS total
                    FROM produto p2 LEFT JOIN estoque e ON p2.codigo = e.codigo_produto 
                ) AS _estoque WHERE _estoque.total > 0 or _estoque.permiteEstoqueNegativo = 1
            )
            order by codigo, nome""")
    fun searchComEstoquePositivo(search: String?): List<Produto>
}