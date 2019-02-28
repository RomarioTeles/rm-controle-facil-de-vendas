package br.com.rm.cfv.asyncTasks

interface IPostExecuteInsertAndUpdate : IProgressState{

    fun afterInsert(result : Any?)

    fun afterUpdate(result : Any?)

}