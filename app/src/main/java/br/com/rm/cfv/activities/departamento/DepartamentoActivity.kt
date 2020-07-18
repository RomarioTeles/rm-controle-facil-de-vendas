package br.com.rm.cfv.activities.departamento

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.adapters.departamento.DepartamentoAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.cliente.InsertDepartamentoAsyncTask
import br.com.rm.cfv.asyncTasks.cliente.SelectAllDepartamentosAsyncTask
import br.com.rm.cfv.database.entities.Departamento
import br.com.rm.cfv.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_departamento.*

class DepartamentoActivity : BaseActivity() , IPostExecuteSearch, IPostExecuteInsertAndUpdate {

    override fun getToobarTitle(): String {
        return getString(R.string.listar_departamentos_title)
    }

    var departamento : Departamento? = null

    lateinit var adapter : DepartamentoAdapter

    lateinit var departamentos : MutableList<Departamento>

    var departs : MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departamento)

        departamentos = mutableListOf()

        adapter = DepartamentoAdapter(this, departamentos)

        listViewDepartamentos.adapter = adapter

        /**
         * adiciona header a listview
         */
        //val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //var listViewHeader =inflater.inflate(R.layout.list_view_header_default, null)
        //listViewDepartamentos.addHeaderView(listViewHeader)

        setClickEvents()

    }

    private fun setClickEvents(){

        fab().setOnClickListener {
            openDialog()
        }

    }

    private fun openDialog(){
        val view = getLayoutInflater().inflate(R.layout.departamento_bottom_seet, null)
        var dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        view.findViewById<Button>(R.id.buttonAddDepart).setOnClickListener {
            var nome = view.findViewById<TextInputEditText>(R.id.textViewDepartamento).text.toString()
            if(nome.isBlank()) {
                view.findViewById<TextInputLayout>(R.id.textInputLayoutDepartamento).error = getString(R.string.mensagem_campo_requerido)
            }else{
                view.findViewById<TextInputLayout>(R.id.textInputLayoutDepartamento).error = null
                view.findViewById<TextInputEditText>(R.id.textViewDepartamento).text = null
                departamento = Departamento(null, nome, null)
                var task = InsertDepartamentoAsyncTask(getCfvApplication().getDataBase()!!.departamentoDAO(), this)
                task.execute(departamento)
            }
        }

        view.findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        var task = SelectAllDepartamentosAsyncTask(getCfvApplication().getDataBase()!!.departamentoDAO(), this)
        task.execute()
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
    }

    override fun afterInsert(result: Any?) {
        if (result == null) {
            Toast.makeText(this, "Erro ao criar departamento!", Toast.LENGTH_LONG).show()
        } else {
            var depart = result as Departamento?
            if (depart!!.uid != null) {
                departamentos.add(depart)
                adapter.notifyDataSetChanged()
                departamento = null
                Toast.makeText(this, "Criado com sucesso!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Erro ao criar departamento!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun afterUpdate(result: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
