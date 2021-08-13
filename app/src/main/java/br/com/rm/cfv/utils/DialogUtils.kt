package br.com.rm.cfv.utils

import android.app.AlertDialog
import android.content.Context
import br.com.rm.cfv.R


class DialogUtils {

    companion object {

        fun showDialog(context : Context, title : Int, text : Int?, config: DialogConfig){
            val sTitle = context.getString(title)
            val sMessage = if(text == null || text == 0)  "" else context.getString(text)
            showDialog(context, sTitle, sMessage, config )
        }

        fun showDialog(context : Context, title : String, text : String?, config: DialogConfig){
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle(title)
            if(config.showSubtitle){
                dialog.setMessage(text!!)
            }

            dialog.setPositiveButton(R.string.ok){
                interfaceDialog, i ->
                    if(config.positiveButtonListener != null) {
                        config.positiveButtonListener!!.run()
                    }
                    interfaceDialog.dismiss()
            }

            if(config.showNegativeButton){
                dialog.setNegativeButton(R.string.cancelar){
                    interfaceDialog, i ->
                    if(config.negativeButtonListener == null){
                        config.negativeButtonListener!!.run()
                    }
                    interfaceDialog.dismiss()
                }
            }

            dialog.show()
        }
    }
}