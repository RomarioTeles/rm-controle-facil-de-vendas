package br.com.rm.cfv.database.entities

interface IReferencia {
    fun getNomeRef(): String?
    fun getIdRef(): Int?
    fun getTipoRef(): String?

    fun reportFileName() : String{
        return "${getNomeRef()}_${getTipoRef()}.csv"
    }
}