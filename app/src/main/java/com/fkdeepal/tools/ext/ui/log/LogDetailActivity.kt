package com.fkdeepal.tools.ext.ui.log

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.FileProvider
import com.fkdeepal.tools.ext.adapter.LogFileListAdapter
import com.fkdeepal.tools.ext.base.BaseActivity
import com.fkdeepal.tools.ext.databinding.ActivityLogDetailBinding
import com.fkdeepal.tools.ext.databinding.ActivityLogFileListBinding
import com.fkdeepal.tools.ext.utils.FileUtils
import com.fkdeepal.tools.ext.utils.ShareUtils
import com.fkdeepal.tools.ext.widget.ObservableScrollView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Paths

class LogDetailActivity: BaseActivity<ActivityLogDetailBinding>() {
    private var path:String = ""
    private var bufferedReader: BufferedReader? = null
    private var isLoading = false
    private var currentLine = 0
    private var totalLines = 0
    private val linesPerLoad = 50 // 每次加载的行数
    private val jobScope = MainScope()
    // 用于跟踪阅读位置
    private var lastScrollY = 0
    private var autoScrolling = false

    companion object {



        fun startActivity(context: Context, path: String) {
            val intent = Intent(context, LogDetailActivity::class.java)
            intent.putExtra("path", path)
            context.startActivity(intent)

        }
    }

    override fun initViews() {
        mViewBinding.apply {
            setOnClickListener(btnShare)
            svContent.setOnScrollChangedListener(object : ObservableScrollView.OnScrollChangedListener {
                override fun onScrollBottom() {
                    if (!isLoading) {
                        loadMoreContent()
                    }
                }

                override fun onScrollTop() {
                    // 可以在这里实现向上加载历史内容
                }

                override fun onScrollChanged(scrollY: Int) {
                    lastScrollY = scrollY
                }
            })
             path = intent.getStringExtra("path")?:""
            if (path.isNullOrBlank()){
                finish()
                return
            }
            val file = File(path)
            if (file.isFile && file.exists()) {
                val fileName = file.nameWithoutExtension
                setTitle(fileName)

            } else {
                finish()
                return
            }
        }
        loadTxtFile()
    }


    override fun initViewBinding(layoutInflater: LayoutInflater): ActivityLogDetailBinding? {
        return ActivityLogDetailBinding.inflate(layoutInflater)
    }

    private fun loadTxtFile() {
        // 示例：从assets加载文件，您可以根据需要修改文件路径
        val filePath = path // 实现您自己的文件路径获取逻辑

        jobScope.launch(Dispatchers.IO) {
            try {
                val file = File(filePath)
                if (!file.exists()) {
                    withContext(Dispatchers.Main) {
                        mViewBinding.tvContent.text = "文件不存在: $filePath"
                    }
                    return@launch
                }

                // 计算总行数（用于进度显示）
                totalLines = countFileLines(file)

                // 创建BufferedReader
                bufferedReader = BufferedReader(FileReader(file))

                // 首次加载内容
                withContext(Dispatchers.Main) {
                    loadMoreContent()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mViewBinding.tvContent.text = "读取文件出错: ${e.message}"
                }
            }
        }
    }

    private fun loadMoreContent() {
        if (isLoading || bufferedReader == null) return

        isLoading = true

        jobScope.launch(Dispatchers.IO) {
            try {
                val contentBuilder = StringBuilder()
                var linesRead = 0

                bufferedReader?.let { reader ->
                    var line: String? = null
                    while (linesRead < linesPerLoad && reader.readLine().also { line = it } != null) {
                        line?.let {
                            contentBuilder.append(it).append("\n")
                            currentLine++
                            linesRead++
                        }
                    }
                }

                val newContent = contentBuilder.toString()

                withContext(Dispatchers.Main) {
                    if (newContent.isNotEmpty()) {
                        // 追加新内容
                        val currentText = mViewBinding.tvContent.text.toString()
                        mViewBinding.tvContent.text = if (currentText.isEmpty()) {
                            newContent
                        } else {
                            currentText + newContent
                        }

                        // 如果是首次加载后自动滚动，保持滚动位置
                        if (autoScrolling) {
                            mViewBinding.svContent.post {
                                mViewBinding.svContent.scrollTo(0, lastScrollY)
                            }
                        }
                        trimContentIfNeeded()
                    }

                    isLoading = false

                    // 检查是否已到文件末尾

                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mViewBinding.tvContent.append("\n读取错误: ${e.message}")
                    isLoading = false
                }
            }
        }
    }



    private fun countFileLines(file: File): Int {
        return try {
            BufferedReader(FileReader(file)).use { reader ->
                var lines = 0
                while (reader.readLine() != null) lines++
                lines
            }
        } catch (e: Exception) {
            0
        }
    }
    override fun onViewClick(v: View) {
        mViewBinding.apply {
            when(v){
                btnShare-> ShareUtils.shareFile(mActivity, File(path), "")
            }
        }
    }
    private fun trimContentIfNeeded() {
        if (currentLine > 1000) { // 超过1000行时清理前500行
            val lines = mViewBinding.tvContent.text.toString().lines()
            if (lines.size > 500) {
                mViewBinding.tvContent.text = lines.takeLast(500).joinToString("\n")
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        jobScope.cancel()

        // 关闭文件流
        try {
            bufferedReader?.close()
        } catch (e: Exception) {
        }
    }
}