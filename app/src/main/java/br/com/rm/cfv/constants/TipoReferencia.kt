package br.com.rm.cfv.constants

class TipoReferencia{
    companion object {
        fun values(): Array<String> {
            return arrayOf(CLIENTE, FORNECEDOR, OUTRAS_DESPESA)
        }

        val CLIENTE: String = "CLIENTE"
        val FORNECEDOR: String = "FORNECEDOR"
        val OUTRAS_DESPESA: String = "OUTRAS_DESPESA"
    }
}