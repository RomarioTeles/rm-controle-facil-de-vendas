package br.com.rm.cfv.adapters.produto

import br.com.rm.cfv.database.entities.ItemProduto

interface IOnClickItemProdutoListener {

    fun onItemProdutoClick(item : ItemProduto)
}