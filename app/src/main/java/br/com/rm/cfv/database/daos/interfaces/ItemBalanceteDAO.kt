package br.com.rm.cfv.database.daos.interfaces

import androidx.room.*
import br.com.rm.cfv.database.entities.ItemBalancete

@Dao
public interface ItemBalanceteDAO {

    @Query("SELECT * FROM itembalancete")
    fun getAll(): List<ItemBalancete>

    @Query("SELECT * FROM itembalancete WHERE balancete_id = :balanceteId ORDER BY data_hora DESC")
    fun findByBalanceteId(balanceteId: Int): List<ItemBalancete>

    @Query("SELECT * FROM itembalancete WHERE uid IN (:argids)")
    fun loadAllByIds(argids: IntArray): List<ItemBalancete>

    @Insert
    fun insertAll(vararg itensBalancete: ItemBalancete)

    @Insert
    fun insert(itembalancete: ItemBalancete) : Long

    @Update
    fun update(itembalancete: ItemBalancete) : Int

    @Delete
    fun delete(itembalancete: ItemBalancete)
}
