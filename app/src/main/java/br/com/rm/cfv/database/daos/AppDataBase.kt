package br.com.rm.cfv.database.daos

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.rm.cfv.database.daos.interfaces.ClienteDAO
import br.com.rm.cfv.database.daos.interfaces.DepartamentoDAO
import br.com.rm.cfv.database.daos.interfaces.MovimentacaoEstoqueDAO
import br.com.rm.cfv.database.daos.interfaces.ProdutoDAO
import br.com.rm.cfv.database.entities.Cliente
import br.com.rm.cfv.database.entities.Departamento
import br.com.rm.cfv.database.entities.MovimentacaoEstoque
import br.com.rm.cfv.database.entities.Produto

@Database(entities = arrayOf(Cliente::class, Departamento::class, Produto::class, MovimentacaoEstoque::class), version = 8, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun clienteDAO(): ClienteDAO
    abstract fun departamentoDAO() : DepartamentoDAO
    abstract fun produtoDAO() : ProdutoDAO
    abstract fun movimentacaoEstoqueDAO() : MovimentacaoEstoqueDAO
}