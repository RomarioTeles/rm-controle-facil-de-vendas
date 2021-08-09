package br.com.rm.cfv.activities.balancete

import android.os.Bundle
import android.os.Environment
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.adapters.balancete.ItemBalanceteAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.balancete.SelectAllItemBalanceteCoroutine
import br.com.rm.cfv.constants.TipoItemBalancete
import br.com.rm.cfv.database.entities.Balancete
import br.com.rm.cfv.database.entities.ItemBalancete
import br.com.rm.cfv.utils.reports.CSVReportUtils
import br.com.rm.cfv.utils.reports.IReportable
import java.io.File

class BalanceteTipoFragment : Fragment(), IPostExecuteSearch, IReportable{

    override fun getDataSet(): List<Any> {
        return myDataset
    }

    override fun getReportFileName(): String {
        return "balancente ${balancete}.csv"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ItemBalanceteAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var balancete : Balancete
    private lateinit var filter: String
    private var myDataset : List<ItemBalancete> = ArrayList()
    private var isReport = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {
        val root = inflater.inflate(R.layout.activity_lista_default, container, false)

        return root
    }

    override fun onViewCreated(viewRoot: View, savedInstanceState: Bundle?) {
        super.onViewCreated(viewRoot, savedInstanceState)

        setHasOptionsMenu(true)

        balancete = requireArguments().getParcelable<Balancete>(ARG_BALANCETE)!!

        var tabPosition = requireArguments().getInt(ARG_SECTION_NUMBER)

        filter = if(tabPosition == 1) TipoItemBalancete.RECEITA else TipoItemBalancete.DESPESA

        if(balancete == null){
            requireActivity().finish()
        }

        viewManager = LinearLayoutManager(activity)

        viewAdapter = ItemBalanceteAdapter(requireActivity(), myDataset)

        recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerViewItens).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        getAllBalancetes(listOf(filter))

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

    fun getAllBalancetes(filter: List<String>, isReport: Boolean = false){
        this.isReport = isReport
        var task =
            SelectAllItemBalanceteCoroutine(
                CfvApplication.database!!.itemBalanceteDAO(),
                this
            )
        task.execute(balancete, filter)
    }

    override fun afterSearch(result: Any?) {
        myDataset = result as List<ItemBalancete>
        if(isReport) {
            gerarReport()
        }else{
            viewAdapter.setDataset(myDataset)
        }
    }

    override fun showProgress(text: String) {
        //(this.activity as BaseActivity).showProgress()
    }

    override fun hideProgress() {
        //(this.activity as BaseActivity).hideProgress()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        inflater.inflate(R.menu.main_report, menu)
        super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.report -> {
                getAllBalancetes(TipoItemBalancete.values().toList(), true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun gerarReport(){
        val storageDir = requireActivity()!!.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(storageDir, reportFileName)
        file.createNewFile()
        CSVReportUtils.writeCsvFromBean(file.toPath(), myDataset)
        (activity as BaseActivity)!!.shareReport(file.toPath())
    }
}
