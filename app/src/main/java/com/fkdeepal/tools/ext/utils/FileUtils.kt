package com.fkdeepal.tools.ext.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

object FileUtils {
    private fun checkAndCreateDir(dir: File){
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }
    private fun checkAndCreateFile(file: File){
        if (!file.exists()) {
            file.createNewFile()
        }
    }
    fun getLogCacheDir(context: Context): File{
        val cacheDir = context.cacheDir
        val dir = File(cacheDir, "log")
        checkAndCreateDir(dir)
        return dir
    }
    fun getLogCacheFile(context: Context): File{
        val timeStr = "${SimpleDateFormat("yyyyMMdd", Locale.US).format(System.currentTimeMillis())}"
        val fileName = "${timeStr}.log"
        val cacheFile = File(getLogCacheDir(context), fileName)
        checkAndCreateFile(cacheFile)
        return cacheFile
    }
}