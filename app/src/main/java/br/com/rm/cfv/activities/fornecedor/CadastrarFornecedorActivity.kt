package br.com.rm.cfv.activities.fornecedor

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.fornecedor.InsertFornecedorAsyncTask
import br.com.rm.cfv.database.entities.Fornecedor
import br.com.rm.cfv.utils.EditTextMaskUtil
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_cadastrar_fornecedor.*
import kotlinx.android.synthetic.main.activity_cadastrar_fornecedor.editTextBairro
import kotlinx.android.synthetic.main.activity_cadastrar_fornecedor.editTextCPF
import kotlinx.android.synthetic.main.activity_cadastrar_fornecedor.editTextCidade
import kotlinx.android.synthetic.main.activity_cadastrar_fornecedor.editTextComplemento
import kotlinx.android.synthetic.main.activity_cadastrar_fornecedor.editTextEmail
import kotlinx.android.synthetic.main.activity_cadastrar_fornecedor.editTextEndereco
import kotlinx.android.synthetic.main.activity_cadastrar_fornecedor.editTextId
import kotlinx.android.synthetic.main.activity_cadastrar_fornecedor.editTextNome
import kotlinx.android.synthetic.main.activity_cadastrar_fornecedor.editTextNumero
import kotlinx.android.synthetic.main.activity_cadastrar_fornecedor.editTextTelefone
import kotlinx.android.synthetic.main.activity_cadastrar_fornecedor.editTextUf
import kotlinx.android.synthetic.main.activity_cadastrar_fornecedor.scrollView
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*

class CadastrarFornecedorActivity : BaseActivity(), IPostExecuteInsertAndUpdate {
    override fun getToobarTitle(): String {
        return getString(R.string.cadastrar_fornecedor_title)
    }

    private lateinit var fornecedor : Fornecedor

    private lateinit var mapFields : HashMap<String, TextInputLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_fornecedor)

        mapFields = HashMap()
        mapFields["nome"] = textInputLayoutNome
        mapFields["telefone"] = textInputLayoutTelefone
        mapFields["cpfCnpj"] = textInputLayoutCPF
        mapFields["email"] = textInputLayoutEmail
        mapFields["endereco"] = textInputLayoutEndereco
        mapFields["numero"] = textInputLayoutNumero
        mapFields["bairro"] = textInputLayoutBairro
        mapFields["cidade"] = textInputLayoutCidade
        mapFields["uf"] = textInputLayoutUf
        mapFields["complemento"] = textInputLayoutComplemento

        setClickEvents()
    }

    private fun setClickEvents(){
        fab().setOnClickListener {
            val id = editTextId.text.toString()
            val nome = editTextNome.text.toString()
            val telefone = editTextTelefone.text.toString()
            val cpf = editTextCPF.text.toString()
            val email = editTextEmail.text.toString()
            val endereco = editTextEndereco.text.toString()
            val numero = editTextNumero.text.toString()
            val bairro = editTextBairro.text.toString()
            val cidade = editTextCidade.text.toString()
            val uf = editTextUf.text.toString()
            val complemento = editTextComplemento.text.toString()
            val idInt = if(id.isBlank()) null else id.toInt()

            fornecedor = Fornecedor(idInt, nome, cpf, telefone, email, endereco, numero, complemento, bairro, cidade, uf)
            if(fornecedor.validate(mapFields)){
                val task = InsertFornecedorAsyncTask(getCfvApplication().getDataBase()!!.fornecedorDAO(), this)
                task.execute(fornecedor)
            }
        }

        editTextCPF.addTextChangedListener(EditTextMaskUtil.insert(editTextCPF, EditTextMaskUtil.MASK_CPF_CNPJ))
        editTextTelefone.addTextChangedListener(EditTextMaskUtil.insert(editTextTelefone, EditTextMaskUtil.MASK_TELEFONE))

        hideFabOnScroll(scrollView)

        preencheFornecedor()

        fab.setImageResource(R.drawable.content_save_black_24dp)

    }

    private fun preencheFornecedor(){
        val bundle = intent.extras

        if(bundle != null) {

            var fornecedor = bundle.getParcelable("fornecedor") as Fornecedor?

            if (fornecedor != null) {
                editTextId.setText(fornecedor.uid.toString())
                editTextNome.setText(fornecedor.nome)
                editTextTelefone.setText(fornecedor.telefone)
                editTextEmail.setText(fornecedor.email)
                editTextCPF.setText(fornecedor.cpfCnpj)
                editTextEndereco.setText(fornecedor.endereco)
                editTextNumero.setText(fornecedor.numero)
                editTextComplemento.setText(fornecedor.complemento)
                editTextBairro.setText(fornecedor.bairro)
                editTextUf.setText(fornecedor.uf)
                editTextCidade.setText(fornecedor.cidade)
            }
        }
    }

    override fun afterInsert(result: Any?) {
        if(result == null){
            Toast.makeText(this, "Erro ao criar fornecedor!", Toast.LENGTH_LONG).show()
            editTextCPF.error = "Verifique se o CPF/CNPJ já está cadastrado."
        }else{
            var fornecedor = result as Fornecedor?
            if(fornecedor!!.uid != null) {
                Toast.makeText(this, "Criado com sucesso!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, ListaFornecedorActivity::class.java))
            }else{
                Toast.makeText(this, "Erro ao criar fornecedor!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun afterUpdate(result: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
