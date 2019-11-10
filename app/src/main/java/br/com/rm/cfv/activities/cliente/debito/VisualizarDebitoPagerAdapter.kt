package br.com.rm.cfv.activities.cliente.debito

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.com.rm.cfv.R
import br.com.rm.cfv.database.entities.DebitoCliente

private val TAB_TITLES = arrayOf(
    R.string.tab_lancamentos,
    R.string.tab_parcelas
)

class VisualizarDebitoPagerAdapter(private val context: Context, private val debitoCliente: DebitoCliente, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        if(position == 0){
            return LancamentosDebitoFragment.newInstance(
                debitoCliente
            )
        }
        return ParcelasDebitoFragment.newInstance(
            debitoCliente
        )
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 2
    }
}