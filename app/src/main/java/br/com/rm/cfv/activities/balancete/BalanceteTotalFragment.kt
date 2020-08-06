package br.com.rm.cfv.activities.balancete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.R
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.balancete.SelectTotalBalanceteAsyncTask
import br.com.rm.cfv.database.entities.Balancete
import br.com.rm.cfv.database.entities.dtos.TotalBalanceteDTO
import br.com.rm.cfv.utils.charts.PieChatUtil
import br.com.rm.numberUtils.DecimalFormatUtils
import kotlinx.android.synthetic.main.fragment_balancete.*

class BalanceteTotalFragment : Fragment(), IPostExecuteSearch {

    private lateinit var balancete : Balancete

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {
        val root = inflater.inflate(R.layout.fragment_balancete, container, false)

        return root
    }

    override fun onViewCreated(viewRoot: View, savedInstanceState: Bundle?) {
        super.onViewCreated(viewRoot, savedInstanceState)

        balancete = arguments!!.getParcelable(BalanceteTipoFragment.ARG_BALANCETE) as Balancete

        if(balancete == null){
            activity!!.finish()
        }

        textViewlabelMes.text = "${balancete}"

        getBalancete()

    }

    fun getBalancete(){
        var task =
            SelectTotalBalanceteAsyncTask(
                CfvApplication.database!!.itemBalanceteDAO(),
                this
            )
        task.execute(balancete)
    }

    override fun afterSearch(result: Any?) {
        if(result != null){
            val totalBalancete = result as TotalBalanceteDTO?
            textViewTotalDespesas.text = getString(R.string.currency_format, DecimalFormatUtils.decimalFormatPtBR(totalBalancete!!.totalDespesas))
            textViewTotalReceitas.text = getString(R.string.currency_format, DecimalFormatUtils.decimalFormatPtBR(totalBalancete!!.totalReceitas))
            textViewTotal.text = getString(R.string.currency_format, DecimalFormatUtils.decimalFormatPtBR(totalBalancete!!.total()))
            if(totalBalancete!!.total() < 0){
                textViewTotal.setTextColor(ContextCompat.getColor(this.context!!, R.color.color_error))
            }
            val p = PieChatUtil(activity, R.id.chart1)
            p.build()
            val entries = mapOf(getString(R.string.chart_receitas) to totalBalancete!!.totalReceitas!!.toFloat(), getString(R.string.chart_despesas) to totalBalancete!!.totalDespesas!!.toFloat())
            p.setData(balancete.toString(), entries)
        }

    }

    override fun showProgress(text: String) {
        //(this.activity as BaseActivity).showProgress()
    }

    override fun hideProgress() {
        //(this.activity as BaseActivity).hideProgress()
    }

    companion object {
        const val ARG_BALANCETE = "balancete"

        @JvmStatic
        fun newInstance(balancete: Balancete): BalanceteTotalFragment {
            return BalanceteTotalFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_BALANCETE, balancete )
                }
            }
        }
    }
}
