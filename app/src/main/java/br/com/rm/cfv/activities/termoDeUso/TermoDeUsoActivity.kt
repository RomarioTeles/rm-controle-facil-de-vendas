package br.com.rm.cfv.activities.termoDeUso

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.com.rm.cfv.R
import br.com.rm.cfv.activities.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.activity_termo_de_uso.*

class TermoDeUsoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termo_de_uso)

        constraintLayoutTermoUso.visibility = View.GONE

        val aceitouTermoUso = getSharedPreferences("TERMO_DE_USO", MODE_PRIVATE).getBoolean("aceitou_termo_de_uso", false)

        if(aceitouTermoUso){
            startActivity(Intent(this@TermoDeUsoActivity, DashboardActivity::class.java))
            finish()
        }else{

            pdfView.fromAsset("termo-de-uso.pdf").load()

            constraintLayoutTermoUso.visibility = View.VISIBLE
            progress.visibility = View.GONE
        }

        buttonAceitarTermosUso.setOnClickListener {
            getSharedPreferences("TERMO_DE_USO", MODE_PRIVATE).edit().putBoolean("aceitou_termo_de_uso", true).apply()
            startActivity(Intent(this@TermoDeUsoActivity, DashboardActivity::class.java))
            finish()
        }

        buttonSair.setOnClickListener {
            getSharedPreferences("TERMO_DE_USO", MODE_PRIVATE).edit().putBoolean("aceitou_termo_de_uso", false).apply()
            finish()
        }

    }
}