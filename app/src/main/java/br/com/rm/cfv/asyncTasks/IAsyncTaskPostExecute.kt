package br.com.rm.cfv.asyncTasks

interface IAsyncTaskPostExecute{

    fun afterExecute(result : Any?)

    fun showProgress(text : String = "Aguarde...")

    fun hideProgress()
}