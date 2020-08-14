package br.com.rm.cfv.constants

class TipoReferencia{
    companion object {
        fun values(): Array<String> {
            return arrayOf(CLIENTE, FORNECEDOR, DESPESAS, RECEITAS)
        }

        fun getListDespesas() : List<String> {
            return listOf(FORNECEDOR, DESPESAS)
        }

        fun getListReceitas() : List<String> {
            return listOf(CLIENTE, RECEITAS)
        }

        fun valuesByTipoRef(tipoRef: String) : List<String>?{
            val listReceitas = getListReceitas()
            val listDespesas = getListDespesas()
            if(tipoRef in listReceitas){
                return listReceitas
            }else if(tipoRef in listDespesas){
                return listDespesas
            }else{
                return null
            }
        }

        val CLIENTE: String = "CLIENTE"
        val FORNECEDOR: String = "FORNECEDOR"
        val DESPESAS: String = "DESPESAS"
        val RECEITAS: String = "RECEITAS"


    }
}