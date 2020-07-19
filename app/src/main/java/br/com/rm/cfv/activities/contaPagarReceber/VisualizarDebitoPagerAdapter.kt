package br.com.rm.cfv.activities.contaPagarReceber

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.com.rm.cfv.R
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO

private val TAB_TITLES = arrayOf(
    R.string.tab_lancamentos,
    R.string.tab_parcelas
)

class VisualizarDebitoPagerAdapter(private val context: Context, private val pagamentoDebito: PagamentoDebitoSubtotalDTO, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        if(position == 0){
            return LancamentosDebitoFragment.newInstance(
                pagamentoDebito
            )
        }
        return ParcelasDebitoFragment.newInstance(
            pagamentoDebito
        )
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 2
    }
}