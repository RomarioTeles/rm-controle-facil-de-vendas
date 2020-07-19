package br.com.rm.cfv.database.daos.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.com.rm.cfv.database.entities.ItemProduto

@Dao
public interface ItemProdutoDAO {

    @Query("SELECT * FROM itemproduto WHERE debito_cliente_id = :contaPagarReceberId")
    fun getAll(contaPagarReceberId: Int): List<ItemProduto>

    @Query("SELECT * FROM itemproduto")
    fun getAll(): List<ItemProduto>

    @Query("SELECT * FROM itemproduto WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<ItemProduto>

    @Insert
    fun insertAll(users:  List<ItemProduto>)

    @Delete
    fun delete(ItemProduto: ItemProduto)
}
