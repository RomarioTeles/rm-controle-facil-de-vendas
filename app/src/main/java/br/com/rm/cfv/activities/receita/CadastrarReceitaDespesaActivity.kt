package br.com.rm.cfv.activities.receita

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import br.com.rm.cfv.activities.cliente.ListaClientesActivity
import br.com.rm.cfv.activities.contaPagarReceber.ListaContasPagarReceberActivity
import br.com.rm.cfv.activities.contaPagarReceber.compra_venda_produtos.RegistrarCompraVendaActivity
import br.com.rm.cfv.activities.fornecedor.ListaFornecedorActivity
import br.com.rm.cfv.adapters.MeioPagamentoAdapter
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate
import br.com.rm.cfv.asyncTasks.contaPagarReceber.InsertContaPagarReceberAsyncTask
import br.com.rm.cfv.constants.MeioPagamento
import br.com.rm.cfv.constants.TipoPagamento
import br.com.rm.cfv.constants.TipoReferencia
import br.com.rm.cfv.database.entities.Cliente
import br.com.rm.cfv.database.entities.ContaPagarReceber
import br.com.rm.cfv.database.entities.Fornecedor
import br.com.rm.cfv.database.entities.IReferencia
import br.com.rm.cfv.utils.EditTextMaskUtil
import br.com.rm.cfv.utils.ToastUtils
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_cadastrar_receita.*
import java.text.SimpleDateFormat
import java.util.*

class CadastrarReceitaDespesaActivity : BaseActivity(), IPostExecuteInsertAndUpdate {

    override fun getToobarTitle(): String {
        return if (title == TipoReferencia.RECEITAS.name) getString(R.string.cadastrar_receita) else getString(R.string.cadastrar_despesa)
    }

    var contaPagarReceber: ContaPagarReceber = ContaPagarReceber()

    lateinit var meioPagamentoAdapter: MeioPagamentoAdapter

    var referencia: IReferencia? = null

    var title = ""

    companion object{
        var ARG_TIPO_REF = "tipoRef"
        var ARG_CONTA_PAGAR_RECEBER = "conta pagar receber"
        var REQUEST_SELECT_REF = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_cadastrar_receita)

        if(intent!!.hasExtra(ARG_TIPO_REF)){
            contaPagarReceber.tipoRef = intent.extras!!.getString(ARG_TIPO_REF)
            contaPagarReceber.idRef = -1
            contaPagarReceber.nomeRef = getString(R.string.empty_nome_ref, contaPagarReceber.tipoRef)
            title = contaPagarReceber.tipoRef!!
            if(title in TipoReferencia.getListDespesas()){
                textInputLayoutJuros.visibility  = View.GONE
            }

        }else if(intent!!.hasExtra(ARG_CONTA_PAGAR_RECEBER)){
            contaPagarReceber = intent.extras!!.getParcelable(ARG_CONTA_PAGAR_RECEBER)!!
        }

        supportActionBar!!.title = getToobarTitle()

        meioPagamentoAdapter = MeioPagamentoAdapter(this, true)

        listViewMeioPagamento.adapter = meioPagamentoAdapter

        prepareView()

        setEventsClick()

