package com.fkdeepal.tools.ext.utils

import android.content.Context
import android.graphics.Picture
import android.graphics.drawable.PictureDrawable
import com.caverock.androidsvg.SVG
import timber.log.Timber
import java.io.InputStream

object SvgLoader {
    private const val TAG = "SvgLoader"
    private val cache = mutableMapOf<String, PictureDrawable>()
    private val colorManager by lazy { ColorPreferenceManager.getInstance(AppUtils.appContext) }
    
    /**
     * 从 res/drawable 加载 SVG 文件
     */
    fun loadSvgFromResources(context: Context, resourceName: String): PictureDrawable? {
        // 检查缓存
        cache[resourceName]?.let {
            Timber.tag(TAG).d("从缓存加载 SVG: $resourceName")
            return it
        }
        
        var inputStream: InputStream? = null
        return try {
            Timber.tag(TAG).d("开始加载 SVG: $resourceName")
            
            // 从 res/drawable 读取 SVG 文件
            val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
            if (resourceId == 0) {
                Timber.tag(TAG).e("找不到资源: $resourceName")
                return null
            }
            
            inputStream = context.resources.openRawResource(resourceId)
            
            // 替换颜色引用
            val svgContent = replaceColorReferences(inputStream)
            val svg = SVG.getFromString(svgContent)
            
            // 只设置必要的属性
            svg.setDocumentWidth("100%")
            svg.setDocumentHeight("100%")
            
            // 渲染为 Picture
            val picture = svg.renderToPicture()
            
            // 检查渲染结果
            if (picture.width <= 0 || picture.height <= 0) {
                Timber.tag(TAG).w("SVG 渲染尺寸异常: ${picture.width}x${picture.height}")
                return null
            }
            
            val drawable = PictureDrawable(picture)
            
            // 加入缓存
            cache[resourceName] = drawable
            
            Timber.tag(TAG).d("成功加载 SVG: $resourceName, 尺寸: ${picture.width}x${picture.height}")
            drawable
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "加载 SVG 失败: $resourceName - ${e.message}")
            null
        } finally {
            // 确保流被关闭
            try {
                inputStream?.close()
            } catch (e: Exception) {
                Timber.tag(TAG).w("关闭输入流失败: ${e.message}")
            }
        }
    }
    
    /**
     * 替换 SVG 中的颜色引用
     */
    private fun replaceColorReferences(inputStream: InputStream): String {
        val svgContent = inputStream.bufferedReader().use { it.readText() }
        
        // 获取当前颜色值
        val primaryColor = colorToHexString(colorManager.getLandPrimaryColor())
        val secondaryColor = colorToHexString(colorManager.getLandSecondaryColor())
        
        Timber.tag(TAG).d("替换颜色 - 主色: $primaryColor, 次色: $secondaryColor")
        
        // 替换颜色引用
        return svgContent
            .replace("@color/land_arrow_primary", primaryColor)
            .replace("@color/land_arrow_secondary", secondaryColor)
    }
    
    /**
     * 将颜色值转换为 HEX 字符串
     */
    private fun colorToHexString(color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }
    
    /**
     * 加载车道图标 - 从 res/drawable 加载
     */
    fun loadLandIcon(context: Context, iconNumber: String): PictureDrawable? {
        val resourceName = "ic_land_$iconNumber"
        return loadSvgFromResources(context, resourceName)
    }
    
    /**
     * 调试方法：检查SVG文件是否能正常加载
     */
    fun debugLoadLandIcon(context: Context, iconNumber: String): Boolean {
        val resourceName = "ic_land_$iconNumber"
        return try {
            val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
            if (resourceId == 0) {
                Timber.tag(TAG).e("调试加载失败: 找不到资源 $resourceName")
                return false
            }
            
            val inputStream = context.resources.openRawResource(resourceId)
            val svgContent = replaceColorReferences(inputStream)
            val svg = SVG.getFromString(svgContent)
            inputStream.close()
            
            val picture = svg.renderToPicture()
            val isValid = picture.width > 0 && picture.height > 0
            
            Timber.tag(TAG).d("调试加载 SVG $resourceName: 有效=$isValid, 尺寸=${picture.width}x${picture.height}")
            isValid
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "调试加载 SVG 失败: $resourceName - ${e.message}")
            false
        }
    }
    
    /**
     * 诊断 SVG 加载问题
     */
    fun diagnoseSvgLoading(context: Context, resourceName: String) {
        Timber.tag(TAG).i("=== 开始诊断 SVG: $resourceName ===")
        
        try {
            val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
            Timber.tag(TAG).d("资源ID: $resourceId")
            
            if (resourceId == 0) {
                Timber.tag(TAG).e("❌ 资源不存在: $resourceName")
                return
            }
            
            // 读取原始文件内容
            val inputStream = context.resources.openRawResource(resourceId)
            val rawContent = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()
            
            Timber.tag(TAG).d("📄 文件大小: ${rawContent.length} 字符")
            Timber.tag(TAG).d("🔍 文件内容开头:\n${rawContent.take(200)}")
            
            // 检查关键特征
            val isSvgFormat = rawContent.contains("<svg") && rawContent.contains("</svg>")
            val hasPrimaryColor = rawContent.contains("@color/land_arrow_primary")
            val hasSecondaryColor = rawContent.contains("@color/land_arrow_secondary")
            
            Timber.tag(TAG).d("📊 格式分析:")
            Timber.tag(TAG).d("   - SVG格式: $isSvgFormat")
            Timber.tag(TAG).d("   - 包含主色引用: $hasPrimaryColor")
            Timber.tag(TAG).d("   - 包含次色引用: $hasSecondaryColor")
            
            // 尝试直接解析
            Timber.tag(TAG).d("🧪 测试1: 直接解析原始内容")
            try {
                val svg = SVG.getFromString(rawContent)
                val picture = svg.renderToPicture()
                Timber.tag(TAG).d("   ✅ 直接解析成功 - 尺寸: ${picture.width}x${picture.height}")
            } catch (e: Exception) {
                Timber.tag(TAG).e("   ❌ 直接解析失败: ${e.message}")
            }
            
            // 尝试替换颜色后解析
            Timber.tag(TAG).d("🧪 测试2: 替换颜色后解析")
            try {
                val replacedContent = rawContent
                    .replace("@color/land_arrow_primary", "#808080")
                    .replace("@color/land_arrow_secondary", "#FF0000")
                val svg = SVG.getFromString(replacedContent)
                val picture = svg.renderToPicture()
                Timber.tag(TAG).d("   ✅ 替换颜色后解析成功 - 尺寸: ${picture.width}x${picture.height}")
            } catch (e: Exception) {
                Timber.tag(TAG).e("   ❌ 替换颜色后解析失败: ${e.message}")
            }
            
            // 尝试使用颜色管理器解析
            Timber.tag(TAG).d("🧪 测试3: 使用颜色管理器解析")
            try {
                val inputStream2 = context.resources.openRawResource(resourceId)
                val finalContent = replaceColorReferences(inputStream2)
                inputStream2.close()
                
                val svg = SVG.getFromString(finalContent)
                val picture = svg.renderToPicture()
                Timber.tag(TAG).d("   ✅ 颜色管理器解析成功 - 尺寸: ${picture.width}x${picture.height}")
            } catch (e: Exception) {
                Timber.tag(TAG).e("   ❌ 颜色管理器解析失败: ${e.message}")
            }
            
            Timber.tag(TAG).i("=== 诊断完成: $resourceName ===")
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "诊断失败: ${e.message}")
        }
    }
    
    /**
     * 批量检查 SVG 文件是否存在
     */
    fun checkSvgFilesExist(context: Context): List<String> {
        val existingFiles = mutableListOf<String>()
        val missingFiles = mutableListOf<String>()
        
        // 检查 ic_land_0 到 ic_land_83
        for (i in 0..83) {
            val resourceName = "ic_land_$i"
            if (isSvgFileExists(context, resourceName)) {
                existingFiles.add(resourceName)
            } else {
                missingFiles.add(resourceName)
            }
        }
        
        // 检查 ic_land_89
        val land89File = "ic_land_89"
        if (isSvgFileExists(context, land89File)) {
            existingFiles.add(land89File)
        } else {
            missingFiles.add(land89File)
        }
        
        Timber.tag(TAG).d("SVG 文件检查结果: 存在 ${existingFiles.size} 个, 缺失 ${missingFiles.size} 个")
        if (missingFiles.isNotEmpty()) {
            Timber.tag(TAG).w("缺失的 SVG 文件: $missingFiles")
        }
        
        return existingFiles
    }
    
    /**
     * 检查 SVG 文件是否存在
     */
    private fun isSvgFileExists(context: Context, resourceName: String): Boolean {
        return try {
            val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
            resourceId != 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取所有可用的车道图标名称
     */
    fun getAvailableLandIcons(): List<String> {
        return listOf(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
            "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
            "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
            "60", "61", "62", "63", "64", "65", "66", "67", "68", "69",
            "70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
            "80", "81", "82", "83", "89"
        )
    }
    
    /**
     * 预加载常用 SVG 文件
     */
    fun preloadCommonSvgs(context: Context) {
        try {
            val commonIcons = listOf("13", "38", "66", "89")
            
            commonIcons.forEach { iconNumber ->
                try {
                    loadLandIcon(context, iconNumber)
                    Timber.tag(TAG).d("预加载 SVG 成功: ic_land_$iconNumber")
                } catch (e: Exception) {
                    Timber.tag(TAG).w("预加载 SVG 失败: ic_land_$iconNumber - ${e.message}")
                }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "预加载 SVG 失败")
        }
    }
    
    /**
     * 清理缓存
     */
    fun clearCache() {
        val cacheSize = cache.size
        cache.clear()
        Timber.tag(TAG).d("清理 SVG 缓存, 清理了 $cacheSize 个缓存项")
    }
    
    /**
     * 获取缓存统计信息
     */
    fun getCacheStats(): String {
        return "SVG 缓存: ${cache.size} 个文件"
    }
}
