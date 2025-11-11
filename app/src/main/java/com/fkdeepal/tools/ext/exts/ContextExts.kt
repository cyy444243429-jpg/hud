package com.fkdeepal.tools.ext.exts

import android.content.Context
import android.widget.Toast

fun Context.toast(msg: CharSequence,isLong: Boolean  = false){
    Toast.makeText(this,msg,if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}