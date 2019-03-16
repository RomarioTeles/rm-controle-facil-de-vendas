package br.com.rm.cfv.database.daos.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.rm.cfv.database.entities.MovimentacaoEstoque
import br.com.rm.cfv.database.entities.dtos.EstoqueDTO

@Dao
interface MovimentacaoEstoqueDAO{
    
    @Query("SELECT distinct p.uid AS id, p.codigo AS codigo, p.nome AS nome," +
                "( SELECT SUM(sq.quantidade) FROM estoque sq WHERE sq.tipo = 'ENTRADA' and sq.codigo_produto = p.codigo) AS qtdEntrada, "
            +   "( SELECT SUM(sq2.quantidade) FROM estoque sq2 WHERE sq2.tipo = 'SAIDA' and sq2.codigo_produto = p.codigo) AS qtdSaida"
            +   " FROM produto p LEFT JOIN estoque e ON p.codigo = e.codigo_produto" )
    fun getAllEstoque(): List<EstoqueDTO>

    @Query("SELECT * FROM estoque WHERE codigo_produto LIKE :codigo order by data_hora desc")
    fun findByCodigoProduto(codigo: String) : List<MovimentacaoEstoque>

    @Query("SELECT p.codigo AS codigo, p.nome AS nome,"
            +   "( SELECT SUM(sq.quantidade) FROM estoque sq WHERE sq.tipo = 'ENTRADA' and sq.codigo_produto = p.codigo) AS qtdEntrada, "
            +   "( SELECT SUM(sq2.quantidade) FROM estoque sq2 WHERE sq2.tipo = 'SAIDA' and sq2.codigo_produto = p.codigo) AS qtdSaida"
            +   " FROM estoque e JOIN produto p ON p.codigo = e.codigo_produto WHERE p.codigo = :codigo LIMIT 1" )
    fun getEstoqueByCodigoProduto(codigo : String): EstoqueDTO

    @Insert
    fun insertAll(vararg objects: MovimentacaoEstoque)

}