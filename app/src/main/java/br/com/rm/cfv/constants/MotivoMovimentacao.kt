package br.com.rm.cfv.constants

class MotivoMovimentacao {

    companion object {
        const val AJUSTE_ESTOQUE = "Ajuste manual de estoque"
        const val REPOSICAO_DE_ITEM = "Devolução de itens vendidos"
        const val COMPRA_MERCADORIA = "Compra de mercadoria"
        const val VENDA_MERCADORIA = "Venda de mercadoria"

        fun values() : Array<String>{
            return arrayOf(AJUSTE_ESTOQUE, REPOSICAO_DE_ITEM, VENDA_MERCADORIA, COMPRA_MERCADORIA)
        }
    }


}
