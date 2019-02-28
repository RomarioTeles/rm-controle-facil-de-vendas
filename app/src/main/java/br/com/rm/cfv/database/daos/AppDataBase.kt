package br.com.rm.cfv.database.daos

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.rm.cfv.database.daos.interfaces.ClienteDAO
import br.com.rm.cfv.database.daos.interfaces.DepartamentoDAO
import br.com.rm.cfv.database.entities.Cliente
import br.com.rm.cfv.database.entities.Departamento

@Database(entities = arrayOf(Cliente::class, Departamento::class), version = 2, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun clienteDAO(): ClienteDAO
    abstract fun departamentoDAO() : DepartamentoDAO
}