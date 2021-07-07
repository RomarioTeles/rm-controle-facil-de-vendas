package br.com.rm.cfv.activities.contaPagarReceber.compra_venda_produtos

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.activities.ImageUtilsActivity.getBitmapFromAbsolutePath
import br.com.rm.cfv.activities.cliente.ListaClientesActivity
import br.com.rm.cfv.activities.produto.IOnClickProdutoListener
import br.com.rm.cfv.adapters.MeioPagamentoAdapter
import br.com.rm.cfv.adapters.ParcelasAdapter
import br.com.rm.cfv.adapters.produto.IOnClickItemProdutoListener
import br.com.rm.cfv.adapters.produto.ItemProdutoAdapter
import br.com.rm.cfv.adapters.produto.ProdutoAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.contaPagarReceber.InsertContaPagarReceberAsyncTask
import br.com.rm.cfv.asyncTasks.produto.SelectAllProdutosAsyncTask
import br.com.rm.cfv.constants.MeioPagamento
import br.com.rm.cfv.constants.TipoPagamento
import br.com.rm.cfv.constants.TipoReferencia
import br.com.rm.cfv.database.entities.*
import br.com.rm.numberUtils.DecimalFormatUtils
import kotlinx.android.synthetic.main.activity_registrar_debito.*
import kotlinx.android.synthetic.main.content_registrar_debito_adicionar_produto.*
import kotlinx.android.synthetic.main.content_registrar_debito_concluido.*
import kotlinx.android.synthetic.main.content_registrar_debito_pagamento.*
import kotlinx.android.synthetic.main.content_registrar_debito_pagamento_meio.*
import kotlinx.android.synthetic.main.content_registrar_debito_pagamento_parcelamento.*
import kotlinx.android.synthetic.main.content_registrar_debito_pagamento_wizard.*
import kotlinx.android.synthetic.main.content_registrar_debito_produtos.*
import kotlinx.android.synthetic.main.content_registrar_debito_resumo.*
import kotlinx.android.synthetic.main.content_registrar_debito_tipo_pagamento.*
import kotlinx.android.synthetic.main.content_registrar_debito_tipo_vencimento.*
import java.util.*
import kotlin.collections.HashMap
import br.com.rm.cfv.R;

