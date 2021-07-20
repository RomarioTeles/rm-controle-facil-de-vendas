package br.com.rm.cfv.activities.reports.ui.main

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.activities.interfaces.ISelectReport
import br.com.rm.cfv.activities.interfaces.ReportEnum
import br.com.rm.cfv.adapters.report.ReportAdapter
import br.com.rm.cfv.database.daos.interfaces.ReportDAO
import br.com.rm.cfv.database.entities.dtos.ReportFields
import br.com.rm.numberUtils.DecimalFormatUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.math.NumberUtils

@SuppressLint("StaticFieldLeak")
class ReportViewModel : ViewModel(), ISelectReport{

    var reports : List<String> = ReportEnum.reportTitles()

    var report: ReportEnum = ReportEnum.CLIENTES_ATRASO

    lateinit var textViewTitle : TextView

    lateinit var recyclerViewReportData: RecyclerView

    lateinit var reportAdapter: ReportAdapter

    private val title: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }

    private val reportFieldsList: MutableLiveData<List<ReportFields>> by lazy {
        MutableLiveData<List<ReportFields>>()
    }

    lateinit var baseActivity: BaseActivity

    lateinit var buttonReport: AppCompatButton

    lateinit var reportDAO: ReportDAO

    fun init(){

        reportAdapter = ReportAdapter(baseActivity, mutableListOf())

        textViewTitle = baseActivity.findViewById(R.id.textViewTitle)

        buttonReport = baseActivity.findViewById(R.id.button_report)

        recyclerViewReportData = baseActivity.findViewById<RecyclerView>(R.id.recyclerViewReportData).apply{
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(baseActivity)
            adapter = reportAdapter
        }

        buttonReport.setOnClickListener {
            openDialogChooseReport()
        }

        val observerTitle = Observer<String>{ value ->
            textViewTitle.text = value
        }

        val observerReportFields = Observer<List<ReportFields>> { values ->
            reportAdapter.myDataset.clear()
            reportAdapter.myDataset.addAll(values)
            reportAdapter.notifyDataSetChanged()
        }

        title.observe(baseActivity, observerTitle)
        reportFieldsList.observe(baseActivity, observerReportFields)

        onSelectReport(ReportEnum.CLIENTES_ATRASO)
    }

    override fun onSelectReport(report: ReportEnum) {

        title.value = report.title
        when(report){
            ReportEnum.CLIENTES_ATRASO -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val data = reportDAO.getClientesEmAtraso()
                    withContext(Dispatchers.Main) {
                        reportFieldsList.value = data.map {
                            it.valor = DecimalFormatUtils.decimalFormatPtBR(NumberUtils.toDouble(it.valor, 0.0), 2, 2)
                            it
                        }
                    }
                }
            }
            ReportEnum.CLIENTES_SALDO_DEVEDOR -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val data = reportDAO.getSaldoClientes()
                    withContext(Dispatchers.Main) {
                        reportFieldsList.value = data.map {
                            it.valor = DecimalFormatUtils.decimalFormatPtBR(NumberUtils.toDouble(it.valor, 0.0), 2, 2)
                            it
                        }
                    }
                }
            }
            ReportEnum.PRODUTOS_ESTOQUE_BAIXO -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val data = reportDAO.getEstoqueBaixoProduto()
                    withContext(Dispatchers.Main) {
                        reportFieldsList.value = data
                    }
                }
            }
            ReportEnum.PRODUTOS_QTD_ESTOQUE -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val data = reportDAO.getEstoqueProduto()
                    withContext(Dispatchers.Main) {
                        reportFieldsList.value = data
                    }
                }
            }
        }

    }

    fun openDialogChooseReport(){

        baseActivity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.escolha_uma_opcao)
                .setItems(reports.toTypedArray()
                ) { dialog, which ->
                    onSelectReport(ReportEnum.values()[which])
                    dialog.dismiss()
                }
            builder.show()
        } ?: throw IllegalStateException("Activity cannot be null")

    }

}