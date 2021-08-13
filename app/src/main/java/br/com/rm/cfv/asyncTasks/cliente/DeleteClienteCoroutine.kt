package br.com.rm.cfv.asyncTasks.cliente

import br.com.rm.cfv.asyncTasks.IPostExecuteDelete
import br.com.rm.cfv.database.daos.interfaces.ClienteDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeleteClienteCoroutine(private val dao : ClienteDAO, private var ipostExecuteDelete : IPostExecuteDelete){

    private fun onPreExecute() {
        ipostExecuteDelete.showProgress("Removendo cliente...")
    }

    fun execute(id: Int, position: Int) {
        onPreExecute()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cliente = dao.findById(id)
                dao.delete(cliente)
                onPostExecute(position)
            } catch (e: Exception) {
                e.printStackTrace()
                onPostExecute(-1)
            }
        }
    }

    private fun onPostExecute(result: Int) {
        ipostExecuteDelete.afterDelete(result)
        ipostExecuteDelete.hideProgress()
    }

}
