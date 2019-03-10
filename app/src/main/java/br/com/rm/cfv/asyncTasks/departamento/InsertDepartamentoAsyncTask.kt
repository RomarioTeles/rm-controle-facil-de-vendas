package br.com.rm.cfv.asyncTasks.cliente

import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.database.daos.interfaces.DepartamentoDAO
import br.com.rm.cfv.database.entities.Departamento

open class InsertDepartamentoAsyncTask(private var dao: DepartamentoDAO?, private val ipostExecuteInsert: IPostExecuteInsertAndUpdate) : AsyncTask<Departamento, Departamento, Departamento>() {

    override fun doInBackground(vararg params: Departamento): Departamento? {
        try {
            var result: Departamento? = null

            var departamento: Departamento = params.get(0)

            var pai = dao!!.findByNome(departamento.departamentoPai!!)

            if(pai == null){
                var departamentoPai = Departamento(null, departamento.departamentoPai, null)
                dao!!.insertAll(departamentoPai)
            }

            dao!!.insertAll(departamento)

            result = dao!!.findByNome(departamento.nome!!)

            return result
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