package br.com.rm.cfv.activities.cliente.debito

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
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
import br.com.rm.cfv.constants.TipoPagamento
import br.com.rm.cfv.database.entities.Cliente
import br.com.rm.cfv.database.entities.DebitoCliente
import br.com.rm.cfv.database.entities.ItemProduto
import br.com.rm.cfv.database.entities.Produto
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
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.HashMap

class RegistrarDebitoActivity : ImageUtilsActivity(), IPostExecuteSearch, IOnClickProdutoListener,
    IPostExecuteInsertAndUpdate {

    override fun afterInsert(result: Any?) {
        if (result == null) {
            Toast.makeText(this, "Erro ao registrar o débito cliente!", Toast.LENGTH_LONG).show()
        } else {
            var cliente = result as DebitoCliente?
            if (cliente!!.uid != null) {
                mudaEstadoDaTela(registrarDebitoConcluido)
            } else {
                Toast.makeText(this, "Erro ao registrar o débito cliente!", Toast.LENGTH_LONG)
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
    private var debitoCliente: DebitoCliente = DebitoCliente()
    private lateinit var cliente: Cliente
    private lateinit var adapterParcelas: ArrayAdapter<String>
    private lateinit var pagWizardViewVoltar: View
    private lateinit var pagWizardViewAvancar: View
    private lateinit var pagWizardViewCurrent: View
    private var mapWizardControls = HashMap<Int, Array<View>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_registrar_debito)

        cliente = intent.getSerializableExtra("cliente") as Cliente

        debitoCliente.clienteId = cliente.uid
        debitoCliente.clienteNome = cliente.nome

        adicionaAcoesComponentes()
    }

    private fun adicionaAcoesComponentes() {
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
            registrarDebitoPagamentoMeio.id,
            arrayOf(registrarDebitoPagamentoTipo, registrarDebitoConcluido)
        )
        mapWizardControls.put(
            registrarDebitoPagamentoTipo.id,
            arrayOf(registrarDebitoCesta, registrarDebitoPagamentoParcelas)
        )
        mapWizardControls.put(
            registrarDebitoPagamentoParcelas.id,
            arrayOf(registrarDebitoPagamentoTipo, registrarDebitoPagamentoVencimento)
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
            if (debitoCliente.itemProdutoList.isEmpty()) {
                Toast.makeText(this, getString(R.string.mensagem_cesta_vazia), Toast.LENGTH_LONG)
                    .show();
            } else {
                mudaEstadoDaTela(registrarDebitoCesta)
                linearLayoutFinalizar.visibility = View.VISIBLE
            }
        }

        linearLayoutFinalizar.setOnClickListener {
            debitoCliente.atualizaTotal()
            textViewPagandoSubtotal.text =
                "R$ ${DecimalFormatUtils.decimalFormatPtBR(debitoCliente.total)}"
            mudaEstadoDaTela(registrarDebitoPagamentoTipo)
        }

        buttonCancelarProduto.setOnClickListener {
            limpaItemProduto()
            mudaEstadoDaTela(registrarDebitoProdutos)
        }

        buttonAdicionarProduto.setOnClickListener {
            if (!debitoCliente.itemProdutoList.contains(itemProdutoEmEdicao)) {
                debitoCliente.itemProdutoList.add(itemProdutoEmEdicao)
                itemProdutoAdapter.setDataset(debitoCliente.itemProdutoList)
            }
            atualizaResumoCesta()
            limpaItemProduto()
            itemProdutoAdapter.notifyDataSetChanged()
            mudaEstadoDaTela(registrarDebitoProdutos)
        }

        imageViewItemRemover.setOnClickListener {
            if (debitoCliente.itemProdutoList.contains(itemProdutoEmEdicao)) {
                debitoCliente.itemProdutoList.remove(itemProdutoEmEdicao)
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
            linearLayoutVerCesta.visibility = View.VISIBLE
        }

        listViewMeioPagamento.setOnItemClickListener { parent, view, position, id ->
            debitoCliente.meioPagamento = MeioPagamento.values().get(position).name
            meioPagamentoAdapter.selected = MeioPagamento.values().get(position)
            meioPagamentoAdapter.notifyDataSetChanged()
        }

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val date = GregorianCalendar()
            date.set(year, month, dayOfMonth, 0, 0, 0)
            debitoCliente.dataPrevistaPagamento = date.time.time
        }

        buttonPagAvista.setOnClickListener {
            debitoCliente.tipoPagamento = TipoPagamento.A_VISTA
            buttonPagPrazo.setBackgroundColor(resources.getColor(R.color.accent))
            buttonPagAvista.setBackgroundColor(resources.getColor(R.color.accent_active))
        }

        buttonPagPrazo.setOnClickListener {
            debitoCliente.tipoPagamento = TipoPagamento.A_PRAZO
            buttonPagPrazo.setBackgroundColor(resources.getColor(R.color.accent_active))
            buttonPagAvista.setBackgroundColor(resources.getColor(R.color.accent))
            initListaDeParcelas()
        }

        buttonAvancar.setOnClickListener {
            if (pagWizardViewCurrent == registrarDebitoPagamentoTipo) {
                if (debitoCliente.tipoPagamento == TipoPagamento.A_VISTA) {
                    mudaEstadoDaTela(registrarDebitoPagamentoMeio)
                } else {
                    mudaEstadoDaTela(registrarDebitoPagamentoParcelas)
                }
            } else if (pagWizardViewCurrent == registrarDebitoPagamentoVencimento || pagWizardViewCurrent == registrarDebitoPagamentoMeio) {
                registrarPedido()
            } else {
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
    }

    private fun registrarPedido() {
        var task = InsertDebitoClienteAsyncTask(getCfvApplication().getDataBase(), this)
        debitoCliente.itemProdutoList = itemProdutoAdapter.getListaProdutos().toMutableList()
        debitoCliente.atualizaTotal()
        task.execute(debitoCliente)
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
        textViewquantidadeItens.text = debitoCliente.itemProdutoList.size.toString()
        textViewSubtotalItens.text =
            DecimalFormatUtils.decimalFormatPtBR(debitoCliente.getSubtotal())
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
            linearLayoutVerCesta.visibility = View.VISIBLE
        }
    }

    private fun pesquisarProdutos(search: String) {
        SelectAllProdutosAsyncTask(getCfvApplication().getDataBase()!!.produtoDAO(), this)
            .execute(search)
    }

    @SuppressLint("SetTextI18n")
    override fun onProdutoClick(produto: Produto, isLongClick: Boolean) {
        itemProdutoEmEdicao = ItemProduto()

        itemProdutoEmEdicao.codigoProduto = produto.codigo
        itemProdutoEmEdicao.nomeProduto = produto.nome
        itemProdutoEmEdicao.precoUnitario = produto.precoVenda!!
        itemProdutoEmEdicao.subtotal = produto.precoVenda!!

        if (debitoCliente.itemProdutoList.contains(itemProdutoEmEdicao)) {
            itemProdutoEmEdicao =
                debitoCliente.itemProdutoList.find { it.codigoProduto == itemProdutoEmEdicao.codigoProduto }!!
            itemProdutoEmEdicao.setQuantidade(itemProdutoEmEdicao.getQuantidade() + 1)
        }

        textInputQuantidade.setText(itemProdutoEmEdicao.getQuantidade().toString())
        textViewItemNomeProduto.text = itemProdutoEmEdicao.nomeProduto.toString()
        atualizaSubtotalItemEmEdicao(itemProdutoEmEdicao.subtotal)
        if (produto.caminhoImagem != null && !produto.caminhoImagem!!.isBlank()) {
            imageViewItemProduto.setImageBitmap(
                getBitmapFromAbsolutePath(
                    produto.caminhoImagem,
                    false
                )
            )
        }

        if (isLongClick) {
            mudaEstadoDaTela(registrarDebitoAdicionarProduto)
        } else {
            buttonAdicionarProduto.performClick()
        }
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

    fun initListaDeParcelas() {
        adapterParcelas = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        val map = mutableListOf<String>()
        val qtdeParcelas = 12
        val percentualJurosParcelas = BigDecimal(10.0)
        val juros = percentualJurosParcelas.plus(BigDecimal(100)).divide(BigDecimal(100))
        val total = BigDecimal(debitoCliente.total).multiply(juros)
        for (i in 1..qtdeParcelas) {
            if (i < 5) {
                val valorSemJuros: Double = debitoCliente.total / i
                val chave = "$i x R$ ${DecimalFormatUtils.decimalFormatPtBR(valorSemJuros)}"
                map.add(chave)
            } else {
                val valorComJuros = total.divide(BigDecimal(i), 2, RoundingMode.CEILING)
                val chave = "$i x R$ ${valorComJuros}"
                map.add(chave)
            }
        }

        adapterParcelas.addAll(map)
        listViewParcelamento.adapter = adapterParcelas
        listViewParcelamento.setOnItemClickListener { parent, view, position, id ->
            debitoCliente.qtdeParcelas = position + 1
            debitoCliente.percentualJurosParcelas =
                if (debitoCliente.qtdeParcelas < 5) 0.0 else 10.0
        }
        adapterParcelas.notifyDataSetChanged()
    }
}
