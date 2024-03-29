package br.com.rm.cfv.activities.contaPagarReceber.contaPagarReceberViewer

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.com.rm.cfv.R
import br.com.rm.cfv.constants.TipoReferencia
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO

private val TAB_TITLES = arrayOf(
    R.string.tab_parcelas,
    R.string.tab_lancamentos
)

class VisualizarContaPagarReceberPagerAdapter(private val context: Context, private val pagamentoDebito: PagamentoDebitoSubtotalDTO, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        if(count == 2) {
            if (position == 0) {
                return ParcelasFragment.newInstance(pagamentoDebito)
            }
            return LancamentosFragment.newInstance(pagamentoDebito)
        }else{
            return ParcelasFragment.newInstance(pagamentoDebito)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return if (pagamentoDebito.tipoRef in listOf(TipoReferencia.CLIENTE, TipoReferencia.FORNECEDOR).map { it.name }) 2 else 1
    }

}