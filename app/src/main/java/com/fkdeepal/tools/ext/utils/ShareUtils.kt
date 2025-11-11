package com.fkdeepal.tools.ext.utils

import android.app.Activity
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object ShareUtils {
    fun shareFile(mActivity: Activity, file: File,shareTitle: String?){
        val shareIntent = Intent()
        shareIntent.setAction(Intent.ACTION_SEND)
        val uri = FileProvider.getUriForFile(mActivity, mActivity.packageName + ".fileprovider", file)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                     Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // 指定发送内容的类型 (MIME type)
        shareIntent.setType("text/plain");
        mActivity.startActivity(Intent.createChooser(shareIntent, shareTitle));
    }
}