package br.com.rm.cfv.activities.balancete

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.com.rm.cfv.R
import br.com.rm.cfv.database.entities.Balancete

private val TAB_TITLES = arrayOf(
    R.string.tab_balancete,
    R.string.tab_receitas,
    R.string.tab_despesas
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, private val balancete: Balancete, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        if(position > 0) {
            return BalanceteTipoFragment.newInstance(position, balancete)
        }else{
            return BalanceteTotalFragment.newInstance(balancete)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }
}