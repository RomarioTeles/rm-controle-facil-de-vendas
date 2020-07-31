package br.com.rm.cfv.activities.cliente

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.cliente.InsertClienteAsyncTask
import br.com.rm.cfv.database.entities.Cliente
import br.com.rm.cfv.utils.EditTextMaskUtil
import kotlinx.android.synthetic.main.activity_cadastrar_cliente.*
import kotlinx.android.synthetic.main.activity_cadastrar_cliente.scrollView
import kotlinx.android.synthetic.main.app_bar_main.*
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
            val id = editTextId.text.toString()
            val nome = editTextNome.text.toString()
            val telefone = editTextTelefone.text.toString()
            val cpf = editTextCPF.text.toString()
            val email = editTextEmail.text.toString()
            val dataNasc = editTextDataNascimento.text.toString()
            val endereco = editTextEndereco.text.toString()
            val numero = editTextNumero.text.toString()
            val bairro = editTextBairro.text.toString()
            val cidade = editTextCidade.text.toString()
            val uf = editTextUf.text.toString()
            val complemento = editTextComplemento.text.toString()
            val idInt = if(id.isBlank()) null else id.toInt()

            cliente = Cliente(idInt, nome, cpf, telefone, email, dataNasc, endereco, numero, complemento, bairro, cidade, uf)
            if(cliente.validate(mapFields)){
                val task = InsertClienteAsyncTask(getCfvApplication().getDataBase()!!.clienteDAO(), this)
                task.execute(cliente)
            }
        }

        editTextCPF.addTextChangedListener(EditTextMaskUtil.insert(editTextCPF, EditTextMaskUtil.MASK_CPF_CNPJ))
        editTextDataNascimento.addTextChangedListener(EditTextMaskUtil.insert(editTextDataNascimento, EditTextMaskUtil.MASK_DATE))
        editTextTelefone.addTextChangedListener(EditTextMaskUtil.insert(editTextTelefone, EditTextMaskUtil.MASK_TELEFONE))

        hideFabOnScroll(scrollView)

        preencheCliente()

        fab.setImageResource(R.drawable.content_save_black_24dp)

    }

    private fun preencheCliente(){
        val bundle = intent.extras

        if(bundle != null) {

            var cliente = bundle.getSerializable("cliente") as Cliente?

            if (cliente != null) {
                editTextId.setText(cliente.uid.toString())
                editTextNome.setText(cliente.nome)
                editTextTelefone.setText(cliente.telefone)
                editTextEmail.setText(cliente.email)
                editTextCPF.setText(cliente.cpf)
                editTextDataNascimento.setText(cliente.dataNascimento)
                editTextEndereco.setText(cliente.endereco)
                editTextNumero.setText(cliente.numero)
                editTextComplemento.setText(cliente.complemento)
                editTextBairro.setText(cliente.bairro)
                editTextUf.setText(cliente.uf)
                editTextCidade.setText(cliente.cidade)
            }
        }
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
