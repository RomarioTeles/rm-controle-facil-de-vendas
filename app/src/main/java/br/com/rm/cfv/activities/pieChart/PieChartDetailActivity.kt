package br.com.rm.cfv.activities.pieChart

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.activities.pieChart.ui.main.PieChartPagerAdapter
import kotlinx.android.synthetic.main.activity_pie_chart_detail.*

class PieChartDetailActivity : BaseActivity() {

    var titulo : String = ""

    companion object {
        const val ARG_TITLE = "title"
        const val ARG_ENTRIES_KEYS = "entries_KEYS"
        const val ARG_ENTRIES_VALUES = "entries_VALUES"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pie_chart_detail)

        val keys = intent.getStringArrayListExtra(ARG_ENTRIES_KEYS)
        val values = intent.getFloatArrayExtra(ARG_ENTRIES_VALUES)
        titulo = intent.getStringExtra(ARG_TITLE)!!

        supportActionBar!!.title = titulo

        val sectionsPagerAdapter = PieChartPagerAdapter(this, supportFragmentManager, titulo, keys!!, values!!)

        val viewPager: ViewPager = findViewById(R.id.view_pager)

        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        fab().hide()
    }

    override fun getToobarTitle(): String {
        return titulo
    }
}