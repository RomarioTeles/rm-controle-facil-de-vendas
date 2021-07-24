package br.com.rm.cfv.activities.pieChart.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.com.rm.cfv.R
import java.util.ArrayList

private val TAB_TITLES = arrayOf(
    R.string.tab_pie_chart,
    R.string.tab_report
)


class PieChartPagerAdapter(
    private val context: Context,
    fm: FragmentManager,
    private val title: String,
    private val keys: ArrayList<String>,
    private val values: FloatArray
) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        if(position == 0){
            return ChartFragment.newInstance(title, keys, values)
        }else{
            return ReportFragment.newInstance(title, keys, values)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}