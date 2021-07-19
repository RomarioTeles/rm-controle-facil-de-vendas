package br.com.rm.cfv.activities.interfaces

enum class ReportEnum(val title: String) {

    CLIENTES_ATRASO("Clientes em atraso"),
    CLIENTES_SALDO_DEVEDOR("Cliente x Saldo devedor"),
    PRODUTOS_ESTOQUE_BAIXO("Produtos x Estoque baixo"),
    PRODUTOS_QTD_ESTOQUE("Produtos x Estoque");

    companion object {
        fun reportTitles() : List<String>{
            return values().map { it.title }
        }
    }
}
