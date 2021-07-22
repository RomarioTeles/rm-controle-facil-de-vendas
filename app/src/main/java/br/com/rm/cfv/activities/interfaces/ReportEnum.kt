package br.com.rm.cfv.activities.interfaces

enum class ReportEnum(val title: String) {

    CLIENTES_ATRASO("Cliente: em atraso"),
    CLIENTES_SALDO_DEVEDOR("Cliente: saldo devedor"),
    PRODUTOS_ESTOQUE_BAIXO("Produto: Estoque baixo"),
    PRODUTOS_QTD_ESTOQUE("Produtos: Estoque"),
    PRODUTOS_INVENTARIO("Produto: Inventário"),
    RECEITAS_ATRASO("Receitas: em atraso"),
    DESPESAS_ATRASO("Despesas: em atraso"),
    FORNECEDOR_ATRASO("Fornecedor: em atraso");

    companion object {
        fun reportTitles() : List<String>{
            return values().map { it.title }
        }
    }
}
