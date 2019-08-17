package br.com.rm.cfv.activities.cliente

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.ImageUtilsActivity
import br.com.rm.cfv.activities.produto.IOnClickProdutoListener
import br.com.rm.cfv.adapters.MeioPagamentoAdapter
import br.com.rm.cfv.adapters.produto.ItemProdutoAdapter
import br.com.rm.cfv.adapters.produto.ProdutoAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.debitoCliente.InsertDebitoClienteAsyncTask
import br.com.rm.cfv.asyncTasks.produto.SelectAllProdutosAsyncTask
import br.com.rm.cfv.constants.MeioPagamento
import br.com.rm.cfv.database.entities.Cliente
import br.com.rm.cfv.database.entities.DebitoCliente
import br.com.rm.cfv.database.entities.ItemProduto
import br.com.rm.cfv.database.entities.Produto
import br.com.rm.cfv.utils.DatePicker
import br.com.rm.cfv.utils.IDatePicker
import br.com.rm.cfv.utils.ToastUtils
import br.com.rm.dateutils.DateFormatUtils
import br.com.rm.numberUtils.DecimalFormatUtils
import kotlinx.android.synthetic.main.activity_registrar_debito.*
import kotlinx.android.synthetic.main.content_registrar_debito_adicionar_produto.*
import kotlinx.android.synthetic.main.content_registrar_debito_cesta.*
import kotlinx.android.synthetic.main.content_registrar_debito_pagamento_meio.*
import kotlinx.android.synthetic.main.content_registrar_debito_pagamento_tipo.*
import kotlinx.android.synthetic.main.content_registrar_debito_produtos.*
import kotlinx.android.synthetic.main.content_registrar_debito_resumo.*
import java.util.*

