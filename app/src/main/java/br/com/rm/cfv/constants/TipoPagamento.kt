package br.com.rm.cfv.constants

class TipoPagamento {

    companion object {
        val A_VISTA: String = "A_VISTA"
        val A_PRAZO : String = "A_PRAZO"
        val map = mapOf(A_VISTA to "à vista", A_PRAZO to "à prazo")

        fun getDescricaoPeloNome(nome : String): String? {
            return map.get(nome)
        }
    }

}
