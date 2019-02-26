package br.com.rm.cfv.activities.cliente

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.asyncTasks.IAsyncTaskPostExecute
import br.com.rm.cfv.asyncTasks.cliente.InsertClienteAsyncTask
import br.com.rm.cfv.database.entities.Cliente
import com.rm.cfv.R
import kotlinx.android.synthetic.main.activity_cadastrar_cliente.*

class CadastrarClienteActivity : BaseActivity(), IAsyncTaskPostExecute {

    private lateinit var cliente : Cliente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_cliente)

        setClickEvents()
    }

    private fun setClickEvents(){
        fab().setOnClickListener {
            var nome = editTextNome.text.toString()
            var telefone = editTextTelefone.text.toString()
            var cpf = editTextCPF.text.toString()

            cliente = Cliente(null, nome, cpf, telefone)

            var task = InsertClienteAsyncTask(getCfvApplication().getDataBase()!!.clienteDAO(), this)
            task.execute(cliente)
        }
    }

    override fun afterExecute(result: Any?) {
        if(result != null){
            var cliente = result as Cliente?
            if(cliente!!.uid != null) {
                Toast.makeText(this, "Criado com sucesso!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, ListaClientesActivity::class.java))
            }else{
                Toast.makeText(this, "Erro ao criar cliente!", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this, "Erro ao criar cliente!", Toast.LENGTH_LONG).show()
        }
    }
}
