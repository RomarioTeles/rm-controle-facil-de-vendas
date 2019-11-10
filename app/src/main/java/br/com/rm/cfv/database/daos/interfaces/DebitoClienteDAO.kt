package br.com.rm.cfv.database.daos.interfaces

import androidx.room.*
import br.com.rm.cfv.database.entities.DebitoCliente
import br.com.rm.cfv.database.entities.dtos.DebitoClienteDTO

@Dao
interface DebitoClienteDAO{
    
    @Query("SELECT * FROM debitocliente")
    fun getAll(): List<DebitoCliente>

    @Query("SELECT cliente_id as cliente_id, cliente_nome as nome, SUM(total) AS total  FROM debitocliente where status_pagamento = :status and cliente_id = :cliente_id LIMIT 1")
    fun findByClienteIdAndStatus(cliente_id: Int, status: String): DebitoClienteDTO

    @Query("SELECT * FROM debitocliente WHERE cliente_id = :cliente_id")
    fun findByClienteId(cliente_id: Int) : List<DebitoCliente>

    @Query("SELECT * FROM debitocliente WHERE codigo = :codigo")
    fun findByCodigo(codigo: String) : DebitoCliente

    @Insert
    fun insertAll(vararg users: DebitoCliente)

    @Delete
    fun delete(debitocliente: DebitoCliente)

    @Update
    fun update(debitocliente: DebitoCliente)
}