package br.com.rm.cfv.asyncTasks.produto

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

            var departamento : Departamento? = appdatabase.departamentoDAO().findByNome(produto.departamento)

            if(departamento == null){
                departamento = Departamento(null, produto.departamento,  null)
                appdatabase.departamentoDAO().insert(departamento)
            }

            if(produto.uid == null)
                appdatabase.produtoDAO().insertAll(produto)
            else
                appdatabase.produtoDAO().update(produto)

            result = appdatabase.produtoDAO().findByCodigo(produto.codigo!!)

            return result
        }catch (e : Exception){
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