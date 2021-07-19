package br.com.rm.cfv.activities.produto

import android.content.Context
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
import br.com.rm.cfv.activities.VisualizarImagemActivity
import com.google.common.base.Strings
import kotlinx.android.synthetic.main.activity_cadastrar_produto.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.util.*


class CadastrarProdutoActivity : ImageUtilsActivity(), IPostExecuteSearch, IPostExecuteInsertAndUpdate{

    override fun onPostCaptureCompleted(bitmap: Bitmap?, path: String?) {
        if(bitmap == null){
            this.imageBitmap = null
            this.imageFilePath = null
            imageViewProduto.setImageResource(R.drawable.no_image)
        }else {
            this.imageBitmap = bitmap!!
            this.imageFilePath = path!!
            imageViewProduto.setImageBitmap(bitmap)
        }
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
    private var proxCodigo : Int = 0
    private var imageFilePath: String? = null

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

        SelectAllNamesAsyncTask(getCfvApplication().getDataBase()!!.departamentoDAO(), this).execute()

        proxCodigo = getProximoCodigoProduto()
        val codigoFormatado = Strings.padStart(proxCodigo.toString(), 5, '0')
        textInputEditCodigo.setText(codigoFormatado)

        fab().setOnClickListener(View.OnClickListener {
            val id = textInputEditId.text.toString()
            var idToInt = if (id.isBlank()) null else id.toInt()
            val codigo = textInputEditCodigo.text.toString()
            val nome = textInputEditNome.text.toString()
            val precoCusto = textInputEditPrecoCusto.text.toString()
            val precoRevenda = textInputEditPrecoRevenda.text.toString()
            val departamento = autocompleteTextViewDepartamento.text.toString()
            val precoCustoToDouble = if (precoCusto.isBlank()) null else precoCusto.toDouble()
            val precoRevendaToDouble = if (precoRevenda.isBlank()) null else precoRevenda.toDouble()
            val permiteEstoqueNegativo = switchEstoqueNegativo.isChecked
            val produto = Produto(idToInt, nome, codigo, precoCustoToDouble, precoRevendaToDouble, saveImage(codigo), departamento, permiteEstoqueNegativo)

            if(produto.validate(mapFields)){
                if(idToInt == null){
                    salvaUltimoCodigoProduto(proxCodigo)
                }
               InsertProdutoAsyncTask(getCfvApplication().getDataBase()!!, this).execute(produto)
            }

        })

        fab.setImageResource(R.drawable.content_save_black_24dp)

        imageViewProduto.setOnClickListener{
            if(imageFilePath !== null){
                val intent = Intent(it.context, VisualizarImagemActivity::class.java)
                intent.putExtra("filepath", imageFilePath)
                startActivity(intent)
            }
        }

        hideFabOnScroll(scrollView)
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
                textInputEditPrecoRevenda.setText(produto.precoVenda!!.toString())
                autocompleteTextViewDepartamento.setText(produto.departamento)
                autocompleteTextViewDepartamento.showDropDown()
                autocompleteTextViewDepartamento.performCompletion()
                autocompleteTextViewDepartamento.setSelection(0)
                autocompleteTextViewDepartamento.listSelection = 0
                autocompleteTextViewDepartamento.dismissDropDown()
                switchEstoqueNegativo.isChecked = produto.permiteEstoqueNegativo

                if (produto.caminhoImagem != null && !produto.caminhoImagem!!.isBlank()) {
                    imageFilePath = produto.caminhoImagem!!
                    imageBitmap = getBitmapFromAbsolutePath(produto.caminhoImagem)
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
        imageFilePath = file.absolutePath
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

    fun getProximoCodigoProduto(): Int{
        val sharedPreferences = getSharedPreferences("PREF_PRODUTO", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("ULTIMO_CODIGO", 0) + 1
    }

    fun salvaUltimoCodigoProduto(codigo : Int){
        val sharedPreferences = getSharedPreferences("PREF_PRODUTO", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("ULTIMO_CODIGO", codigo).apply()
    }

}
