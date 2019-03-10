package br.com.rm.cfv.database.daos.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.com.rm.cfv.database.entities.Departamento

@Dao
interface DepartamentoDAO {

    @Query("SELECT * FROM Departamento")
    fun getAll(): List<Departamento>

    @Query("SELECT nome FROM Departamento")
    fun getAllNomes(): List<String>

    @Query("SELECT * FROM Departamento WHERE uid IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<Departamento>

    @Query("SELECT * FROM Departamento WHERE nome LIKE :nome LIMIT 1")
    fun findByNome(nome: String) : Departamento?

    @Query("SELECT * FROM Departamento WHERE uid = :id LIMIT 1")
    fun findById(id: Int) : Departamento

    @Insert
    fun insertAll(vararg departs: Departamento)

    @Delete
    fun delete(departamento: Departamento)
}