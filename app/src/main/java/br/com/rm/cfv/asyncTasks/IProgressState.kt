package br.com.rm.cfv.asyncTasks

interface IProgressState {

    fun showProgress(text : String = "Aguarde...")

    fun hideProgress()
}