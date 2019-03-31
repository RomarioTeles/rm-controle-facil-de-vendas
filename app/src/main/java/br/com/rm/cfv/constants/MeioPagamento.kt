package br.com.rm.cfv.constants

class MeioPagamento {

    companion object {
        val DINHEIRO: String = "DINHEIRO"
        val CARTAO_CREDITO: String = "CARTAO_CREDITO"
        val CARTAO_DEBITO: String = "CARTAO_DEBITO"
        val VALE_REFEICAO: String = "VALE_REFEICAO"
        val VALE_ALIMENTACAO: String = "VALE_ALIMENTACAO"
        val CREDITO_LOJA: String = "CREDITO_LOJA"
        val VALE_PRESENTE: String = "VALE_PRESENTE"
        val OUTROS: String = "OUTROS"

        val map = HashMap<String, String>()

        fun getDescricaoPeloNome(nome : String): String? {
            return map[nome]
        }
    }

    init {
        map.put(DINHEIRO, "Dinheiro")
        map.put(CARTAO_CREDITO, "Cartão de crédito")
        map.put(CARTAO_DEBITO, "Cartão de débito")
        map.put(VALE_REFEICAO, "Vale refeição")
        map.put(VALE_ALIMENTACAO, "Vale alimentação")
        map.put(CREDITO_LOJA, "Crédito da loja")
        map.put(VALE_PRESENTE, "Vale presente")
        map.put(OUTROS, "Outros")

    }

}