class RegistrarCompraVendaActivity : BaseActivity(), IPostExecuteSearch, IOnClickProdutoListener,
    IPostExecuteInsertAndUpdate, IOnClickItemProdutoListener {

    private lateinit var produtoRecyclerView: RecyclerView
    private lateinit var itemProdutoRecyclerView: RecyclerView
    private lateinit var produtoAdapter: ProdutoAdapter
    private lateinit var itemProdutoAdapter: ItemProdutoAdapter
    private lateinit var meioPagamentoAdapter: MeioPagamentoAdapter
    private lateinit var produtoLayoutManager: RecyclerView.LayoutManager
    private lateinit var itemProdutoLayoutManager: RecyclerView.LayoutManager
    private var listaProdutos: List<Produto> = ArrayList()
    private var listaLayoutsTela = mutableListOf<View>()
    private lateinit var itemProdutoEmEdicao: ItemProduto
    private var contaPagarReceber: ContaPagarReceber = ContaPagarReceber()
    private lateinit var referencia: IReferencia
    private lateinit var adapterParcelas: ParcelasAdapter
    private lateinit var pagWizardViewVoltar: View
    private lateinit var pagWizardViewAvancar: View
    private lateinit var pagWizardViewCurrent: View
    private lateinit var menu: Menu
    private var mapWizardControls = HashMap<Int, Array<View>>()
    private var flag_cesta : Boolean = false
    private var isFABOpen : Boolean = false

    companion object{
        var ARG_REFERENCIA = "referencia"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_registrar_debito)

        if(intent.hasExtra(ARG_REFERENCIA)){
            referencia = intent.getParcelableExtra<Parcelable>(ARG_REFERENCIA) as IReferencia
            contaPagarReceber.idRef = referencia.getIdRef()
            contaPagarReceber.nomeRef = referencia.getNomeRef()
            contaPagarReceber.tipoRef = referencia.getTipoRef()
        }else{
            contaPagarReceber.idRef = -1
            contaPagarReceber.nomeRef = getString(R.string.cliente_nao_informado)
            contaPagarReceber.tipoRef = TipoReferencia.CLIENTE
        }

        adicionaAcoesComponentes()
    }

    private fun adicionaAcoesComponentes() {

        meioPagamentoAdapter = MeioPagamentoAdapter(this)
        listViewMeioPagamento.adapter = meioPagamentoAdapter

        produtoLayoutManager = GridLayoutManager(this, 1)

        itemProdutoLayoutManager = LinearLayoutManager(this)

        produtoAdapter = ProdutoAdapter(this, this, listaProdutos)

        itemProdutoAdapter = ItemProdutoAdapter(this, this, contaPagarReceber.itemProdutoList)

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

        fabSettings()

        listaLayoutsTela.add(registrarDebitoProdutos)
        listaLayoutsTela.add(registrarDebitoCesta)
        listaLayoutsTela.add(registrarDebitoAdicionarProduto)
        listaLayoutsTela.add(registrarDebitoPagamentoMeio)
        listaLayoutsTela.add(registrarDebitoPagamentoTipo)
        listaLayoutsTela.add(registrarDebitoPagamentoParcelas)
        listaLayoutsTela.add(registrarDebitoPagamentoVencimento)
        listaLayoutsTela.add(registrarDebitoConcluido)

        mapWizardControls.put(
            registrarDebitoPagamentoTipo.id,
            arrayOf(registrarDebitoCesta, registrarDebitoPagamentoMeio)
        )

        mapWizardControls.put(
            registrarDebitoPagamentoMeio.id,
            arrayOf(registrarDebitoPagamentoTipo, registrarDebitoPagamentoParcelas)
        )

        mapWizardControls.put(
            registrarDebitoPagamentoParcelas.id,
            arrayOf(registrarDebitoPagamentoMeio, registrarDebitoPagamentoVencimento)
        )
        mapWizardControls.put(
            registrarDebitoPagamentoVencimento.id,
            arrayOf(registrarDebitoPagamentoParcelas, registrarDebitoConcluido)
        )

        editTextSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                pesquisarProdutos(v.text.toString())
                true
            } else {
                false
            }
        }

        buttonCancelarProduto.setOnClickListener {
            limpaItemProduto()
            mudaEstadoDaTela(registrarDebitoProdutos)
        }

        buttonAdicionarProduto.setOnClickListener {
            if (!contaPagarReceber.itemProdutoList.contains(itemProdutoEmEdicao)) {
                contaPagarReceber.itemProdutoList.add(itemProdutoEmEdicao)
                itemProdutoAdapter.setDataset(contaPagarReceber.itemProdutoList)
            }
            atualizaResumoCesta()
            limpaItemProduto()
            itemProdutoAdapter.notifyDataSetChanged()
            if(flag_cesta){
                flag_cesta = false
                mudaEstadoDaTela(registrarDebitoCesta)
            }else {
                mudaEstadoDaTela(registrarDebitoProdutos)
            }
        }

        imageViewItemRemover.setOnClickListener {
            if (contaPagarReceber.itemProdutoList.contains(itemProdutoEmEdicao)) {
                contaPagarReceber.itemProdutoList.remove(itemProdutoEmEdicao)
                atualizaResumoCesta()
            }
            mudaEstadoDaTela(registrarDebitoProdutos)
        }

        radioGroupAcresDesc.setOnCheckedChangeListener { buttonView, isChecked ->
            if (radioAcrescimo.isChecked) {
                itemProdutoEmEdicao.setDesconto(0.0)
            } else {
                itemProdutoEmEdicao.setAcrescimo(0.0)
            }
            itemProdutoEmEdicao.atualizaSubtotal()
            editTextItemDescAcresc.isEnabled = true
            imageButtonCancelDescAcresc.isEnabled = true
            imageButtonConfirmDescAcresc.isEnabled = true
            editTextItemDescAcresc.setText("")
            atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)
        }

        imageButtonConfirmDescAcresc.setOnClickListener {
            atualizaDescAcresc()
        }

        imageButtonCancelDescAcresc.setOnClickListener {
            imageButtonConfirmDescAcresc.isEnabled = true
            itemProdutoEmEdicao.setAcrescimo(0.0)
            itemProdutoEmEdicao.setDesconto(0.0)
            itemProdutoEmEdicao.atualizaSubtotal()
            atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)
        }

        buttonIncrementarQuantidade.setOnClickListener {
            var quantidade = itemProdutoEmEdicao.getQuantidade()
            quantidade++
            textInputQuantidade.setText(quantidade.toString())
            itemProdutoEmEdicao.setQuantidade(quantidade)
            atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)

        }

        buttonDecrementarQuantidade.setOnClickListener {
            var quantidade = itemProdutoEmEdicao.getQuantidade()
            quantidade--
            quantidade = if (quantidade <= 0) 1 else quantidade
            textInputQuantidade.setText(quantidade.toString())
            itemProdutoEmEdicao.setQuantidade(quantidade)
            atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)
        }

        textInputQuantidade.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                var valor: Int = if (v.text.isBlank()) 0 else v.text.toString().toInt()
                itemProdutoEmEdicao.setQuantidade(valor)
                atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)
                hideKeyboard()
                true
            } else {
                false
            }
        }

        editTextItemDescAcresc.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                atualizaDescAcresc()
                hideKeyboard()
                true
            } else {
                false
            }
        }

        listViewMeioPagamento.setOnItemClickListener { parent, view, position, id ->
            contaPagarReceber.meioPagamento = meioPagamentoAdapter.getItem(position)!!.name
            meioPagamentoAdapter.selected = meioPagamentoAdapter.getItem(position)
            meioPagamentoAdapter.notifyDataSetChanged()
        }

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val date = GregorianCalendar()
            date.set(year, month, dayOfMonth, 0, 0, 0)
            contaPagarReceber.dataPrevistaPagamento = date.time.time
        }

        buttonPagAvista.setOnClickListener {
            contaPagarReceber.tipoPagamento = TipoPagamento.A_VISTA
            buttonPagPrazo.setBackgroundColor(ContextCompat.getColor(this, R.color.secondaryColor))
            buttonPagAvista.setBackgroundColor(ContextCompat.getColor(this,R.color.secondaryLightColor))
            meioPagamentoAdapter.setTipoPagamento(TipoPagamento.A_VISTA)
            initListaDeParcelas()
        }

        buttonPagPrazo.setOnClickListener {
            contaPagarReceber.tipoPagamento = TipoPagamento.A_PRAZO
            buttonPagPrazo.setBackgroundColor(ContextCompat.getColor(this,R.color.secondaryLightColor))
            buttonPagAvista.setBackgroundColor(ContextCompat.getColor(this,R.color.secondaryColor))
            meioPagamentoAdapter.setTipoPagamento(TipoPagamento.A_PRAZO)
            initListaDeParcelas()
        }

        buttonAvancar.setOnClickListener {

            if(pagWizardViewCurrent == registrarDebitoPagamentoMeio){
                if (contaPagarReceber.tipoPagamento == TipoPagamento.A_VISTA) {
                    registrarPedido()
                }else{
                    mudaEstadoDaTela(pagWizardViewAvancar)
                }
            }else if(pagWizardViewCurrent == registrarDebitoPagamentoVencimento){
                registrarPedido()
            }else{
                mudaEstadoDaTela(pagWizardViewAvancar)
            }
        }

        buttonVoltar.setOnClickListener {
            mudaEstadoDaTela(pagWizardViewVoltar)
        }

        buttonConcluido.setOnClickListener {
            finish()
        }

        pesquisarProdutos("")
        mudaEstadoDaTela(registrarDebitoProdutos)
        initListaDeParcelas()
    }

    private fun fabSettings() {

        hideFabOnScroll(produtoRecyclerView, fab())
        hideFabOnScroll(produtoRecyclerView, fabOptionCesta)
        hideFabOnScroll(produtoRecyclerView, fabOptionCliente)
        hideFabOnScroll(produtoRecyclerView, fabOptionPesquisar)
        hideFabOnScroll(produtoRecyclerView, fabOptionFinalizar)

        fab().setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }

        fabOptionCesta.setOnClickListener{
            abreCesta()
        }

        fabOptionCliente.setOnClickListener {
            abreSelecionarCliente()
        }

        fabOptionPesquisar.setOnClickListener {
            mudaEstadoDaTela(registrarDebitoProdutos)
        }

        fabOptionFinalizar.setOnClickListener {
            textViewPagandoSubtotal.text =
                getString(R.string.currency_format, DecimalFormatUtils.decimalFormatPtBR(contaPagarReceber.total))
            mudaEstadoDaTela(registrarDebitoPagamentoTipo)
        }

        fabOptionPesquisar.hide()
    }

    override fun onItemProdutoClick(item: ItemProduto) {
        itemProdutoEmEdicao = item
        flag_cesta = true
        preparaTelaEdicaoProduto(itemProdutoEmEdicao)
        mudaEstadoDaTela(registrarDebitoAdicionarProduto)
    }

    override fun afterInsert(result: Any?) {
        if (result == null) {
            Toast.makeText(this, "Erro ao registrar o débito!", Toast.LENGTH_LONG).show()
        } else {
            var conta = result as ContaPagarReceber?
            if (conta!!.uid != null) {
                mudaEstadoDaTela(registrarDebitoConcluido)
            } else {
                Toast.makeText(this, "Erro ao registrar o débito!", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
    private fun showFABMenu() {
        isFABOpen = true
        fabOptionCliente.animate().translationY(-resources.getDimension(R.dimen.standard_55))
        fabOptionCesta.animate().translationY(-resources.getDimension(R.dimen.standard_105))
        fabOptionFinalizar.animate().translationY(-resources.getDimension(R.dimen.standard_105))
        fabOptionPesquisar.animate().translationY(-resources.getDimension(R.dimen.standard_155))
    }

    private fun closeFABMenu() {
        isFABOpen = false
        fabOptionCesta.animate().translationY(0f)
        fabOptionCliente.animate().translationY(0f)
        fabOptionPesquisar.animate().translationY(0f)
        fabOptionFinalizar.animate().translationY(0f)
    }

    override fun afterUpdate(result: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun registrarPedido() {
        var task = InsertContaPagarReceberAsyncTask(getCfvApplication().getDataBase(), this)
        contaPagarReceber.itemProdutoList = itemProdutoAdapter.getListaProdutos().toMutableList()
        contaPagarReceber.atualizaTotal(getParcelasComJuros())
        task.execute(contaPagarReceber)
    }

    private fun atualizaDescAcresc() {
        var valor = editTextItemDescAcresc.text.toString()
        var valDouble = if (valor.isBlank()) 0.0 else valor.toDouble()
        itemProdutoEmEdicao.setDesconto(0.0)
        itemProdutoEmEdicao.setAcrescimo(0.0)
        if (radioAcrescimo.isChecked) {
            itemProdutoEmEdicao.setAcrescimo(valDouble)
        } else if (radioDesconto.isChecked) {
            itemProdutoEmEdicao.setDesconto(valDouble)
        }
        atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)

    }

    private fun atualizaSubtotalItemEmEdicao(valor: Double) {
        textViewItemSubtotal.text = getString(R.string.currency_format, DecimalFormatUtils.decimalFormatPtBR(valor))
    }

    private fun atualizaResumoCesta() {
        textViewquantidadeItens.text = contaPagarReceber.itemProdutoList.size.toString()
        textViewSubtotalItens.text =
            getString(R.string.currency_format,DecimalFormatUtils.decimalFormatPtBR(contaPagarReceber.getSubtotal(getParcelasComJuros())))
        /*if(contaPagarReceber.itemProdutoList.size > 0){
            menu.getItem(0).setIcon(R.drawable.basket)
        }else{
            menu.getItem(0).setIcon(R.drawable.basket_off)
        }*/
    }

    private fun limpaItemProduto() {
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

    private fun mudaEstadoDaTela(v: View) {
        v.visibility = View.VISIBLE
        var listViewsPagamento = mutableListOf<Int>()
        listViewsPagamento.add(registrarDebitoConcluido.id)
        listViewsPagamento.add(registrarDebitoPagamentoVencimento.id)
        listViewsPagamento.add(registrarDebitoPagamentoMeio.id)
        listViewsPagamento.add(registrarDebitoPagamentoTipo.id)
        listViewsPagamento.add(registrarDebitoPagamentoParcelas.id)

        listaLayoutsTela.forEach {
            if (it.id != v.id) {
                it.visibility = View.GONE
            }
        }

        if (v.id in listViewsPagamento) {
            hideFab()
            contaPagarReceber.percentualJurosParcelas = getPercentualJuros(contaPagarReceber.qtdeParcelas)
            mudaEstadoTelaPagamento(v)
        } else {
            showFab()
            closeFABMenu()
            contaPagarReceber.percentualJurosParcelas = 0.0
            mudaEstadoTelaVenda(v)
        }
    }

    override fun hideFab() {
        super.hideFab()
        fabOptionPesquisar.hide()
        fabOptionCesta.hide()
        fabOptionCliente.hide()
        fabOptionFinalizar.hide()
    }

    override fun showFab() {
        super.showFab()
        fabOptionPesquisar.show()
        fabOptionCesta.show()
        fabOptionCliente.show()
        fabOptionFinalizar.show()
    }

    fun mudaEstadoTelaPagamento(v: View) {
        if (v.id != registrarDebitoConcluido.id) {

            if(contaPagarReceber.total >= getValorMinimoParcelamento() && (contaPagarReceber.idRef!!).coerceAtLeast(-1) > -1 ){
                buttonPagPrazo.visibility = View.VISIBLE
            }else{
                buttonPagPrazo.visibility = View.GONE
            }

            if(contaPagarReceber.tipoPagamento == TipoPagamento.A_VISTA){
                buttonPagAvista.performClick()
            }else{
                meioPagamentoAdapter.selected = MeioPagamento.PARCELAMENTO_DA_LOJA
                buttonPagPrazo.performClick()
            }

            registrarDebitoPagamento.visibility = View.VISIBLE
            registrarDebitoResumo.visibility = View.GONE

            var wizardControls = mapWizardControls.get(v.id)
            pagWizardViewVoltar = wizardControls!!.get(0)
            pagWizardViewAvancar = wizardControls.get(1)
            pagWizardViewCurrent = v
        }
    }

    fun mudaEstadoTelaVenda(v: View) {
        registrarDebitoPagamento.visibility = View.GONE
        registrarDebitoResumo.visibility = View.VISIBLE

        if (v.id == registrarDebitoAdicionarProduto.id) {
            registrarDebitoResumo.visibility = View.GONE
            hideFab()
        } else if (v.id == registrarDebitoCesta.id) {
            fabOptionCesta.hide()
            fabOptionPesquisar.show()
            fabOptionFinalizar.show()
        } else if (v.id == registrarDebitoProdutos.id) {
            fabOptionCesta.show()
            fabOptionPesquisar.hide()
            fabOptionFinalizar.hide()
        }
    }

    private fun pesquisarProdutos(search: String) {
        SelectAllProdutosAsyncTask(getCfvApplication().getDataBase()!!.produtoDAO(), this)
            .execute(search)
    }

    @SuppressLint("SetTextI18n")
    override fun onProdutoClick(produto: Produto, position: Int?, isLongClick: Boolean) {
        itemProdutoEmEdicao = ItemProduto()
        itemProdutoEmEdicao.imagePath = produto.caminhoImagem
        itemProdutoEmEdicao.codigoProduto = produto.codigo
        itemProdutoEmEdicao.nomeProduto = produto.nome
        val preco = if (contaPagarReceber.tipoRef == TipoReferencia.CLIENTE) produto.precoVenda!! else produto.precoCusto!!
        itemProdutoEmEdicao.precoUnitario = preco
        itemProdutoEmEdicao.subtotal = preco

        if (contaPagarReceber.itemProdutoList.contains(itemProdutoEmEdicao)) {
            itemProdutoEmEdicao =
                contaPagarReceber.itemProdutoList.find { it.codigoProduto == itemProdutoEmEdicao.codigoProduto }!!
            if(isLongClick && itemProdutoEmEdicao.getQuantidade() == 0) {
                itemProdutoEmEdicao.setQuantidade(itemProdutoEmEdicao.getQuantidade() + 1)
            }else{
                itemProdutoEmEdicao.setQuantidade(itemProdutoEmEdicao.getQuantidade())
            }
        }

        preparaTelaEdicaoProduto(itemProdutoEmEdicao)

        if (isLongClick) {
            mudaEstadoDaTela(registrarDebitoAdicionarProduto)
        } else {
            buttonAdicionarProduto.performClick()
            //Toast.makeText(this, "Produto foi adicionado!", Toast.LENGTH_LONG).show();
        }
    }


    fun preparaTelaEdicaoProduto(itemProdutoEmEdicao: ItemProduto){
        textInputQuantidade.setText(itemProdutoEmEdicao.getQuantidade().toString())
        textViewItemNomeProduto.text = itemProdutoEmEdicao.nomeProduto.toString()
        textViewItemPrecoProduto.text = getString(R.string.currency_format, DecimalFormatUtils.decimalFormatPtBR(itemProdutoEmEdicao.precoUnitario))
        atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)
        if (itemProdutoEmEdicao.imagePath != null && !itemProdutoEmEdicao.imagePath!!.isBlank()) {
            imageViewItemProduto.setImageBitmap(
                getBitmapFromAbsolutePath(
                    itemProdutoEmEdicao.imagePath
                )
            )
        }
    }

    override fun getToobarTitle(): String {
        return getString(R.string.nova_venda)
    }

    override fun afterSearch(result: Any?) {
        listaProdutos = result as List<Produto>
        produtoAdapter.setDataset(listaProdutos)
    }

    fun initListaDeParcelas() {
        adapterParcelas = ParcelasAdapter(this)
        var total = contaPagarReceber.total
        adapterParcelas.init(total, getQuantidadeDeParcela(total), getParcelasComJuros(), getJurosValor())
        listViewParcelamento.adapter = adapterParcelas
        listViewParcelamento.setOnItemClickListener { parent, view, position, id ->
            atualizaTotalPagamento(position+1)
            adapterParcelas.selected = position
            adapterParcelas.notifyDataSetChanged()
        }
        atualizaTotalPagamento(contaPagarReceber.qtdeParcelas)
        adapterParcelas.selected = 0
        adapterParcelas.notifyDataSetChanged()
    }

    fun atualizaTotalPagamento(qtdeParcelas: Int? = 1){
        contaPagarReceber.qtdeParcelas = qtdeParcelas!!
        contaPagarReceber.percentualJurosParcelas = getPercentualJuros(contaPagarReceber.qtdeParcelas)
        contaPagarReceber.atualizaTotal(getParcelasComJuros())
        textViewPagandoSubtotal.text =
            getString(R.string.currency_format, DecimalFormatUtils.decimalFormatPtBR(contaPagarReceber.total))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        /*this.menu = menu
        menuInflater.inflate(R.menu.menu_registrar_debito, menu)
        if(contaPagarReceber.idRef!!.compareTo(-1) > 0){
            menu.getItem(1).setIcon(R.drawable.account_black_24dp)
            if(contaPagarReceber.tipoRef == TipoReferencia.FORNECEDOR){
                menu.getItem(1).setVisible(false)
            }
        }
        return true*/
        return false;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cesta -> {
                abreCesta()
                return true
            }
            R.id.action_ref ->{
                abreSelecionarCliente()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun abreSelecionarCliente() {
        if(contaPagarReceber.tipoRef == TipoReferencia.CLIENTE) {
            val intent = Intent(this, ListaClientesActivity::class.java)
            intent.putExtra("SELECTABLE", true)
            startActivityForResult(intent, 1)
        }
    }

    private fun abreCesta() {
        if (contaPagarReceber.itemProdutoList.isEmpty()) {
            Toast.makeText(this, getString(R.string.mensagem_cesta_vazia), Toast.LENGTH_LONG).show()
        } else {
            mudaEstadoDaTela(registrarDebitoCesta)
        }
    }

    private fun getPercentualJuros(qtdeParcelas: Int) : Double{
        val prefParcela = getParcelasComJuros()
        val prefJuros = getJurosValor()
        return if (qtdeParcelas < prefParcela) 0.0 else prefJuros
    }

    private fun getParcelasComJuros(): Int{
        try{
            val prefParcela = getPreferences().getString("juros_parcela", "1")
            return prefParcela!!.toInt()
        }catch (e: Exception){
            return 1
        }
    }



    private fun getNumeroMaxParcelas(): Int{
        try{
            val prefParcela = getPreferences().getString("max_parcela", "1")
            return prefParcela!!.toInt()
        }catch (e: Exception){
            return 1
        }
    }

    private fun getJurosValor(): Double{
        try {
            if(contaPagarReceber.tipoRef == TipoReferencia.FORNECEDOR){
                return 0.0
            }else {
                val prefJuros = getPreferences().getString("juros_valor", "0")
                return prefJuros!!.toDouble()
            }
        }catch (e: Exception){
            return 0.0
        }
    }

    private fun getValorMinimoParcelamento(): Double{
        try {
            val prefValorMin = getPreferences().getString("valor_minimo_parcelamento", "0.0")
            return prefValorMin!!.toDouble()
        }catch (e: Exception){
            return 0.0
        }
    }

    private fun getQuantidadeDeParcela(totalDaCompra: Double = 0.0): Int{
        try {
            val valorMinimoParcela = getValorMinimoParcelamento()
            if(valorMinimoParcela.coerceAtLeast(0.0) > 0) {
                var numeroMaxParcelas = getNumeroMaxParcelas()
                val quantParcelas = Math.floor(totalDaCompra.div(valorMinimoParcela)).toInt()
                return Math.min(quantParcelas, numeroMaxParcelas)
            }else{
                return getNumeroMaxParcelas()
            }
        }catch (e: Exception){
            Log.e("Quantidade Parcelas", e.message, e)
            return 1
        }
    }

    private fun getPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if(data!!.hasExtra("result")) {
                var cliente = data!!.extras!!.get("result") as Cliente
                contaPagarReceber.tipoRef = cliente.getTipoRef()
                contaPagarReceber.idRef = cliente.getIdRef()
                contaPagarReceber.nomeRef = cliente.getNomeRef()
                supportActionBar!!.title = cliente.getNomeRef()
                //menu.getItem(1).setIcon(R.drawable.account_black_24dp)
            }
        }
    }
}
