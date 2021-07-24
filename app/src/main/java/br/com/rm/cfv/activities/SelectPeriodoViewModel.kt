package br.com.rm.cfv.activities

import android.content.DialogInterface
import android.util.Log
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.interfaces.ILoadReportData
import java.time.LocalDate
import java.util.*

class SelectPeriodoViewModel : ViewModel() {

    var mes : Int? = null

    var ano : Int? = null

    lateinit var baseActivity : BaseActivity

    lateinit var iLoadReportData: ILoadReportData

    fun init(){
    }

    fun abrirDialogAnoMesPicker(){

        val builder = AlertDialog.Builder(baseActivity, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
        val inflater = baseActivity.layoutInflater

        val cal = LocalDate.now()

        var dialog = inflater.inflate(R.layout.ano_mes_picker, null)
        val monthPicker = dialog.findViewById<NumberPicker>(R.id.numberpicker_mes)
        val yearPicker = dialog.findViewById<NumberPicker>(R.id.numberpicker_ano)

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = mes!!

        var year = cal.year
        yearPicker.minValue = year - 5
        yearPicker.maxValue = year
        yearPicker.value = ano!!

        monthPicker.setOnValueChangedListener { picker, oldVal, newVal -> mes = newVal }

        yearPicker.setOnValueChangedListener { picker, oldVal, newVal -> ano = newVal }

        builder.setView(dialog).setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            Log.i("Positive Button", which.toString())
            val cal = Calendar.getInstance()

            cal.set(Calendar.YEAR, ano!!)
            cal.set(Calendar.MONTH, mes!!)

            iLoadReportData.execute(mes!!, ano!!)

        })

        builder.show()
    }

}