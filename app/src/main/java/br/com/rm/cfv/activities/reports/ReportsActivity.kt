package br.com.rm.cfv.activities.reports

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.activities.interfaces.ILoadReportData
import br.com.rm.cfv.activities.reports.ui.main.ReportsFragment
import br.com.rm.cfv.activities.reports.ui.main.SelectPeriodoViewModel
import java.time.LocalDate

class ReportsActivity : BaseActivity(), ILoadReportData {

    companion object{
        var ARG_MES = "MES"
        var ARG_ANO = "ANO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reports_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ReportsFragment.newInstance())
                .commitNow()
        }


    }

    override fun getToobarTitle(): String {
        return "Relatórios"
    }

    override fun execute(mes: Int, ano: Int) {
        TODO("Not yet implemented")
    }
}