package br.com.rm.cfv.constants

class TipoPagamento {

    companion object {
        val A_VISTA: String = "A_VISTA"
        val A_PRAZO : String = "A_PRAZO"
        val map = HashMap<String, String>()

        fun getDescricaoPeloNome(nome : String): String? {
            return map[nome]
        }
    }

    init {
        map.put(A_VISTA, "Pagamento a vista")
        map.put(A_PRAZO, "Pagamento a prazo")
    }

}
