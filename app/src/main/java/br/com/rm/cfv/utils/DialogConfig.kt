package br.com.rm.cfv.utils

import android.view.View

data class DialogConfig(
    var showNegativeButton : Boolean = false,
    var showSubtitle : Boolean = false,
    var positiveButtonListener : Runnable?,
    var negativeButtonListener : Runnable?
)