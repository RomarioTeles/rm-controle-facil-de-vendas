package br.com.rm.cfv.database.daos.interfaces

import androidx.room.*
import br.com.rm.cfv.database.entities.DebitoCliente
import br.com.rm.cfv.database.entities.dtos.DebitoClienteDTO

@Dao
interface DebitoClienteDAO{
    
    @Query("SELECT * FROM debitocliente")
    fun getAll(): List<DebitoCliente>

    @Query("SELECT cliente_id as cliente_id, cliente_nome as nome, SUM(total) AS total  FROM debitocliente where status_pagamento = 'PENDENTE' group by cliente_id, cliente_nome")
    fun getAllDTO(): List<DebitoClienteDTO>

    @Query("SELECT * FROM debitocliente WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<DebitoCliente>

    @Query("SELECT * FROM debitocliente WHERE cliente_nome LIKE :first")
    fun findByNome(first: String) : List<DebitoCliente>

    @Insert
    fun insertAll(vararg users: DebitoCliente)

    @Delete
    fun delete(debitocliente: DebitoCliente)

    @Update
    fun update(debitocliente: DebitoCliente)
}