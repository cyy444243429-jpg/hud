package com.fkdeepal.tools.ext.exts

import android.content.Context
import android.widget.Toast

fun CharSequence?.safe(msg: CharSequence = ""):CharSequence{
    return  this?:""
}
fun String?.safe(msg: String = ""):String{
    return  this?:""
}