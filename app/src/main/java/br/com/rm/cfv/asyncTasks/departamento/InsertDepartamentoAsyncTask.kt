package br.com.rm.cfv.asyncTasks.cliente

import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.database.daos.interfaces.DepartamentoDAO
import br.com.rm.cfv.database.entities.Departamento

open class InsertDepartamentoAsyncTask(private var dao: DepartamentoDAO?, private val ipostExecuteInsert: IPostExecuteInsertAndUpdate) : AsyncTask<Departamento, Departamento, Departamento>() {

    override fun doInBackground(vararg params: Departamento): Departamento? {
        try {

            val departamento: Departamento = params.get(0)

            if(departamento.uid != null) {

                dao!!.update(departamento)

                return departamento
            }else{
                val id : Long = dao!!.insert(departamento)

                departamento.uid = id.toInt()

                return departamento
            }

        }catch (e : SQLiteConstraintException){
            return null
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteInsert.showProgress("Inserindo Departamento...")
    }

    override fun onPostExecute(result: Departamento?) {
        super.onPostExecute(result)
        ipostExecuteInsert.afterInsert(result)
        ipostExecuteInsert.hideProgress()
    }

}