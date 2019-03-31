package br.com.rm.cfv.activities.produto

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import br.com.rm.cfv.activities.ImageUtilsActivity
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.cliente.SelectAllNamesAsyncTask
import br.com.rm.cfv.asyncTasks.produto.InsertProdutoAsyncTask
import br.com.rm.cfv.database.entities.Produto
import com.google.android.material.textfield.TextInputLayout
import br.com.rm.cfv.R
import kotlinx.android.synthetic.main.activity_cadastrar_produto.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.util.*


class CadastrarProdutoActivity : ImageUtilsActivity(), IPostExecuteSearch, IPostExecuteInsertAndUpdate{

    override fun onPostCaptureCompleted(bitmap: Bitmap?) {
        this.imageBitmap = bitmap!!
        imageViewProduto.setImageBitmap(bitmap)
    }

    override fun getCaptureTrigger(): View {
       return textViewEditarImagem
    }

    override fun getToobarTitle(): String {
        return getString(R.string.cadastrar_produto_title)
    }

    lateinit var departs : List<String>
    private lateinit var mapFields : HashMap<String, TextInputLayout>
    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_produto)
        toolbar.title = "Cadastro de Produto"
        mapFields = HashMap()
        mapFields["codigo"] = textInputLayoutCodigo
        mapFields["nome"] = textInputLayoutNome
        mapFields["departamento"] = textInputLayoutDepartamento
        mapFields["precoCusto"] = textInputLayoutPrecoCusto
        mapFields["precoVenda"] = textInputLayoutPrecoRevenda
        mapFields["precoTabela"] = textInputLayoutPrecoTabela

        SelectAllNamesAsyncTask(getCfvApplication().getDataBase()!!.departamentoDAO(), this).execute()

        fab().setOnClickListener(View.OnClickListener {
            val id = textInputEditId.text.toString()
            var idToInt = if (id.isBlank()) null else id.toInt()
            val codigo = textInputEditCodigo.text.toString()
            val nome = textInputEditNome.text.toString()
            val precoCusto = textInputEditPrecoCusto.text.toString()
            val precoTabela = textInputEditPrecoTabela.text.toString()
            val precoRevenda = textInputEditPrecoRevenda.text.toString()
            val departamento = autocompleteTextViewDepartamento.text.toString()
            val precoCustoToDouble = if (precoCusto.isBlank()) null else precoCusto.toDouble()
            val precoTabelaToDouble = if (precoTabela.isBlank())null else precoTabela.toDouble()
            val precoRevendaToDouble = if (precoRevenda.isBlank()) null else precoRevenda.toDouble()
            val produto = Produto(idToInt, nome, codigo, precoTabelaToDouble, precoCustoToDouble, precoRevendaToDouble, saveImage(codigo), departamento)

            if(produto.validate(mapFields)){
               InsertProdutoAsyncTask(getCfvApplication().getDataBase()!!, this).execute(produto)
            }

        })
    }

    private fun preencheProduto(){
        val bundle = intent.extras

        if(bundle != null) {

            var produto: Produto? = bundle.getSerializable("produto") as Produto?

            if (produto != null) {
                textInputEditId.setText(produto.uid!!.toString())
                textInputEditId.visibility = View.VISIBLE
                textInputEditCodigo.setText(produto.codigo)
                textInputEditNome.setText(produto.nome)
                textInputEditPrecoCusto.setText(produto.precoCusto!!.toString())
                textInputEditPrecoTabela.setText(produto.precoTabela!!.toString())
                textInputEditPrecoRevenda.setText(produto.precoVenda!!.toString())
                autocompleteTextViewDepartamento.setText(produto.departamento)
                autocompleteTextViewDepartamento.showDropDown()
                autocompleteTextViewDepartamento.performCompletion()
                autocompleteTextViewDepartamento.setSelection(0)
                autocompleteTextViewDepartamento.listSelection = 0
                autocompleteTextViewDepartamento.dismissDropDown()

                if (produto.caminhoImagem != null && !produto.caminhoImagem!!.isBlank()) {
                    imageBitmap = getBitmapFromAbsolutePath(produto.caminhoImagem, false)
                    imageViewProduto.setImageBitmap(imageBitmap)
                }
            }
        }
    }

    private fun saveImage(codigoProduto : String) : String?{
        if(imageBitmap == null){
            return null
        }
        val file = createImageFile("produto_"+codigoProduto+".JPEG")
        val bos = ByteArrayOutputStream()
        imageBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, bos)
        val bitmapdata = bos.toByteArray()
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return file.absolutePath
    }

    override fun afterSearch(result: Any?) {
        departs = result as List<String>
        val adapter = ArrayAdapter(this, android.R.layout.select_dialog_item, departs)
        val actv = autocompleteTextViewDepartamento
        actv.threshold = 1
        actv.setAdapter(adapter)
        preencheProduto()
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
