package br.com.rm.cfv.activities.interfaces

import br.com.rm.cfv.database.entities.dtos.EstoqueDTO

interface IMovimentacaoEstoque {

    fun preparaMovimentacao(estoque : EstoqueDTO)

}
