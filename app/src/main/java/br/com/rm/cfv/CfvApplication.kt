package br.com.rm.cfv

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.rm.cfv.database.daos.AppDataBase

open class CfvApplication : Application() {

    companion object {
        var database: AppDataBase? = null
    }

    override fun onCreate() {
        super.onCreate()
        //Room
        database = Room.databaseBuilder(this, AppDataBase::class.java, "database8")
            .fallbackToDestructiveMigration()
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) = db.run {
                        if (BuildConfig.DEBUG) {
                            beginTransaction()
                            try {
                                for (x in 1..10) {
                                    var contentValues = ContentValues()
                                    contentValues.put("nome", "Cliente " + x)
                                    contentValues.put("cpf", "000.000.000-0" + x)
                                    contentValues.put("telefone", "(85) 9 9999-9999")
                                    contentValues.put("data_nascimento", "01/01/1970")
                                    contentValues.put("email", "cliente_" + x + "@email.com")

                                    insert("cliente", 5, contentValues)
                                }

                                for (x in 1..10) {
                                    var contentValues = ContentValues()
                                    contentValues.put("nome", "fornecedor " + x)
                                    contentValues.put("cpf_cnpj", "000.000.000-0" + x)
                                    contentValues.put("telefone", "(85) 9 9999-9999")
                                    contentValues.put("email", "fornecedor_" + x + "@email.com")

                                    insert("fornecedor", 5, contentValues)
                                }

                                for (x in 1..10) {
                                    var contentValues = ContentValues()
                                    contentValues.put("nome", "Departamento" + x)
                                    insert("departamento", 5, contentValues)
                                }

                                for (x in 1..10) {
                                    var contentValues = ContentValues()
                                    contentValues.put("nome", "produto " + x)
                                    contentValues.put("codigo", x)
                                    contentValues.put("preco_custo", 6.0 * x)
                                    contentValues.put("preco_venda", 8.0 * x)
                                    contentValues.put("departamento", "Departamento1")
                                    contentValues.put("permiteEstoqueNegativo", false)

                                    insert("produto", 5, contentValues)
                                }

                                setTransactionSuccessful()
                            } catch (e: Exception) {
                                Log.e("Callback", e.message, e)
                            } finally {
                                endTransaction()
                            }
                        }
                    }
                }
            )
            .build()
    }

    fun getDataBase(): AppDataBase? {
        return database
    }
}