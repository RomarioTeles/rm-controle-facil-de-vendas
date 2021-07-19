package br.com.rm.cfv.database.entities.dtos

import com.opencsv.bean.CsvBindByName

class ReportFields {

    @CsvBindByName(column="descricao") var descricao : String? = null
    @CsvBindByName(column="valor") var valor : String? = null
    @CsvBindByName(column="codigo") var codigo : String? = null
}