class RegistrarDebitoActivity :  ImageUtilsActivity(), IPostExecuteSearch, IOnClickProdutoListener, IDatePicker,
    IPostExecuteInsertAndUpdate {

    override fun afterInsert(result: Any?) {
        if(result == null){
            Toast.makeText(this, "Erro ao registrar o débito cliente!", Toast.LENGTH_LONG).show()
        }else{
            var cliente = result as DebitoCliente?
            if(cliente!!.uid != null) {
                Toast.makeText(this, "Registrado com sucesso!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, ListaClientesActivity::class.java))
            }else{
                Toast.makeText(this, "Erro ao registrar o débito cliente!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun afterUpdate(result: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDatePicked(myCalendar: Calendar) {
        debitoCliente.dataPrevistaPagamento = myCalendar.time.time
        textViewDataPrevistaPagamento.text = DateFormatUtils.format(myCalendar.time, "dd/MM/yyyy")
    }

    private lateinit var produtoRecyclerView: RecyclerView
    private lateinit var itemProdutoRecyclerView: RecyclerView
    private lateinit var produtoAdapter: ProdutoAdapter
    private lateinit var itemProdutoAdapter: ItemProdutoAdapter
    private lateinit var meioPagamentoAdapter: MeioPagamentoAdapter
    private lateinit var produtoLayoutManager: RecyclerView.LayoutManager
    private lateinit var itemProdutoLayoutManager: RecyclerView.LayoutManager
    private var listaProdutos : List<Produto> = ArrayList()
    private var listaLayoutsTela = mutableListOf<View>()
    private lateinit var itemProdutoEmEdicao : ItemProduto
    private var debitoCliente : DebitoCliente = DebitoCliente()
    private lateinit var cliente : Cliente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_registrar_debito)

        cliente = intent.getSerializableExtra("cliente") as Cliente

        debitoCliente.clienteId = cliente.uid
        debitoCliente.clienteNome = cliente.nome

        linearLayoutFinalizar.visibility = View.GONE

        meioPagamentoAdapter = MeioPagamentoAdapter(this)
        listViewMeioPagamento.adapter = meioPagamentoAdapter

        produtoLayoutManager = LinearLayoutManager(this)

        itemProdutoLayoutManager = LinearLayoutManager(this)

        produtoAdapter = ProdutoAdapter(this, this, listaProdutos)

        itemProdutoAdapter = ItemProdutoAdapter(this, debitoCliente.itemProdutoList)

        produtoRecyclerView = findViewById<RecyclerView>(R.id.recyclerViewProdutos).apply {
            setHasFixedSize(true)
            layoutManager = produtoLayoutManager
            adapter = produtoAdapter
        }

        itemProdutoRecyclerView = findViewById<RecyclerView>(R.id.recyclerViewItensProdutos).apply {
            setHasFixedSize(true)
            layoutManager = itemProdutoLayoutManager
            adapter = itemProdutoAdapter
        }

        hideFab()

        listaLayoutsTela.add(registrarDebitoProdutos)
        listaLayoutsTela.add(registrarDebitoCesta)
        listaLayoutsTela.add(registrarDebitoAdicionarProduto)
        listaLayoutsTela.add(linearLayoutFinalizar)
        listaLayoutsTela.add(linearLayoutVerCesta)
        listaLayoutsTela.add(registrarDebitoPagamentoMeio)
        listaLayoutsTela.add(registrarDebitoPagamentoTipo)
        listaLayoutsTela.add(registrarDebitoResumo)

        editTextSearch.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                pesquisarProdutos(v.text.toString())
                true
            } else {
                false
            }
        }

        linearLayoutVerCesta.setOnClickListener{
            if(debitoCliente.itemProdutoList.isEmpty()){
                Toast.makeText(this, getString(R.string.mensagem_cesta_vazia), Toast.LENGTH_LONG).show();
            }else {
                mudaEstadoDaTela(registrarDebitoCesta)
                linearLayoutFinalizar.visibility = View.VISIBLE
            }
        }

        linearLayoutFinalizar.setOnClickListener{
            mudaEstadoDaTela(registrarDebitoPagamentoTipo)
        }

        buttonCancelarProduto.setOnClickListener{
            limpaItemProduto()
            mudaEstadoDaTela(registrarDebitoProdutos)
        }

        buttonAdicionarProduto.setOnClickListener{
            if(!debitoCliente.itemProdutoList.contains(itemProdutoEmEdicao)) {
                debitoCliente.itemProdutoList.add(itemProdutoEmEdicao)
                itemProdutoAdapter.setDataset(debitoCliente.itemProdutoList)
            }
            atualizaResumoCesta()
            limpaItemProduto()
            itemProdutoAdapter.notifyDataSetChanged()
            mudaEstadoDaTela(registrarDebitoProdutos)
            linearLayoutVerCesta.visibility = View.VISIBLE
        }

        imageViewItemRemover.setOnClickListener{
            if(debitoCliente.itemProdutoList.contains(itemProdutoEmEdicao)){
                debitoCliente.itemProdutoList.remove(itemProdutoEmEdicao)
                atualizaResumoCesta()
            }
            mudaEstadoDaTela(registrarDebitoProdutos)
        }

        radioGroupAcresDesc.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(radioAcrescimo.isChecked){
                itemProdutoEmEdicao.setDesconto(0.0)
            }else{
                itemProdutoEmEdicao.setAcrescimo(0.0)
            }
            itemProdutoEmEdicao.atualizaSubtotal()
            editTextItemDescAcresc.isEnabled = true
            imageButtonCancelDescAcresc.isEnabled = true
            imageButtonConfirmDescAcresc.isEnabled = true
            editTextItemDescAcresc.setText("")
            atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)
        }

        imageButtonConfirmDescAcresc.setOnClickListener{
            atualizaDescAcresc()
        }

        imageButtonCancelDescAcresc.setOnClickListener{
            imageButtonConfirmDescAcresc.isEnabled = true
            itemProdutoEmEdicao.setAcrescimo(0.0)
            itemProdutoEmEdicao.setDesconto(0.0)
            itemProdutoEmEdicao.atualizaSubtotal()
            atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)
        }

        buttonIncrementarQuantidade.setOnClickListener{
            var quantidade = itemProdutoEmEdicao.getQuantidade()
            quantidade++
            textInputQuantidade.setText(quantidade.toString())
            itemProdutoEmEdicao.setQuantidade(quantidade)
            atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)

        }

        buttonDecrementarQuantidade.setOnClickListener{
            var quantidade = itemProdutoEmEdicao.getQuantidade()
            quantidade--
            quantidade = if(quantidade <= 0) 1 else quantidade
            textInputQuantidade.setText(quantidade.toString())
            itemProdutoEmEdicao.setQuantidade(quantidade)
            atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)
        }

        textInputQuantidade.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                var valor : Int  = if(v.text.isBlank()) 0 else v.text.toString().toInt()
                itemProdutoEmEdicao.setQuantidade(valor)
                atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)
                true
            } else {
                false
            }
        }

        editTextItemDescAcresc.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                atualizaDescAcresc()
                true
            } else {
                false
            }
        }

        fabAdicionarMaisProdutos.setOnClickListener{
            mudaEstadoDaTela(registrarDebitoProdutos)
            linearLayoutVerCesta.visibility = View.VISIBLE
        }

        radioGroupTipoPagamento.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(radioAvista.isChecked){
                buttonAvancar.text = "Concluir"
                imageButtonEscolherDataPrevista.isEnabled = false
            }else{
                buttonAvancar.text = "Avançar"
                imageButtonEscolherDataPrevista.isEnabled = true
            }
        }

        buttonAvancar.setOnClickListener{
            if(radioAprazo.isChecked){
                mudaEstadoDaTela(registrarDebitoPagamentoMeio)
            }else{
                registrarPedido()
            }
        }

        listViewMeioPagamento.setOnItemClickListener{parent, view, position, id ->
            debitoCliente.meioPagamento = MeioPagamento.values().get(position).name
            registrarPedido()
        }

        imageButtonEscolherDataPrevista.setOnClickListener{
            DatePicker(this, this).createDialog().show()
        }

        textViewDataPrevistaPagamento.setOnClickListener{
            DatePicker(this, this).createDialog().show()
        }

        mudaEstadoDaTela(registrarDebitoProdutos)

        imageButtonEscolherDataPrevista.isEnabled = false
    }

    private fun registrarPedido() {
            ToastUtils.showToastConfirm(this, getString(R.string.mensagem_sucesso))
            var task = InsertDebitoClienteAsyncTask(getCfvApplication().getDataBase(), this)
            debitoCliente.itemProdutoList = itemProdutoAdapter.getListaProdutos().toMutableList()
            debitoCliente.atualizaTotal()
            task.execute(debitoCliente)
    }

    private fun atualizaDescAcresc(){
        var valor = editTextItemDescAcresc.text.toString()
        var valDouble = if(valor.isBlank()) 0.0 else valor.toDouble()
        itemProdutoEmEdicao.setDesconto(0.0)
        itemProdutoEmEdicao.setAcrescimo(0.0)
        itemProdutoEmEdicao.atualizaSubtotal()
        if(radioAcrescimo.isChecked){
            itemProdutoEmEdicao.setAcrescimo(valDouble)
        }else if(radioDesconto.isChecked){
            itemProdutoEmEdicao.setDesconto(valDouble)
        }
        atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)
        if(valor.isNotBlank()){
            imageButtonConfirmDescAcresc.isEnabled = false
        }
    }

    private fun atualizaSubtotalItemEmEdicao(valor : Double){
        textViewItemSubtotal.text = """R$ ${DecimalFormatUtils.decimalFormatPtBR(valor)}"""
    }

    private fun atualizaResumoCesta(){
        textViewquantidadeItens.text = debitoCliente.itemProdutoList.size.toString()
        textViewSubtotalItens.text = DecimalFormatUtils.decimalFormatPtBR(debitoCliente.getSubtotal())
    }

    private fun limpaItemProduto(){
        itemProdutoEmEdicao = ItemProduto()
        textInputQuantidade.setText("")
        textViewItemNomeProduto.text = ""
        atualizaSubtotalItemEmEdicao(0.0)
        imageViewItemProduto.setImageDrawable(resources.getDrawable(R.drawable.no_image))
        radioAcrescimo.isChecked = false
        radioDesconto.isChecked = false
        editTextItemDescAcresc.isEnabled = false
        editTextItemDescAcresc.setText("")
        imageButtonCancelDescAcresc.isEnabled = false
        imageButtonConfirmDescAcresc.isEnabled = false
    }

    private fun mudaEstadoDaTela(v : View){
        v.visibility = View.VISIBLE
        var listException = mutableListOf<Int>()
        listException.add(registrarDebitoAdicionarProduto.id)
        listException.add(registrarDebitoPagamentoMeio.id)
        listException.add(registrarDebitoPagamentoTipo.id)
        listaLayoutsTela.forEach {
            if(it.id != v.id){
                it.visibility = View.GONE
            }
            if(it.id in listException){
                registrarDebitoResumo.visibility = View.GONE
            }else{
                registrarDebitoResumo.visibility = View.VISIBLE
            }
            if(it.id == registrarDebitoCesta.id){
                linearLayoutFinalizar.visibility = View.VISIBLE
                linearLayoutVerCesta.visibility = View.GONE
            }else if(it.id == registrarDebitoProdutos.id){
                linearLayoutFinalizar.visibility = View.GONE
                linearLayoutVerCesta.visibility = View.VISIBLE
            }
        }
    }

    private fun pesquisarProdutos(search: String) {
        SelectAllProdutosAsyncTask(getCfvApplication().getDataBase()!!.produtoDAO(), this)
        .execute(search)
    }

    @SuppressLint("SetTextI18n")
    override fun onProdutoClick(produto : Produto) {

        itemProdutoEmEdicao = ItemProduto()

        itemProdutoEmEdicao.codigoProduto = produto.codigo
        itemProdutoEmEdicao.nomeProduto = produto.nome
        itemProdutoEmEdicao.precoUnitario = produto.precoVenda!!
        itemProdutoEmEdicao.subtotal = produto.precoVenda!!

        if(debitoCliente.itemProdutoList.contains(itemProdutoEmEdicao)){
            itemProdutoEmEdicao = debitoCliente.itemProdutoList.find { it.codigoProduto == itemProdutoEmEdicao.codigoProduto }!!
            itemProdutoEmEdicao.setQuantidade(itemProdutoEmEdicao.getQuantidade() + 1)
        }

        textInputQuantidade.setText(itemProdutoEmEdicao.getQuantidade().toString())
        textViewItemNomeProduto.text = itemProdutoEmEdicao.nomeProduto.toString()
        atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)
        if (produto.caminhoImagem != null && !produto.caminhoImagem!!.isBlank()) {
            imageViewItemProduto.setImageBitmap(getBitmapFromAbsolutePath(produto.caminhoImagem, false))
        }

        registrarDebitoAdicionarProduto.visibility = View.VISIBLE
    }

    override fun getToobarTitle(): String {
        return getString(R.string.caderno_de_contas)
    }

    override fun afterSearch(result: Any?) {
        listaProdutos = result as List<Produto>
        produtoAdapter.setDataset(listaProdutos)
    }

    override fun onPostCaptureCompleted(bitmap: Bitmap?) {

    }

    override fun getCaptureTrigger(): View? {
        return null
    }
}
