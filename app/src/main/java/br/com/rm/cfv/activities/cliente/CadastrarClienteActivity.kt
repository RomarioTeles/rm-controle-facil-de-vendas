package br.com.rm.cfv.activities.cliente

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.cliente.InsertClienteAsyncTask
import br.com.rm.cfv.database.entities.Cliente
import br.com.rm.cfv.R
import kotlinx.android.synthetic.main.activity_cadastrar_cliente.*
import java.util.*

class CadastrarClienteActivity : BaseActivity(), IPostExecuteInsertAndUpdate {
    override fun getToobarTitle(): String {
        return getString(R.string.cadastrar_cliente_title)
    }

    private lateinit var cliente : Cliente

    private lateinit var mapFields : HashMap<String, EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_cliente)

        mapFields = HashMap()
        mapFields["nome"] = editTextNome
        mapFields["telefone"] = editTextTelefone
        mapFields["cpf"] = editTextCPF
        mapFields["email"] = editTextEmail
        mapFields["dataNascimento"] = editTextDataNascimento
        mapFields["endereco"] = editTextEndereco
        mapFields["numero"] = editTextNumero
        mapFields["bairro"] = editTextBairro
        mapFields["cidade"] = editTextCidade
        mapFields["uf"] = editTextUf
        mapFields["complemento"] = editTextComplemento

        setClickEvents()
    }

    private fun setClickEvents(){
        fab().setOnClickListener {
            var nome = editTextNome.text.toString()
            var telefone = editTextTelefone.text.toString()
            var cpf = editTextCPF.text.toString()
            var email = editTextEmail.text.toString()
            var dataNasc = editTextDataNascimento.text.toString()
            var endereco = editTextEndereco.text.toString()
            var numero = editTextNumero.text.toString()
            var bairro = editTextBairro.text.toString()
            var cidade = editTextCidade.text.toString()
            var uf = editTextUf.text.toString()
            var complemento = editTextComplemento.text.toString()
            cliente = Cliente(null, nome, cpf, telefone, email, dataNasc, endereco, numero, complemento, bairro, cidade, uf)
            if(cliente.validate(mapFields)){
                var task = InsertClienteAsyncTask(getCfvApplication().getDataBase()!!.clienteDAO(), this)
                task.execute(cliente)
            }
        }

        hideFabOnScroll(scrollView)
    }

    override fun afterInsert(result: Any?) {
        if(result == null){
            Toast.makeText(this, "Erro ao criar cliente!", Toast.LENGTH_LONG).show()
            editTextCPF.error = "Verifique se o CPF já está cadastrado."
        }else{
            var cliente = result as Cliente?
            if(cliente!!.uid != null) {
                Toast.makeText(this, "Criado com sucesso!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, ListaClientesActivity::class.java))
            }else{
                Toast.makeText(this, "Erro ao criar cliente!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun afterUpdate(result: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
