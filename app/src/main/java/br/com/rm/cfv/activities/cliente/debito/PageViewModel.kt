package br.com.rm.cfv.activities.cliente.debito

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.database.entities.DebitoCliente
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO

open class PageViewModel : ViewModel() {

    lateinit var debitoCliente : PagamentoDebitoSubtotalDTO

    lateinit var baseActivity: BaseActivity

    lateinit var view: View

}