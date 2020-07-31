package br.com.rm.cfv.activities.contaPagarReceber.contaPagarReceberViewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.activities.contaPagarReceber.viewModel.ParcelasDebitoViewModel
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO

/**
 * A placeholder fragment containing a simple view.
 */
class ParcelasFragment : Fragment() {

    private lateinit var pageViewModel: ParcelasDebitoViewModel

    override fun onViewCreated(viewRoot: View, savedInstanceState: Bundle?) {
        super.onViewCreated(viewRoot, savedInstanceState)

        pageViewModel = ViewModelProviders.of(this).get(ParcelasDebitoViewModel::class.java).apply {
            pagamentoDebito = arguments?.getParcelable(ARG_PAGAMENTO_DEBITO)!!
            baseActivity = activity as BaseActivity
            view = viewRoot
        }

    }

    override fun onResume() {
        super.onResume()
        pageViewModel.buscarParcelas()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_visualizar_debito, container, false)


        return root
    }

    companion object {
        private const val ARG_PAGAMENTO_DEBITO = "PAGAMENTO_DEBITO"

        @JvmStatic
        fun newInstance(pagamentoDebito: PagamentoDebitoSubtotalDTO): ParcelasFragment {
            return ParcelasFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PAGAMENTO_DEBITO, pagamentoDebito)
                }
            }
        }
    }
}