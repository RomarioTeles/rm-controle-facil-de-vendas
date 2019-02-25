package br.com.rm.cfv

import android.app.Application
import androidx.room.Room
import br.com.rm.cfv.database.daos.AppDataBase

open class CfvApplication : Application() {

    companion object {
        var database: AppDataBase? = null
    }

    override fun onCreate() {
        super.onCreate()
        //Room
        database = Room.databaseBuilder(this, AppDataBase::class.java, "cfv_db").build()
    }

    fun getDataBase() : AppDataBase?{
        return database
    }
}