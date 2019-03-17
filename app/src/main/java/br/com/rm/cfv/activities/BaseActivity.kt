package br.com.rm.cfv.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import br.com.rm.cfv.CfvApplication
import br.com.rm.cfv.activities.cliente.ListaClientesActivity
import br.com.rm.cfv.activities.departamento.DepartamentoActivity
import br.com.rm.cfv.activities.estoque.ListaEstoqueActivity
import br.com.rm.cfv.activities.produto.ListaProdutosActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import br.com.rm.cfv.R
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.app_bar_main.*


abstract class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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
        setSupportActionBar(toolbar as Toolbar?)

        supportActionBar!!.setTitle(getToobarTitle())
        supportActionBar!!.setHomeButtonEnabled(true)
        //supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        progress.visibility = View.GONE
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
                if (drawer_layout!!.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout!!.closeDrawer(GravityCompat.START)
                }else{
                    drawer_layout!!.openDrawer(GravityCompat.START)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_main -> {
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
        }

        drawer_layout!!.closeDrawer(GravityCompat.START)
        return true
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
}
