package br.com.rm.cfv.asyncTasks

interface IPostExecuteDelete : IProgressState{

    fun afterDelete(result : Any?)

}