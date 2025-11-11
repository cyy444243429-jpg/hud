package com.fkdeepal.tools.ext.ui.log

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.fkdeepal.tools.ext.adapter.LogFileListAdapter
import com.fkdeepal.tools.ext.base.BaseActivity
import com.fkdeepal.tools.ext.databinding.ActivityLogFileListBinding
import com.fkdeepal.tools.ext.utils.FileUtils
import com.fkdeepal.tools.ext.utils.ShareUtils
import java.io.File

class LogFileListActivity : BaseActivity<ActivityLogFileListBinding>() {
    private val mData = arrayListOf<File>()
    private val mAdapter by lazy {
        LogFileListAdapter(mData, { file, i ->
            LogDetailActivity.startActivity(mActivity, file.path)
        },
                           { file, i ->
                               AlertDialog.Builder(mActivity)
                                   .setTitle("删除")
                                   .setMessage("确认删除吗？")
                                   .setNegativeButton("取消", { d, w ->
                                       d.dismiss()
                                   })
                                   .setPositiveButton("删除", { d, w ->
                                       d.dismiss()
                                       runCatching {
                                           file.deleteRecursively()
                                       }
                                       getData()

                                   })
                                   .setNeutralButton("分享", { d, w ->
                                       d.dismiss()
                                       runCatching {
                                       ShareUtils.shareFile(mActivity,file,"")
                                       }
                                       getData()

                                   })
                                   .show()
                           })
    }
    private val mCacheFileDir by lazy {
        FileUtils.getLogCacheDir(mActivity)
    }

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, LogFileListActivity::class.java)
            context.startActivity(intent)

        }
    }

    override fun initViews() {
        getData()
        mViewBinding.apply {
            rvContent.adapter = mAdapter
        }
    }

    fun getData() {
        mData.clear()
        val files = arrayListOf<File>()
        val listFile = mCacheFileDir.listFiles()
            ?.filter { it.isFile }
        if (!listFile.isNullOrEmpty()) {
            files.addAll(listFile)
        }

        files.sortedByDescending { it.lastModified() }
        mData.addAll(files)
        mAdapter.notifyDataSetChanged()
    }

    override fun initViewBinding(layoutInflater: LayoutInflater): ActivityLogFileListBinding? {
        return ActivityLogFileListBinding.inflate(layoutInflater)
    }

    override fun onViewClick(v: View) {
    }
}