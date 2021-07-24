package br.com.rm.cfv.activities.pieChart.ui.main

import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.adapters.report.ReportAdapter
import br.com.rm.cfv.database.entities.dtos.ReportFields
import br.com.rm.numberUtils.DecimalFormatUtils

class ReportDetailViewModel: ViewModel(){

    lateinit var title: String

    lateinit var keys: ArrayList<String>

    lateinit var values: FloatArray

    lateinit var textViewTitle: TextView

    lateinit var textViewFooter: TextView

    lateinit var recyclerViewReportData: RecyclerView

    lateinit var reportAdapter: ReportAdapter

    lateinit var baseActivity: BaseActivity

    fun init(){
        reportAdapter = ReportAdapter(baseActivity, mutableListOf())

        textViewTitle = baseActivity.findViewById(R.id.textViewReportTitle)
        textViewTitle.text = baseActivity.getString(R.string.label_total)

        textViewFooter = baseActivity.findViewById(R.id.textViewFooter)
        textViewFooter.text = DecimalFormatUtils.decimalFormatPtBR(values.sum(), 2, 2)

        recyclerViewReportData = baseActivity.findViewById<RecyclerView>(R.id.recyclerViewReportData).apply{
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(baseActivity)
            adapter = reportAdapter
        }

        val reportData = keys.mapIndexed { index, key ->
            val data = ReportFields()
            data.valor = DecimalFormatUtils.decimalFormatPtBR(values[index], 2, 2)
            data.descricao = key
            data
        }

        reportAdapter.myDataset.clear()
        reportAdapter.myDataset.addAll(reportData)
        reportAdapter.notifyDataSetChanged()


    }

}
