package br.com.rm.cfv.activities.contaPagarReceber.viewModel

import android.view.View
import androidx.lifecycle.ViewModel
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO

open class PageViewModel : ViewModel() {

    lateinit var pagamentoDebito : PagamentoDebitoSubtotalDTO

    lateinit var baseActivity: BaseActivity

    lateinit var view: View

}