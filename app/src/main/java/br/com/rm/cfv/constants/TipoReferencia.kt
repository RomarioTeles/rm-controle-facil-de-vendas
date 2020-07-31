package br.com.rm.cfv.constants

class TipoReferencia{
    companion object {
        fun values(): Array<String> {
            return arrayOf(CLIENTE, FORNECEDOR, DESPESAS, RECEITAS)
        }

        val CLIENTE: String = "CLIENTE"
        val FORNECEDOR: String = "FORNECEDOR"
        val DESPESAS: String = "DESPESAS"
        val RECEITAS: String = "RECEITAS"
    }
}