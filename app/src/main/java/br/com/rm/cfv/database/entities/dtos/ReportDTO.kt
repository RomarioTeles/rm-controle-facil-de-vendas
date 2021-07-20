package br.com.rm.cfv.database.entities.dtos

class ReportDTO(var title: String, var footer: String? = null){

    var data : List<ReportFields> = mutableListOf()

}