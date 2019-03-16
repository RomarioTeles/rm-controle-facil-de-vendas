package br.com.rm.cfv.constants

class TipoMovimentacaoEstoque{
    companion object {
        fun values(): Array<String> {
            return arrayOf(ENTRADA, SAIDA)
        }

        val ENTRADA: String = "ENTRADA"
        val SAIDA: String = "SAIDA"
    }
}