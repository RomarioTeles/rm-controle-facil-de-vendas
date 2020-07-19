package br.com.rm.cfv.asyncTasks.fornecedor

import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import android.util.Log
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.database.daos.interfaces.FornecedorDAO
import br.com.rm.cfv.database.entities.Fornecedor

open class InsertFornecedorAsyncTask(private var dao: FornecedorDAO?,private val ipostExecuteInsert: IPostExecuteInsertAndUpdate) : AsyncTask<Fornecedor, Fornecedor, Fornecedor>() {

    override fun doInBackground(vararg params: Fornecedor): Fornecedor? {
        try {
            var result: Fornecedor? = null

            val fornecedor: Fornecedor = params.get(0)
            var id : Long? = null
            if(fornecedor.uid == null) {
                id = dao!!.insert(fornecedor)
            }else{
                dao!!.update(fornecedor)
            }
            Log.i("Id", "$id" )
            result = dao!!.findCpfCnpj(fornecedor.cpfCnpj!!)

            return result
        }catch (e : SQLiteConstraintException){
            return null
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteInsert.showProgress("Inserindo Fornecedor...")
    }

    override fun onPostExecute(result: Fornecedor?) {
        super.onPostExecute(result)
        ipostExecuteInsert.afterInsert(result)
        ipostExecuteInsert.hideProgress()
    }

}