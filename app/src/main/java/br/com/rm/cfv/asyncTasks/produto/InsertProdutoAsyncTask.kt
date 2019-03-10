package br.com.rm.cfv.asyncTasks.produto

import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.database.daos.AppDataBase
import br.com.rm.cfv.database.entities.Departamento
import br.com.rm.cfv.database.entities.Produto

open class InsertProdutoAsyncTask(private var appdatabase: AppDataBase, private val ipostExecuteInsert: IPostExecuteInsertAndUpdate) : AsyncTask<Produto, Produto, Produto>() {

    override fun doInBackground(vararg params: Produto): Produto? {
        try {
            var result: Produto?

            var produto: Produto = params[0]

            var departamento : Departamento? = appdatabase.departamentoDAO().findByNome(produto.categoria)

            if(departamento == null){
                departamento = Departamento(null, produto.categoria,  null)
                appdatabase.departamentoDAO().insertAll(departamento)
            }

            appdatabase.produtoDAO().insertAll(produto)

            result = appdatabase.produtoDAO().findByCodigo(produto.codigo!!)

            return result
        }catch (e : SQLiteConstraintException){
            return null
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        ipostExecuteInsert.showProgress("Inserindo Produto...")
    }

    override fun onPostExecute(result: Produto?) {
        super.onPostExecute(result)
        ipostExecuteInsert.afterInsert(result)
        ipostExecuteInsert.hideProgress()
    }

}