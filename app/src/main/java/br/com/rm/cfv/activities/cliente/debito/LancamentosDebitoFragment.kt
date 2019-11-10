package br.com.rm.cfv.activities.cliente.debito

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.database.entities.DebitoCliente

/**
 * A placeholder fragment containing a simple view.
 */
class LancamentosDebitoFragment : Fragment() {

    private lateinit var pageViewModel: LancamentosDebitoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {

        val root = inflater.inflate(R.layout.fragment_visualizar_debito, container, false)

        return root
    }

    override fun onViewCreated(viewRoot: View, savedInstanceState: Bundle?) {
        super.onViewCreated(viewRoot, savedInstanceState)

        pageViewModel = ViewModelProviders.of(this).get(LancamentosDebitoViewModel::class.java).apply {
            debitoCliente = arguments?.getParcelable(ARG_DEBITO_CLIENTE)!!
            baseActivity = activity as BaseActivity
            view = viewRoot
        }

        pageViewModel.buscarLancamentos()
    }

    companion object {
        private const val ARG_DEBITO_CLIENTE = "DEBITO_CLIENTE"

        @JvmStatic
        fun newInstance(debitoCliente: DebitoCliente): LancamentosDebitoFragment {
            return LancamentosDebitoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DEBITO_CLIENTE, debitoCliente)
                }
            }
        }
    }
}