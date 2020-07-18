package br.com.rm.cfv.activities.departamento

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.adapters.departamento.DepartamentoAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.cliente.InsertDepartamentoAsyncTask
import br.com.rm.cfv.asyncTasks.cliente.SelectAllDepartamentosAsyncTask
import br.com.rm.cfv.database.entities.Departamento
import br.com.rm.cfv.R
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

        framelayout_cad_departamento.visibility = View.GONE

        buttonAddDepart.setOnClickListener {
            var nome = textViewDepartamento.text.toString()
            var nomePai = autocompleteTextViewDepartamentoPai.text.toString()
            if(nome.isBlank()) {
                textInputLayoutDepartamento.error = "Nome não pode ser vazio."
            }else{
                departamento = Departamento(null, nome, nomePai)
                var task = InsertDepartamentoAsyncTask(getCfvApplication().getDataBase()!!.departamentoDAO(), this)
                task.execute(departamento)
            }
        }

        fab().setOnClickListener {
            framelayout_cad_departamento.visibility = View.VISIBLE
            textViewDepartamento.text = null
            autocompleteTextViewDepartamentoPai.text = null
        }

        buttonCancel.setOnClickListener {
            framelayout_cad_departamento.visibility = View.GONE
            textViewDepartamento.text = null
            autocompleteTextViewDepartamentoPai.text = null
        }
    }

    override fun onResume() {
        super.onResume()
        var task = SelectAllDepartamentosAsyncTask(getCfvApplication().getDataBase()!!.departamentoDAO(), this)
        task.execute()
    }

    override fun afterSearch(result: Any?) {
        val listaDepartamento = result as List<Departamento>
        departamentos.addAll(listaDepartamento.toList())
        adapter.notifyDataSetChanged()
        if(departamentos.size > 0){
            departamentos.forEach {
                departs.add(it.nome!!)
            }
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, departs)
        val actv = autocompleteTextViewDepartamentoPai
        actv.threshold = 1
        actv.setAdapter(adapter)
    }

    override fun afterInsert(result: Any?) {
        if (result == null) {
            Toast.makeText(this, "Erro ao criar departamento!", Toast.LENGTH_LONG).show()
            textInputLayoutDepartamento.error = "Verifique se o Nome já está cadastrado."
        } else {
            var depart = result as Departamento?
            if (depart!!.uid != null) {
                departamentos.add(depart)
                adapter.notifyDataSetChanged()
                departamento = null
                textViewDepartamento.text = null
                autocompleteTextViewDepartamentoPai.text = null
                textInputLayoutDepartamento.error = null
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
