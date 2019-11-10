package br.com.rm.cfv.database.daos

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.rm.cfv.database.daos.interfaces.*
import br.com.rm.cfv.database.entities.*

@Database(entities = arrayOf(
    Cliente::class,
    Departamento::class,
    Produto::class,
    MovimentacaoEstoque::class,
    DebitoCliente::class,
    ItemProduto::class,
    PagamentoDebito::class),
version = 3, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun clienteDAO(): ClienteDAO
    abstract fun departamentoDAO() : DepartamentoDAO
    abstract fun produtoDAO() : ProdutoDAO
    abstract fun movimentacaoEstoqueDAO() : MovimentacaoEstoqueDAO
    abstract fun debitoClienteDAO() : DebitoClienteDAO
    abstract fun itemProdutoDAO() : ItemProdutoDAO
    abstract fun pagamentoDebitoDAO(): PagamentoDebitoDAO
}