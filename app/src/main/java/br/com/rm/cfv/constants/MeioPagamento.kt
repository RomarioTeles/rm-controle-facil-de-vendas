package br.com.rm.cfv.constants

import br.com.rm.cfv.R

enum class MeioPagamento(val descricao: String, val res: Int) {

    DINHEIRO("Dinheiro", R.drawable.cash),
    PARCELAMENTO_DA_LOJA("Parcelamento da loja", R.drawable.checkbook),
    CARTAO_CREDITO("Cartão de crédito", R.drawable.credit_card),
    CARTAO_DEBITO("Cartão de débito", R.drawable.credit_card),
    VALE_REFEICAO("Vale refeição", R.drawable.food),
    VALE_ALIMENTACAO("Vale alimentação", R.drawable.cart_primary),
    CREDITO_LOJA("Crédito da loja", R.drawable.store),
    VALE_PRESENTE("Vale presente", R.drawable.wallet_giftcard),
    OUTROS("Outros", R.drawable.credit_card_multiple);

    companion object{
        fun getMeiosPagamentosAvista(): List<MeioPagamento> {
            var lista = values().toMutableList()
            lista.remove(PARCELAMENTO_DA_LOJA)
            return lista
        }

        fun getMeiosPagamentosPrazo(): List<MeioPagamento> {
            return listOf(PARCELAMENTO_DA_LOJA)
        }
    }

}
