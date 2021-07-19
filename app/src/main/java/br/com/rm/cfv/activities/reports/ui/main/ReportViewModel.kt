package br.com.rm.cfv.activities.reports.ui.main

import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModel
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.activities.interfaces.ISelectReport
import br.com.rm.cfv.activities.interfaces.ReportEnum
import br.com.rm.cfv.database.entities.dtos.ReportDTO

class ReportViewModel : ViewModel(), ISelectReport{

    var reports : List<String> = ReportEnum.reportTitles()

    var mes : Int? = null

    var ano : Int? = null

    lateinit var data: ReportDTO

    lateinit var baseActivity: BaseActivity

    lateinit var buttonReport: AppCompatButton

    fun init(){

        buttonReport.setOnClickListener {
            openDialogChooseReport()
        }

    }

    override fun onSelectReport(report: ReportEnum, mes: Int, ano: Int) {
        TODO("Not yet implemented")
    }

    fun openDialogChooseReport(){

        baseActivity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.escolha_uma_opcao)
                .setItems(reports.toTypedArray()
                ) { dialog, which ->
                    onSelectReport(ReportEnum.values()[which], mes!!, ano!!)
                    dialog.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }

}