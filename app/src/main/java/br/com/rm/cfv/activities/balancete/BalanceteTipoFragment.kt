package br.com.rm.cfv.activities.balancete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.adapters.balancete.ItemBalanceteAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.balancete.SelectAllItemBalanceteAsyncTask
import br.com.rm.cfv.constants.TipoItemBalancete
import br.com.rm.cfv.database.entities.Balancete
import br.com.rm.cfv.database.entities.ItemBalancete

class BalanceteTipoFragment : Fragment(), IPostExecuteSearch{

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ItemBalanceteAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var balancete : Balancete
    private lateinit var filter: String
    private var myDataset : List<ItemBalancete> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {
        val root = inflater.inflate(R.layout.activity_lista_default, container, false)

        return root
    }

    override fun onViewCreated(viewRoot: View, savedInstanceState: Bundle?) {
        super.onViewCreated(viewRoot, savedInstanceState)

        balancete = arguments!!.getParcelable(ARG_BALANCETE) as Balancete

        var tabPosition = arguments!!.getInt(ARG_SECTION_NUMBER)

        filter = if(tabPosition == 1) TipoItemBalancete.RECEITA else TipoItemBalancete.DESPESA

        if(balancete == null){
            activity!!.finish()
        }

        viewManager = LinearLayoutManager(activity)

        viewAdapter = ItemBalanceteAdapter(activity!!, myDataset)

        recyclerView = view!!.findViewById<RecyclerView>(R.id.recyclerViewItens).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        getAllBalancetes()

    }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"
        const val ARG_BALANCETE = "balancete"

        @JvmStatic
        fun newInstance(sectionNumber: Int, balancete: Balancete): BalanceteTipoFragment {
            return BalanceteTipoFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                    putParcelable(ARG_BALANCETE, balancete )
                }
            }
        }
    }

    fun getAllBalancetes(){
        var task =
            SelectAllItemBalanceteAsyncTask(
                CfvApplication.database!!.itemBalanceteDAO(),
                this
            )
        task.execute(balancete, filter)
    }

    override fun afterSearch(result: Any?) {
        myDataset = result as List<ItemBalancete>
        viewAdapter.setDataset(myDataset)
    }

    override fun showProgress(text: String) {
        //(this.activity as BaseActivity).showProgress()
    }

    override fun hideProgress() {
        //(this.activity as BaseActivity).hideProgress()
    }
}
