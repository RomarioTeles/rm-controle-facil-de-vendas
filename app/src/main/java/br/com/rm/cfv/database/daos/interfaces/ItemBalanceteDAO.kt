package br.com.rm.cfv.database.daos.interfaces

import androidx.room.*
import br.com.rm.cfv.database.entities.ItemBalancete
import br.com.rm.cfv.database.entities.dtos.TotalBalanceteDTO

@Dao
interface ItemBalanceteDAO {

    @Query("SELECT * FROM itembalancete")
    fun getAll(): List<ItemBalancete>

    @Query("SELECT * FROM itembalancete WHERE balancete_id = :balanceteId ORDER BY data_hora DESC")
    fun findByBalanceteId(balanceteId: Int): List<ItemBalancete>

    @Query("""SELECT despesa.valor AS total_despesas, receita.valor AS total_receitas
        FROM ( select coalesce(SUM(coalesce(desp.valor, 0.0)), 0.0) AS valor from itembalancete desp where desp.balancete_id = :balanceteId and desp.tipo = 'DESPESA') AS despesa
        ,(select coalesce(SUM(coalesce(receita.valor, 0.0)), 0.0) AS valor from itembalancete receita where receita.balancete_id = :balanceteId and receita.tipo = 'RECEITA') AS receita
        LIMIT 1""")
    fun getTotalBalancete(balanceteId: Int): TotalBalanceteDTO

    @Query("""SELECT despesa.valor AS total_despesas, receita.valor AS total_receitas
        FROM
        ( select coalesce(SUM(coalesce(desp.valor, 0.0)), 0.0) AS valor from balancete b join itembalancete desp
                on b.uid = desp.balancete_id where desp.tipo = 'DESPESA' and b.ano = :ano and b.mes = :mes) AS despesa
        ,(select coalesce(SUM(coalesce(receita.valor, 0.0)), 0.0) AS valor from balancete b join itembalancete receita 
                on receita.balancete_id = b.uid where receita.tipo = 'RECEITA' and b.ano = :ano and b.mes = :mes) AS receita
        LIMIT 1""")
    fun getTotalBalanceteByMesAndAno(mes: Int, ano: Int): TotalBalanceteDTO

    @Query("SELECT * FROM itembalancete WHERE balancete_id = :balanceteId and tipo in (:tipo) ORDER BY data_hora DESC")
    fun findByBalanceteIdAndTipo(balanceteId: Int, tipo: List<String>): List<ItemBalancete>

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
