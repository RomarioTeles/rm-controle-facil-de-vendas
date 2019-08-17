package br.com.rm.cfv.utils

import android.content.Context
import android.widget.Toast
import android.view.Gravity
import android.app.Activity
import android.view.View
import android.widget.TextView
import android.widget.ImageView
import androidx.constraintlayout.solver.GoalRow
import br.com.rm.cfv.R


class ToastUtils {

    companion object {
        fun showToastSuccess(context: Context, message : String){
            showToast(context, context.getString(R.string.toast_title_sucess), message, R.drawable.check_circle_outline)
        }

        fun showToastError(context: Context, message : String){
            showToast(context, context.getString(R.string.toast_title_error), message, R.drawable.close_circle_outline)
        }

        fun showToastAlert(context: Context, message : String){
            showToast(context, context.getString(R.string.toast_title_alert), message, R.drawable.alert_outline)
        }

        fun showToastConfirm(context: Context, message : String){
            val inflater = (context as Activity).getLayoutInflater()
            val layout = inflater.inflate(
                br.com.rm.cfv.R.layout.toast_layout_confirm,
                null
            )
            val image = layout.findViewById(R.id.image) as ImageView
            image.setImageResource(R.drawable.alert_circle_outline)
            val txtMessage = layout.findViewById(R.id.text) as TextView
            txtMessage.text = message
            val toast = Toast(context)
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            toast.duration = Toast.LENGTH_LONG
            toast.view = layout
            toast.show()
        }

        private fun showToast(context: Context,title : String, message : String, iconRes : Int){
            val inflater = (context as Activity).getLayoutInflater()
            val layout = inflater.inflate(
                br.com.rm.cfv.R.layout.toast_layout,
                null
            )
            val image = layout.findViewById(R.id.image) as ImageView
            image.setImageResource(iconRes)
            val txtMessage = layout.findViewById(R.id.text) as TextView
            txtMessage.text = message
            val txtTitle = layout.findViewById(R.id.title) as TextView
            txtTitle.text = title
            val toast = Toast(context)
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            toast.duration = Toast.LENGTH_LONG
            toast.view = layout
            toast.show()
        }
    }
}