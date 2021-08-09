package br.com.rm.cfv.database.daos

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.rm.cfv.database.converters.Converters
import br.com.rm.cfv.database.daos.interfaces.*
import br.com.rm.cfv.database.entities.*

@Database(entities = arrayOf(
    Cliente::class,
    Departamento::class,
    Produto::class,
    MovimentacaoEstoque::class,
    ContaPagarReceber::class,
    ItemProduto::class,
    PagamentoDebito::class,
    Fornecedor::class,
    Balancete::class,
    ItemBalancete::class),
version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun clienteDAO(): ClienteDAO
    abstract fun departamentoDAO() : DepartamentoDAO
    abstract fun produtoDAO() : ProdutoDAO
    abstract fun movimentacaoEstoqueDAO() : MovimentacaoEstoqueDAO
    abstract fun contaPagarReceberDAO() : ContaPagarReceberDAO
    abstract fun itemProdutoDAO() : ItemProdutoDAO
    abstract fun pagamentoDebitoDAO(): PagamentoDebitoDAO
    abstract fun fornecedorDAO(): FornecedorDAO
    abstract fun balanceteDAO(): BalanceteDAO
    abstract fun itemBalanceteDAO(): ItemBalanceteDAO
    abstract fun chartsDAO(): ChartsDAO
    abstract fun reportDAO() : ReportDAO
}