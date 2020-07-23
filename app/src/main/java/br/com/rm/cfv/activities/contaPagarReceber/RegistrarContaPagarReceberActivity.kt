package br.com.rm.cfv.activities.contaPagarReceber

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.ImageUtilsActivity
import br.com.rm.cfv.activities.produto.IOnClickProdutoListener
import br.com.rm.cfv.adapters.MeioPagamentoAdapter
import br.com.rm.cfv.adapters.ParcelasAdapter
import br.com.rm.cfv.adapters.produto.ItemProdutoAdapter
import br.com.rm.cfv.adapters.produto.ProdutoAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.IPostExecuteSearch
import br.com.rm.cfv.asyncTasks.contaPagarReceber.InsertContaPagarReceberAsyncTask
import br.com.rm.cfv.asyncTasks.produto.SelectAllProdutosAsyncTask
import br.com.rm.cfv.constants.TipoPagamento
import br.com.rm.cfv.constants.TipoReferencia
import br.com.rm.cfv.database.entities.*
import br.com.rm.numberUtils.DecimalFormatUtils
import kotlinx.android.synthetic.main.activity_registrar_debito.*
import kotlinx.android.synthetic.main.content_registrar_debito_adicionar_produto.*
import kotlinx.android.synthetic.main.content_registrar_debito_cesta.*
import kotlinx.android.synthetic.main.content_registrar_debito_concluido.*
import kotlinx.android.synthetic.main.content_registrar_debito_pagamento.*
import kotlinx.android.synthetic.main.content_registrar_debito_pagamento_meio.*
import kotlinx.android.synthetic.main.content_registrar_debito_pagamento_parcelamento.*
import kotlinx.android.synthetic.main.content_registrar_debito_pagamento_wizard.*
import kotlinx.android.synthetic.main.content_registrar_debito_produtos.*
import kotlinx.android.synthetic.main.content_registrar_debito_resumo.*
import kotlinx.android.synthetic.main.content_registrar_debito_resumo.textViewSubtotalItens
import kotlinx.android.synthetic.main.content_registrar_debito_tipo_pagamento.*
import kotlinx.android.synthetic.main.content_registrar_debito_tipo_vencimento.*
import java.util.*
import kotlin.collections.HashMap

