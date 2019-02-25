package br.com.rm.cfv.database.daos

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.rm.cfv.database.daos.interfaces.ClienteDAO
import br.com.rm.cfv.database.entities.Cliente

@Database(entities = arrayOf(Cliente::class), version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun clienteDAO(): ClienteDAO
}