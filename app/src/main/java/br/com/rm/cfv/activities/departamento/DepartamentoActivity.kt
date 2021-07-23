package br.com.rm.cfv.activities.departamento

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewStub
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.adapters.departamento.DepartamentoAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.cliente.InsertDepartamentoAsyncTask
import br.com.rm.cfv.asyncTasks.cliente.SelectAllDepartamentosAsyncTask
import br.com.rm.cfv.database.entities.Departamento
import br.com.rm.cfv.R
import br.com.rm.cfv.asyncTasks.IPostExecuteDelete
import br.com.rm.cfv.asyncTasks.departamento.DeleteDepartamentoAsyncTask
import br.com.rm.cfv.bottomsheets.BottomSheetDialogSettings
import br.com.rm.cfv.bottomsheets.IBottomSheetOptions
import br.com.rm.cfv.bottomsheets.ItemOptionsBottomSheetDialog
import br.com.rm.cfv.utils.ToastUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_departamento.*
import java.util.concurrent.CompletableFuture

class DepartamentoActivity : BaseActivity() , IPostExecuteSearch, IPostExecuteInsertAndUpdate, IPostExecuteDelete,
    IBottomSheetOptions {

    override fun getDataSet(): List<Any> {
        return departamentos
    }

    override fun getReportFileName(): String {
        return "categorias.csv"
    }

    var departamento : Departamento? = null

    lateinit var adapter : DepartamentoAdapter

    lateinit var departamentos : MutableList<Departamento>

    private lateinit var viewStub: ViewStub

    var departs : MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departamento)

        viewStub = findViewById(R.id.viewStub)
        viewStub.inflate()
        viewStub.visibility = View.GONE

        departamentos = mutableListOf()

        adapter = DepartamentoAdapter(this, departamentos)

        listViewDepartamentos.adapter = adapter

        setClickEvents()

    }

    private fun setClickEvents(){

        fab().setOnClickListener {
            openInsertDialog(null)
        }

        listViewDepartamentos.setOnItemClickListener { parent, view, position, id ->
            val item = adapter.getItem(position)
            openOptionsDialog(item!!, position)
        }

    }

    private fun openInsertDialog(item: Departamento?){

        val view = getLayoutInflater().inflate(R.layout.departamento_bottom_seet, null)
        var dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        if(item != null){
            view.findViewById<TextInputEditText>(R.id.textViewDepartamento).setText(item.nome)
        }

        view.findViewById<Button>(R.id.buttonAddDepart).setOnClickListener {
            var nome = view.findViewById<TextInputEditText>(R.id.textViewDepartamento).text.toString()
            if(nome.isBlank()) {
                view.findViewById<TextInputLayout>(R.id.textInputLayoutDepartamento).error = getString(R.string.mensagem_campo_requerido)
            }else{
                view.findViewById<TextInputLayout>(R.id.textInputLayoutDepartamento).error = null
                view.findViewById<TextInputEditText>(R.id.textViewDepartamento).text = null
                if(item != null){
                    departamento = Departamento(item.uid, nome, null)
                }else {
                    departamento = Departamento(null, nome, null)
                }

                var task = InsertDepartamentoAsyncTask(getCfvApplication().getDataBase()!!.departamentoDAO(), this)
                task.execute(departamento)

                dialog.dismiss()
            }
        }

        view.findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun openOptionsDialog(item : Departamento, position: Int?){

        val settings = BottomSheetDialogSettings(
            item.nome,
            false,
            true,
            false,
            true
        )

        ItemOptionsBottomSheetDialog().openDialog( this, item, position, settings, this)
    }

    override fun onResume() {
        super.onResume()
        getAllDepartamentos()
        nav_view.setCheckedItem(R.id.nav_departamento)
    }

    override fun getHomeIcon() : Int{
        return R.drawable.ic_menu
    }

    private fun getAllDepartamentos(query: String? = null, showProgress: Boolean = true){
        var task = SelectAllDepartamentosAsyncTask(getCfvApplication().getDataBase()!!.departamentoDAO(), this, showProgress)
        task.execute(query)
    }

    override fun afterSearch(result: Any?) {
        val listaDepartamento = result as List<Departamento>
        departamentos.clear()
        departamentos.addAll(listaDepartamento.toList())
        adapter.notifyDataSetChanged()

        if(departamentos.size > 0){
            departamentos.forEach {
                departs.add(it.nome!!)
            }
        }

        if(departamentos.isEmpty()){
            viewStub.visibility = View.VISIBLE
            listViewDepartamentos.visibility = View.GONE
        }else{
            viewStub.visibility = View.GONE
            listViewDepartamentos.visibility = View.VISIBLE
        }
    }

    override fun afterInsert(result: Any?) {
        if (result == null) {
            Toast.makeText(this, getString(R.string.mensagem_error_departamento_criacao), Toast.LENGTH_LONG).show()
        } else {
            var depart = result as Departamento?
            if (depart!!.uid != null) {
                departamentos.add(depart)
                adapter.notifyDataSetChanged()
                departamento = null
                Toast.makeText(this, getString(R.string.mensagem_criado_sucesso), Toast.LENGTH_LONG).show()
                viewStub.visibility = View.GONE
                listViewDepartamentos.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, getString(R.string.mensagem_error_departamento_criacao), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun afterUpdate(result: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun buttonSheetLista(item: Any?) {
    }

    override fun buttonSheetAdiciona(item: Any?) {
    }

    override fun buttonSheetRemove(item: Any?, position: Int) {
        DeleteDepartamentoAsyncTask(getCfvApplication().getDataBase()!!.departamentoDAO(), this)
            .execute((item as Departamento).uid, position)
    }

    override fun buttonSheetEdita(item: Any?) {
        openInsertDialog(item as Departamento)

    }

    override fun afterDelete(result: Any?) {
        if(result as Int > -1) {
            ToastUtils.showToastSuccess(
                this,
                getString(R.string.mensagem_sucesso)
            )
            departamentos.removeAt(result)
            adapter.notifyDataSetChanged()
        }else{
            ToastUtils.showToastError(
                this,
                getString(R.string.mensagem_erro)
            )
        }
    }

    override fun getToobarTitle(): String {
        return getString(R.string.listar_departamentos_title)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        getMenuInflater().inflate(R.menu.menu_search,menu)

        var menuItem : MenuItem = menu.findItem(R.id.searchView)

        var searchView = menuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String) : Boolean {

                getAllDepartamentos(newText, false)

                return true
            }
        })

        return true
    }

}