class RegistrarContaPagarReceberActivity : ImageUtilsActivity(), IPostExecuteSearch, IOnClickProdutoListener,
    IPostExecuteInsertAndUpdate {

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

    override fun afterUpdate(result: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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

    companion object{
        var ARG_REFERENCIA = "referencia"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_registrar_debito)

        referencia = intent.getSerializableExtra(ARG_REFERENCIA) as IReferencia

        contaPagarReceber.idRef = referencia.getIdRef()
        contaPagarReceber.nomeRef = referencia.getNomeRef()
        contaPagarReceber.tipoRef = referencia.getTipoRef()

        adicionaAcoesComponentes()
    }

    private fun adicionaAcoesComponentes() {
        linearLayoutFinalizar.visibility = View.GONE

        meioPagamentoAdapter = MeioPagamentoAdapter(this)
        listViewMeioPagamento.adapter = meioPagamentoAdapter

        produtoLayoutManager = LinearLayoutManager(this)

        itemProdutoLayoutManager = LinearLayoutManager(this)

        produtoAdapter = ProdutoAdapter(this, this, listaProdutos)

        itemProdutoAdapter = ItemProdutoAdapter(this, contaPagarReceber.itemProdutoList)

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

        hideFabOnScroll(itemProdutoRecyclerView, fabAdicionarMaisProdutos)

        hideFab()

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

        linearLayoutVerCesta.setOnClickListener {
            if (contaPagarReceber.itemProdutoList.isEmpty()) {
                Toast.makeText(this, getString(R.string.mensagem_cesta_vazia), Toast.LENGTH_LONG).show()
            } else {
                mudaEstadoDaTela(registrarDebitoCesta)
                linearLayoutFinalizar.visibility = View.VISIBLE
            }
        }

        linearLayoutFinalizar.setOnClickListener {
            contaPagarReceber.atualizaTotal()
            textViewPagandoSubtotal.text =
                "R$ ${DecimalFormatUtils.decimalFormatPtBR(contaPagarReceber.total)}"
            mudaEstadoDaTela(registrarDebitoPagamentoTipo)
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
            mudaEstadoDaTela(registrarDebitoProdutos)
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
                true
            } else {
                false
            }
        }

        editTextItemDescAcresc.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                atualizaDescAcresc()
                true
            } else {
                false
            }
        }

        fabAdicionarMaisProdutos.setOnClickListener {
            mudaEstadoDaTela(registrarDebitoProdutos)
            linearLayoutVerCesta.visibility = View.GONE
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

    private fun registrarPedido() {
        var task = InsertContaPagarReceberAsyncTask(getCfvApplication().getDataBase(), this)
        contaPagarReceber.itemProdutoList = itemProdutoAdapter.getListaProdutos().toMutableList()
        contaPagarReceber.atualizaTotal()
        task.execute(contaPagarReceber)
    }

    private fun atualizaDescAcresc() {
        var valor = editTextItemDescAcresc.text.toString()
        var valDouble = if (valor.isBlank()) 0.0 else valor.toDouble()
        itemProdutoEmEdicao.setDesconto(0.0)
        itemProdutoEmEdicao.setAcrescimo(0.0)
        itemProdutoEmEdicao.atualizaSubtotal()
        if (radioAcrescimo.isChecked) {
            itemProdutoEmEdicao.setAcrescimo(valDouble)
        } else if (radioDesconto.isChecked) {
            itemProdutoEmEdicao.setDesconto(valDouble)
        }
        atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)
        if (valor.isNotBlank()) {
            imageButtonConfirmDescAcresc.isEnabled = false
        }
    }

    private fun atualizaSubtotalItemEmEdicao(valor: Double) {
        textViewItemSubtotal.text = """R$ ${DecimalFormatUtils.decimalFormatPtBR(valor)}"""
    }

    private fun atualizaResumoCesta() {
        textViewquantidadeItens.text = contaPagarReceber.itemProdutoList.size.toString()
        textViewSubtotalItens.text =
            "R$ "+DecimalFormatUtils.decimalFormatPtBR(contaPagarReceber.getSubtotal())
        if(contaPagarReceber.itemProdutoList.size > 0){
            menu.getItem(0).setIcon(R.drawable.basket)
        }else{
            menu.getItem(0).setIcon(R.drawable.basket_off)
        }
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
            mudaEstadoTelaPagamento(v)
        } else {
            mudaEstadoTelaVenda(v)
        }
    }

    fun mudaEstadoTelaPagamento(v: View) {
        if (v.id != registrarDebitoConcluido.id) {
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

        linearLayoutFinalizar.visibility = View.GONE
        linearLayoutVerCesta.visibility = View.GONE
        if (v.id == registrarDebitoAdicionarProduto.id) {
            registrarDebitoResumo.visibility = View.GONE
        } else if (v.id == registrarDebitoCesta.id) {
            linearLayoutFinalizar.visibility = View.VISIBLE
        } else if (v.id == registrarDebitoProdutos.id) {
            linearLayoutVerCesta.visibility = View.GONE
        }
    }

    private fun pesquisarProdutos(search: String) {
        SelectAllProdutosAsyncTask(getCfvApplication().getDataBase()!!.produtoDAO(), this)
            .execute(search)
    }

    @SuppressLint("SetTextI18n")
    override fun onProdutoClick(produto: Produto, position: Int?, isLongClick: Boolean) {
        itemProdutoEmEdicao = ItemProduto()

        itemProdutoEmEdicao.codigoProduto = produto.codigo
        itemProdutoEmEdicao.nomeProduto = produto.nome
        var preco = if (referencia.getTipoRef() == TipoReferencia.CLIENTE) produto.precoVenda!! else produto.precoCusto!!
        itemProdutoEmEdicao.precoUnitario = preco
        itemProdutoEmEdicao.subtotal = preco

        if (contaPagarReceber.itemProdutoList.contains(itemProdutoEmEdicao)) {
            itemProdutoEmEdicao =
                contaPagarReceber.itemProdutoList.find { it.codigoProduto == itemProdutoEmEdicao.codigoProduto }!!
            itemProdutoEmEdicao.setQuantidade(itemProdutoEmEdicao.getQuantidade() + 1)
        }

        textInputQuantidade.setText(itemProdutoEmEdicao.getQuantidade().toString())
        textViewItemNomeProduto.text = itemProdutoEmEdicao.nomeProduto.toString()
        atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)
        if (produto.caminhoImagem != null && !produto.caminhoImagem!!.isBlank()) {
            imageViewItemProduto.setImageBitmap(
                getBitmapFromAbsolutePath(
                    produto.caminhoImagem
                )
            )
        }

        if (isLongClick) {
            mudaEstadoDaTela(registrarDebitoAdicionarProduto)
        } else {
            buttonAdicionarProduto.performClick()
            //Toast.makeText(this, "Produto foi adicionado!", Toast.LENGTH_LONG).show();
        }
    }

    override fun getToobarTitle(): String {
        return getString(R.string.caderno_de_contas)
    }

    override fun afterSearch(result: Any?) {
        listaProdutos = result as List<Produto>
        produtoAdapter.setDataset(listaProdutos)
    }

    override fun onPostCaptureCompleted(bitmap: Bitmap?, path: String?) {

    }

    override fun getCaptureTrigger(): View? {
        return null
    }

    fun initListaDeParcelas() {
        adapterParcelas = ParcelasAdapter(this)
        adapterParcelas.init(contaPagarReceber.total, 12, 5)
        listViewParcelamento.adapter = adapterParcelas
        listViewParcelamento.setOnItemClickListener { parent, view, position, id ->
            contaPagarReceber.qtdeParcelas = position + 1
            contaPagarReceber.percentualJurosParcelas = if (contaPagarReceber.qtdeParcelas < 5) 0.0 else 10.0
            contaPagarReceber.atualizaTotal()
            textViewPagandoSubtotal.text =
                "R$ ${DecimalFormatUtils.decimalFormatPtBR(contaPagarReceber.total)}"
            adapterParcelas.selected = position
            adapterParcelas.notifyDataSetChanged()
        }
        adapterParcelas.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_registrar_debito, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cesta -> {
                if (contaPagarReceber.itemProdutoList.isEmpty()) {
                    Toast.makeText(this, getString(R.string.mensagem_cesta_vazia), Toast.LENGTH_LONG).show()
                } else {
                    mudaEstadoDaTela(registrarDebitoCesta)
                    linearLayoutFinalizar.visibility = View.VISIBLE
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }
}
