package br.com.rm.cfv.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.balancete.ListaBalanceteActivity
import br.com.rm.cfv.activities.cliente.ListaClientesActivity
import br.com.rm.cfv.activities.configuracao.ConfiguracoesActivity
import br.com.rm.cfv.activities.contaPagarReceber.ListaContasPagarReceberActivity
import br.com.rm.cfv.activities.departamento.DepartamentoActivity
import br.com.rm.cfv.activities.estoque.ListaEstoqueActivity
import br.com.rm.cfv.activities.fornecedor.ListaFornecedorActivity
import br.com.rm.cfv.activities.produto.ListaProdutosActivity
import br.com.rm.cfv.constants.TipoReferencia
import br.com.rm.cfv.database.entities.DefaultReferencia
import br.com.rm.cfv.database.entities.IReferencia
import br.com.rm.cfv.utils.ToastUtils
import br.com.rm.cfv.utils.reports.CSVReportUtils
import br.com.rm.cfv.utils.reports.IReportable
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.File
import java.nio.file.Path

abstract class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    IReportable {

    override fun setContentView(layoutResID: Int) {

        val fullView = layoutInflater.inflate(R.layout.activity_base, null) as DrawerLayout
        val activityContainer = fullView.findViewById(R.id.contentActivity) as FrameLayout
        layoutInflater.inflate(layoutResID, activityContainer, true)

        super.setContentView(fullView)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer_layout!!.addDrawerListener(toggle)
        toggle.syncState()

        (nav_view as NavigationView).setNavigationItemSelectedListener(this)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        progress.visibility = View.GONE

        setHomeButtonSettings()
    }

    private fun setHomeButtonSettings(){
        supportActionBar!!.title = getToobarTitle()
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        try {
            val icon = getHomeIcon()
            supportActionBar!!.setHomeAsUpIndicator(icon)
        }catch (e: Exception){
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        if (drawer_layout!!.isDrawerOpen(GravityCompat.START)) {
            drawer_layout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if(getHomeIcon() == R.drawable.arrow_left){
                    onBackPressed()
                }else {
                    if (drawer_layout!!.isDrawerOpen(GravityCompat.START)) {
                        drawer_layout!!.closeDrawer(GravityCompat.START)
                    } else {
                        drawer_layout!!.openDrawer(GravityCompat.START)
                    }
                }
                return true
            }
            R.id.action_settings ->{
                startActivity(Intent(this, ConfiguracoesActivity::class.java))
            }
            R.id.report -> {
                val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                val file = File(storageDir, reportFileName)
                file.createNewFile()
                CSVReportUtils.writeCsvFromBean(file.toPath(), dataSet)
                shareReport(file.toPath())
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_main -> {
                startActivity(Intent(this, DashboardActivity::class.java))
            }
            R.id.nav_clientes -> {
                startActivity(Intent(this, ListaClientesActivity::class.java))
            }
            R.id.nav_produto -> {
                startActivity(Intent(this, ListaProdutosActivity::class.java))
            }
            R.id.nav_departamento ->{
                startActivity(Intent(this, DepartamentoActivity::class.java))
            }
            R.id.nav_estoque ->{
                startActivity(Intent(this, ListaEstoqueActivity::class.java))
            }
            R.id.nav_fornecedores->{
                startActivity(Intent(this, ListaFornecedorActivity::class.java))
            }
            R.id.nav_balancete->{
                startActivity(Intent(this, ListaBalanceteActivity::class.java))
            }
            R.id.nav_config ->{
                startActivity(Intent(this, ConfiguracoesActivity::class.java))
            }
            R.id.nav_charts ->{
                startActivity(Intent(this, ChartsActivity::class.java))
            }
            R.id.nav_receitas ->{
                val intent = Intent(this, ListaContasPagarReceberActivity::class.java)
                val referencia = getArgReferencia(TipoReferencia.RECEITAS)
                intent.putExtra(ListaContasPagarReceberActivity.ARG_REFERENCIA, referencia as DefaultReferencia)
                startActivity(intent)
            }
            R.id.nav_despesas ->{
                val intent = Intent(this, ListaContasPagarReceberActivity::class.java)
                val referencia = getArgReferencia(TipoReferencia.DESPESAS)
                intent.putExtra(ListaContasPagarReceberActivity.ARG_REFERENCIA, referencia as DefaultReferencia)
                startActivity(intent)
            }
        }

        drawer_layout!!.closeDrawer(GravityCompat.START)
        return true
    }

    fun getArgReferencia(tipoRef: String) : IReferencia{
        val nome = if (tipoRef == TipoReferencia.RECEITAS) getString(R.string.menu_receitas) else getString(R.string.menu_despesas)
        return DefaultReferencia(-1, nome, tipoRef)
    }

    fun fab() : FloatingActionButton {
        return fab as FloatingActionButton
    }

    fun hideFab(){
        fab().hide()
    }

    fun showFab(){
        fab().show()
    }

    fun showProgress(){
        progress.visibility = View.VISIBLE
    }

    fun showProgress(text : String){
        textViewProgress.text = text
        showProgress()
    }

    fun hideProgress(){
        progress.visibility = View.GONE
    }

    fun getCfvApplication() : CfvApplication{
        return application as CfvApplication
    }

    abstract fun getToobarTitle(): String

    fun hideFabOnScroll(view : View){
       hideFabOnScroll(view, fab)
    }

    fun hideFabOnScroll(view : View, fab : FloatingActionButton){

        if(view is RecyclerView){
            view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0)
                        fab.hide()
                    else if (dy < 0)
                        fab.show()
                }
            })
        }else {
            view.viewTreeObserver.addOnScrollChangedListener {
                if (view.scrollY > 0)
                    fab.hide()
                else if (view.scrollY < 50)
                    fab.show()
            }
        }
    }

    fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    public fun shareReport(filelocation: Path) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/*"
            val fileuri = FileProvider.getUriForFile(this, "$packageName.fileprovider", filelocation.toFile())
            intent.putExtra(Intent.EXTRA_STREAM, fileuri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val shareIntent = Intent.createChooser(intent, null)
            startActivity(shareIntent)
        } catch (e: Exception) {
            ToastUtils.showToastError(this, "Ocorreu um erro ao tentar compartilhar o arquivo.")
            Log.e("Send Failed!", e.message, e)
        }

    }

    open fun getHomeIcon(): Int{
        return R.drawable.arrow_left
    }
}