        fab().setImageResource(R.drawable.content_save_black_24dp)
    }

    fun setEventsClick(){

        textInputEditDataVencimento.addTextChangedListener(EditTextMaskUtil.insert(textInputEditDataVencimento, EditTextMaskUtil.MASK_DATE))

        radioAvista.isChecked = true

        radioTipoPag.setOnCheckedChangeListener { buttonView, isChecked ->
            if (radioAPrazo.isChecked) {
                contaPagarReceber.tipoPagamento = TipoPagamento.A_PRAZO
                contaPagarReceber.meioPagamento = MeioPagamento.PARCELAMENTO_DA_LOJA.name
                textInputEditQtdeParcelas.isEnabled = true
                textInputEditDataVencimento.isEnabled = true
                textInputEditJuros.isEnabled = true
                textInputEditQtdeParcelas.requestFocus()
            } else {
                textInputEditQtdeParcelas.isEnabled = false
                textInputEditDataVencimento.isEnabled = false
                textInputEditJuros.isEnabled = false

                contaPagarReceber.tipoPagamento = TipoPagamento.A_VISTA
                contaPagarReceber.meioPagamento = MeioPagamento.DINHEIRO.name
                contaPagarReceber.dataPrevistaPagamento = Date().time
                contaPagarReceber.percentualJurosParcelas = 0.0
                contaPagarReceber.qtdeParcelas = 1
            }

            meioPagamentoAdapter.setTipoPagamento(contaPagarReceber.tipoPagamento)
        }

        selecionarRef.setOnClickListener { v -> callSelectRefActivity() }

        fab().setOnClickListener {
            prepareSave()
        }

        hideFabOnScroll(scrollView)
    }

    fun prepareView(){

        textInputEditDescricao.setText(contaPagarReceber.descricao)
        textInputEditTotal.setText(contaPagarReceber.total.toString())
        textInputEditQtdeParcelas.setText(contaPagarReceber.qtdeParcelas.toString())
        if(contaPagarReceber.dataPrevistaPagamento != null) {
            val dataVenc = Date(contaPagarReceber.dataPrevistaPagamento!!)
            textInputEditDataVencimento.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dataVenc))
        }
        textInputEditJuros.setText(contaPagarReceber.percentualJurosParcelas.toString())
        if(contaPagarReceber.meioPagamento.isNotBlank()){
            meioPagamentoAdapter.setTipoPagamento(contaPagarReceber.tipoPagamento)
            val meioPagamento = MeioPagamento.valueOf(contaPagarReceber.meioPagamento)
            val idx = meioPagamentoAdapter.getItems().indexOf(meioPagamento)
            listViewMeioPagamento.setSelection(idx)
            meioPagamentoAdapter.notifyDataSetChanged()
        }
    }

    fun prepareSave(){

        textInputLayoutDescricao.error = ""
        textInputLayoutTotal.error = ""
        textInputLayoutQtdeParcelas.error = ""
        textInputLayoutDataVencimento.error = ""
        textInputLayoutJuros.error = ""

        if(validaTodosOsCampos()) {
            //listViewMeioPagamento
            //
            val descricao = textInputEditDescricao.text.toString()
            val total = textInputEditTotal.text.toString()
            val qtdeParcelas = textInputEditQtdeParcelas.text.toString()
            val dataVencimento = textInputEditDataVencimento.text.toString()
            val percentualJuros = textInputEditJuros.text.toString()
            var meioPagamento : Int = listViewMeioPagamento.selectedItemId.toInt()

            contaPagarReceber.meioPagamento = MeioPagamento.values()[meioPagamento].name
            contaPagarReceber.descricao = descricao
            contaPagarReceber.total = total.toDouble()
            contaPagarReceber.qtdeParcelas = qtdeParcelas.toInt()
            contaPagarReceber.percentualJurosParcelas = percentualJuros.toDouble()
            val data = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dataVencimento)
            contaPagarReceber.dataPrevistaPagamento = data.time

            InsertContaPagarReceberAsyncTask(CfvApplication.database, this).execute(contaPagarReceber)
        }
    }

    fun validaCampoNumerico(editText: TextInputEditText, textInputLayout: TextInputLayout?=null): Boolean{

        if(editText.text.toString().trim().isBlank()){
            if( textInputLayout != null) {
                textInputLayout.error = getString(R.string.mensagem_campo_requerido)
            }
            return false
        }else if (editText.text.toString().toDouble().compareTo(0.0) == 0){
            textInputLayout!!.error = getString(R.string.mensagem_campo_maior_zero)
            return false
        }

        return true
    }

    private fun validaCampoEmBranco(editText: TextInputEditText, textInputLayout: TextInputLayout?=null): Boolean{
        if(editText.text.toString().trim().isBlank()){
            if( textInputLayout != null) {
                textInputLayout.error = getString(R.string.mensagem_campo_requerido)
            }
            return false
        }
        return true
    }

    private fun validaTodosOsCampos() : Boolean{
        var isValid = true

        isValid = isValid && validaCampoEmBranco(textInputEditDescricao, textInputLayoutDescricao)
        isValid = isValid && validaCampoNumerico(textInputEditTotal, textInputLayoutTotal)
        isValid = isValid && validaCampoNumerico(textInputEditQtdeParcelas, textInputLayoutQtdeParcelas)
        isValid = isValid && validaCampoEmBranco(textInputEditDataVencimento, textInputLayoutDataVencimento)
        isValid = isValid && validaCampoEmBranco(textInputEditJuros, textInputLayoutJuros)

        try {
            val dataVencimento = textInputEditDataVencimento.text.toString()
            val data = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dataVencimento)
            /*val today = DateOperationsUtils.removeTimeFromDate(Date())
            if (data.before(today)){
                textInputLayoutDataVencimento.error = getString(R.string.data_retroativa)
                isValid = false
            }*/
        } catch (e: Exception) {
            ToastUtils.showToastError(this, getString(R.string.data_invalida))
            isValid = false
        }

        return isValid
    }

    fun callSelectRefActivity(){

        AlertDialog.Builder(this)
        .setTitle(getString(R.string.escolha_uma_opcao))
        .setItems(arrayOf(
            getString(R.string.label_cliente),
            getString(R.string.label_fornecedor),
            getString(R.string.cancelar))
        ) { dialog, which ->
            when (which) {
                0 -> {
                    val intent = Intent(this, ListaClientesActivity::class.java)
                    intent.putExtra("SELECTABLE", true)
                    startActivityForResult(intent, REQUEST_SELECT_REF)
                }
                1 -> {
                    val intent = Intent(this, ListaFornecedorActivity::class.java)
                    intent.putExtra("SELECTABLE", true)
                    startActivityForResult(intent, REQUEST_SELECT_REF)
                }
                else -> Log.i("Escolher Ref", "Default Selected")
            }
            dialog.dismiss()
        }.show()

    }

    fun postSelectReferencia(ref: IReferencia?){
        if(ref != null){
            referencia = ref
            contaPagarReceber.nomeRef = ref.getNomeRef()
            contaPagarReceber.idRef = ref.getIdRef()

            textInputEditNomeRef.setText(ref.getNomeRef())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_SELECT_REF && resultCode == Activity.RESULT_OK){
            if(data != null && data.hasExtra("result")){
                postSelectReferencia(data.extras!!.get("result") as IReferencia)
            }
        }
    }


    override fun afterInsert(result: Any?) {
        if (result == null) {
            Toast.makeText(this, getString(R.string.mensagem_erro), Toast.LENGTH_LONG).show()
        } else {
            var conta = result as ContaPagarReceber?
            if (conta!!.uid != null) {
                if(referencia == null){
                    this.finish()
                }else if(referencia!!.getTipoRef() == TipoReferencia.CLIENTE.name){
                    val intent = Intent(this, ListaContasPagarReceberActivity::class.java)
                    intent.putExtra(RegistrarCompraVendaActivity.ARG_REFERENCIA, referencia as Cliente)
                    startActivity(intent)
                    this.finish()
                }else if(referencia!!.getTipoRef() == TipoReferencia.FORNECEDOR.name){
                    val intent = Intent(this, ListaContasPagarReceberActivity::class.java)
                    intent.putExtra(RegistrarCompraVendaActivity.ARG_REFERENCIA, referencia as Fornecedor)
                    startActivity(intent)
                    this.finish()
                }
            } else {
                Toast.makeText(this, getString(R.string.mensagem_erro), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun afterUpdate(result: Any?) {

    }

}
