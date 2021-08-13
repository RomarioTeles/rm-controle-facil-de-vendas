package br.com.rm.cfv.utils

import android.content.Context
import android.widget.Toast
import android.view.Gravity
import br.com.rm.cfv.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ToastUtils {

    companion object {
        @JvmStatic
        fun showToastError(context: Context, message : String){
            DialogUtils.showDialog(context, context.getString(R.string.toast_title_error), message, DialogConfig().apply { showSubtitle = true })
        }
        @JvmStatic
        fun showToast(context: Context, message : String){
            CoroutineScope(Dispatchers.Main).launch {
                val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM, 0, 0)
                toast.show()
            }
        }
    }
}