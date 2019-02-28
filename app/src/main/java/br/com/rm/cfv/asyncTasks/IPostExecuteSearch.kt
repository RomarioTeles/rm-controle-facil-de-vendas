package br.com.rm.cfv.asyncTasks

interface IPostExecuteSearch : IProgressState{

    fun afterSearch(result : Any?)
}