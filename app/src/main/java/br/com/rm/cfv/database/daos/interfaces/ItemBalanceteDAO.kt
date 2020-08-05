package br.com.rm.cfv.database.daos.interfaces

import androidx.room.*
import br.com.rm.cfv.database.entities.ItemBalancete
import br.com.rm.cfv.database.entities.dtos.TotalBalanceteDTO

@Dao
public interface ItemBalanceteDAO {

    @Query("SELECT * FROM itembalancete")
    fun getAll(): List<ItemBalancete>

    @Query("SELECT * FROM itembalancete WHERE balancete_id = :balanceteId ORDER BY data_hora DESC")
    fun findByBalanceteId(balanceteId: Int): List<ItemBalancete>

    @Query("""SELECT despesa.valor AS total_despesas, receita.valor AS total_receitas
        FROM ( select coalesce(SUM(coalesce(desp.valor, 0.0)), 0.0) AS valor from itembalancete desp where desp.balancete_id = :balanceteId and desp.tipo = 'DESPESA') AS despesa
        ,(select coalesce(SUM(coalesce(receita.valor, 0.0)), 0.0) AS valor from itembalancete receita where receita.balancete_id = :balanceteId and receita.tipo = 'RECEITA') AS receita
        LIMIT 1""")
    fun getTotalBalancete(balanceteId: Int): TotalBalanceteDTO

    @Query("SELECT * FROM itembalancete WHERE balancete_id = :balanceteId and tipo = :tipo ORDER BY data_hora DESC")
    fun findByBalanceteIdAndTipo(balanceteId: Int, tipo: String): List<ItemBalancete>

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
