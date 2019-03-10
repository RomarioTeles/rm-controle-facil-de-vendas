package br.com.rm.cfv.activities.produto

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.cliente.SelectAllNamesAsyncTask
import br.com.rm.cfv.asyncTasks.produto.InsertProdutoAsyncTask
import br.com.rm.cfv.database.entities.Produto
import com.google.android.material.textfield.TextInputLayout
import com.rm.cfv.R
import kotlinx.android.synthetic.main.activity_cadastrar_produto.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*


class CadastrarProdutoActivity : BaseActivity(), IPostExecuteSearch, IPostExecuteInsertAndUpdate{

    lateinit var departs : List<String>
    private lateinit var mapFields : HashMap<String, TextInputLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_produto)
        toolbar.title = "Cadastro de Produto"
        mapFields = HashMap()
        mapFields["codigo"] = textInputLayoutCodigo
        mapFields["nome"] = textInputLayoutNome
        mapFields["categoria"] = textInputLayoutDepartamento
        mapFields["precoCusto"] = textInputLayoutPrecoCusto
        mapFields["precoVenda"] = textInputLayoutPrecoRevenda
        mapFields["precoTabela"] = textInputLayoutPrecoTabela

        SelectAllNamesAsyncTask(getCfvApplication().getDataBase()!!.departamentoDAO(), this).execute()

        fab().setOnClickListener(View.OnClickListener {
            var codigo = textInputEditCodigo.text.toString()
            var nome = textInputEditNome.text.toString()
            var precoCusto = textInputEditPrecoCusto.text.toString()
            var precoTabela = textInputEditPrecoTabela.text.toString()
            var precoRevenda = textInputEditPrecoRevenda.text.toString()
            var departamento = autocompleteTextViewDepartamento.text.toString()

            var precoCustoD = if (precoCusto.isBlank()) null else precoCusto.toDouble()
            var precoTabelaD = if (precoTabela.isBlank())null else precoTabela.toDouble()
            var precoRevendaD = if (precoRevenda.isBlank()) null else precoRevenda.toDouble()

            var produto = Produto(null, nome, codigo, precoTabelaD, precoCustoD, precoRevendaD, null, departamento)

            if(produto.validate(mapFields)){
               InsertProdutoAsyncTask(getCfvApplication().getDataBase()!!, this).execute(produto)
            }

        })

    }

    override fun afterSearch(result: Any?) {
        departs = result as List<String>
        val adapter = ArrayAdapter(this, android.R.layout.select_dialog_item, departs)
        val actv = autocompleteTextViewDepartamento
        actv.threshold = 1
        actv.setAdapter(adapter)
    }

    override fun afterInsert(result: Any?) {
        if(result == null){
            Toast.makeText(this, "Erro ao criar Produto!", Toast.LENGTH_LONG).show()
            textInputLayoutCodigo.error = "Verifique se o Código já está cadastrado."
        }else{
            var produto = result as Produto?
            if(produto!!.uid != null) {
                Toast.makeText(this, "Criado com sucesso!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, ListaProdutosActivity::class.java))
            }else{
                Toast.makeText(this, "Erro ao criar produto!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun afterUpdate(result: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
