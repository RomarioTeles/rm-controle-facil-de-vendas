package br.com.rm.cfv.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import br.com.rm.cfv.R


class DialogUtils {

    companion object {
        fun showDialogSuccess(context : Context, title : Int, text : Int, config: DialogConfig){
            showDialog(context, title, text, R.layout.dialog_layout_success, config)
        }

        fun showDialogError(context : Context, title : Int, text : Int, config: DialogConfig){
            showDialog(context, title, text, R.layout.dialog_layout_error, config)
        }

        fun showDialogAlert(context : Context, title : Int, text : Int, config: DialogConfig){
            showDialog(context, title, text, R.layout.dialog_layout_alert, config)
        }

        fun showDialogConfirm(context : Context, title : Int, text : Int, config: DialogConfig){
            showDialog(context, title, text, R.layout.dialog_layout_confirm, config)
        }

        private fun showDialog(context : Context, title : Int, text : Int?, layout : Int, config: DialogConfig){
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(layout)

            val txtTitle = dialog.findViewById(R.id.title) as TextView
            txtTitle.setText(title)

            val txtText = dialog.findViewById(R.id.text) as TextView
            if(config.showSubtitle){
                txtText.setText(text!!)
            }else{
                txtText.visibility = View.GONE
            }

            val positiveButton = dialog.findViewById(R.id.button_positive) as Button
            positiveButton.setOnClickListener{
                if(config.positiveButtonListener != null) {
                    config.positiveButtonListener!!.run()
                }
                dialog.dismiss()
            }

            val negativeButton = dialog.findViewById(R.id.button_negative) as Button

            if(config.showNegativeButton){
                negativeButton.setOnClickListener{
                    if(config.negativeButtonListener == null){
                        config.negativeButtonListener!!.run()
                    }
                    dialog.dismiss()
                }
            }else{
                negativeButton.visibility = View.GONE
            }

            dialog.show()
        }
    }
}