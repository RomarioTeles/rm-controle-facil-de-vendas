package br.com.rm.cfv.activities.configuracao

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_base.*

class ConfiguracoesActivity : BaseActivity(){

    override fun getToobarTitle(): String {
       return getString(R.string.configuracoes)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fab().hide()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    override fun onResume() {
        super.onResume()
        nav_view.setCheckedItem(R.id.nav_config)
    }

    override fun getHomeIcon() : Int{
        return R.drawable.ic_menu
    }
}