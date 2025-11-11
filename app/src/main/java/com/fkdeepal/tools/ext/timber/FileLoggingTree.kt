package com.fkdeepal.tools.ext.timber

import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class  FileLoggingTree(val file: File): Timber.Tree(){

    private val mFileWriter by lazy { FileWriter(file,true) }

    override fun i(message: String?, vararg args: Any?) {
        super.i(message, *args)
    }



    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority > Log.DEBUG){
            runCatching {
                mFileWriter.write(String.format("[%s] %s: %s\n", getCurrentTimestamp(),tag, message))
                if (t!=null){
                    mFileWriter.write(Log.getStackTraceString(t));
                    mFileWriter.flush()
                }
            }.onFailure {
                Log.e("FileLoggingTree",it.localizedMessage,it)
            }
        }

    }
    fun getCurrentTimestamp(): String{
        return  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date());
    }
}