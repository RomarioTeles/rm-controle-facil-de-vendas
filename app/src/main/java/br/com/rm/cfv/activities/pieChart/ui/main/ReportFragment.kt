package br.com.rm.cfv.activities.pieChart.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_pie_chart_detail.*
import kotlinx.android.synthetic.main.fragment_pie_chart_detail.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A placeholder fragment containing a simple view.
 */
class ReportFragment : Fragment() {

    private lateinit var pageViewModel: ReportDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pie_chart_report_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pageViewModel = ViewModelProvider(this).get(ReportDetailViewModel::class.java).apply {
            keys = requireArguments().getStringArrayList(ARG_ENTRIES_KEYS) as ArrayList<String>
            values = requireArguments().getFloatArray(ARG_ENTRIES_VALUES) as FloatArray
            baseActivity = requireActivity() as BaseActivity
            title = requireArguments().getString(ARG_TITLE, "")
            init()
        }
    }

    companion object {

        private const val ARG_TITLE = "title"
        private const val ARG_ENTRIES_KEYS = "entries_KEYS"
        private const val ARG_ENTRIES_VALUES = "entries_VALUES"

        @JvmStatic
        fun newInstance(title: String, keys: ArrayList<String>, values: FloatArray): ReportFragment {
            return ReportFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putStringArrayList(ARG_ENTRIES_KEYS, keys)
                    putFloatArray(ARG_ENTRIES_VALUES, values)
                }
            }
        }
    }

}