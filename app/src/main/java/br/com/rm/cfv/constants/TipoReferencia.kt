package br.com.rm.cfv.constants

enum class TipoReferencia{

    CLIENTE, FORNECEDOR, DESPESAS, RECEITAS;

    companion object {

        fun getListDespesas() : List<String> {
            return listOf(FORNECEDOR, DESPESAS).map { it.name }
        }

        fun getListReceitas() : List<String> {
            return listOf(CLIENTE, RECEITAS).map { it.name }
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
    }
}