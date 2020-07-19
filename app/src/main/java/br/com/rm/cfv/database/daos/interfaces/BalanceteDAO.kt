package br.com.rm.cfv.database.daos.interfaces

import androidx.room.*
import br.com.rm.cfv.database.entities.Balancete

@Dao
public interface BalanceteDAO {

    @Query("SELECT * FROM balancete")
    fun getAll(): List<Balancete>

    @Query("SELECT * FROM balancete WHERE mes = :mes and ano = :ano LIMIT 1")
    fun findByMesAndAno(mes: Int, ano:Int) : Balancete

    @Query("SELECT * FROM balancete WHERE uid IN (:argids)")
    fun loadAllByIds(argids: IntArray): List<Balancete>

    @Insert
    fun insert(balancete: Balancete) : Long

    @Update
    fun update(balancete: Balancete) : Int

    @Delete
    fun delete(balancete: Balancete)
}
