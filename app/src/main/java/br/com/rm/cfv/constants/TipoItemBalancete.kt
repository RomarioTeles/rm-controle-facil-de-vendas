package br.com.rm.cfv.constants

class TipoItemBalancete{
    companion object {
        fun values(): Array<String> {
            return arrayOf(RECEITA, DESPESA)
        }

        val RECEITA: String = "RECEITA"
        val DESPESA: String = "DESPESA"

        fun getByTipoReferencia(tipoRef : String?) : String?{
            if (tipoRef == null) {
                return null
            }else if(tipoRef in listOf(TipoReferencia.FORNECEDOR, TipoReferencia.DESPESAS).map { it.name }){
                return DESPESA
            }else{
                return RECEITA
            }
        }
    }
}