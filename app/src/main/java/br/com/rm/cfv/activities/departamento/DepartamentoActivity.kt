package br.com.rm.cfv.activities.departamento

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.adapters.departamento.DepartamentoAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteDelete
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.cliente.InsertDepartamentoAsyncTask
import br.com.rm.cfv.asyncTasks.cliente.SelectAllDepartamentosAsyncTask
import br.com.rm.cfv.database.entities.Departamento
import com.rm.cfv.R
import kotlinx.android.synthetic.main.activity_departamento.*

class DepartamentoActivity : BaseActivity() , IPostExecuteSearch, IPostExecuteInsertAndUpdate {

    var departamento : Departamento? = null

    lateinit var adapter : DepartamentoAdapter

    lateinit var departamentos : MutableList<Departamento>

    val acaoListar : String = "LISTA"

    val acaoCadastro : String = "CADASTRO"

    var acaoAtual : String = acaoListar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departamento)

        departamentos = mutableListOf()

        adapter = DepartamentoAdapter(this, departamentos)

        listViewDepartamentos.adapter = adapter

        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var listViewHeader =inflater.inflate(R.layout.list_view_header_default, null)
        listViewDepartamentos.addHeaderView(listViewHeader)

        setClickEvents()

        hideFab()
    }

    private fun setClickEvents(){
        buttonAddDepart.setOnClickListener {
            acaoAtual = acaoCadastro
            var nome = editTextDepartNome.text.toString()
            if(nome.isBlank()) {
                editTextDepartNome.error = "Nome não pode ser vazio."
            }else{
                departamento = Departamento(null, nome)
                var task = InsertDepartamentoAsyncTask(getCfvApplication().getDataBase()!!.departamentoDAO(), this)
                task.execute(departamento)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        acaoAtual = acaoListar
        var task = SelectAllDepartamentosAsyncTask(getCfvApplication().getDataBase()!!.departamentoDAO(), this)
        task.execute()
    }

    override fun afterSearch(result: Any?) {
        val listaDepartamento = result as List<Departamento>
        departamentos.addAll(listaDepartamento.toList())
        adapter.notifyDataSetChanged()
    }

    override fun afterInsert(result: Any?) {
        if (result == null) {
            Toast.makeText(this, "Erro ao criar departamento!", Toast.LENGTH_LONG).show()
            editTextDepartNome.error = "Verifique se o Nome já está cadastrado."
        } else {
            var depart = result as Departamento?
            if (depart!!.uid != null) {
                departamentos.add(depart)
                adapter.notifyDataSetChanged()
                departamento = null
                editTextDepartNome.text = null
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
