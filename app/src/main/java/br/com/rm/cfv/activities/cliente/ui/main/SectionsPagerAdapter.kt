package br.com.rm.cfv.activities.cliente.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.com.rm.cfv.R

private val TAB_TITLES = arrayOf(
    R.string.tab_lancamentos,
    R.string.tab_parcelas
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        if(position == 0){
            return LancamentosDebitoFragment.newInstance(position + 1)
        }
        return ParcelasDebitoFragment.newInstance(position + 1)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 2
    }
}