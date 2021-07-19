package br.com.rm.cfv.activities.balancete

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.balancete.SelectAllItemBalanceteAsyncTask
import br.com.rm.cfv.database.entities.Balancete
import br.com.rm.cfv.database.entities.ItemBalancete
import com.google.android.material.tabs.TabLayout

class BalanceteActivity : BaseActivity() {

    private lateinit var balancete : Balancete

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balancete)

        balancete = intent.extras!!.getParcelable<Balancete>("balancete")!!

        if(balancete == null){
            finish()
        }

        val sectionsPagerAdapter =
            SectionsPagerAdapter(this, balancete, supportFragmentManager)

        val viewPager: ViewPager = findViewById(R.id.view_pager)

        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = findViewById(R.id.tabs)

        tabs.setupWithViewPager(viewPager)

        fab().hide()
    }

    override fun getToobarTitle(): String {
        return getString(R.string.listar_balancetes)
    }
}