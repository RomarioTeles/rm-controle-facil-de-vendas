package br.com.rm.cfv.activities.produto

import br.com.rm.cfv.database.entities.Produto

interface IOnClickProdutoListener {
    fun onProdutoClick(produto : Produto, isLongClick: Boolean)
}
