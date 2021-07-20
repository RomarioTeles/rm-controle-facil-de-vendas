package br.com.rm.cfv.activities.reports

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.activities.SelectPeriodoViewModel
import br.com.rm.cfv.activities.interfaces.ILoadReportData
import br.com.rm.cfv.activities.reports.ui.main.ReportViewModel
import java.time.LocalDate

class ReportsActivity : BaseActivity() {

    companion object{
        var ARG_MES = "MES"
        var ARG_ANO = "ANO"
    }

    lateinit var reportViewModel: ReportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reports_activity)

        if (savedInstanceState == null) {

            reportViewModel = ViewModelProvider(this).get(ReportViewModel::class.java).apply {
                baseActivity = this@ReportsActivity
                reportDAO = CfvApplication.database!!.reportDAO()
            }
            reportViewModel.init()

            hideFab()
        }


    }

    override fun getToobarTitle(): String {
        return "Relatórios"
    }

}