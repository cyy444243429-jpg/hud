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
    
    // ========== 控制图像大小和位置的关键参数 ==========
    // 调整这个值可以控制所有图标的缩放大小：值越大图标越大
    private const val UNIFORM_SCALE = 0.008f
    
    // 调整这个值可以控制翻转图标的水平位置补偿：值越大翻转图标越靠右
    private const val FLIP_OFFSET_X = 10
    
    // ========== 主要加载方法 ==========
    
    /**
     * 从 res/raw 加载 SVG 文件
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
            
            // 从 res/raw 读取 SVG 文件
            val resourceId = context.resources.getIdentifier(resourceName, "raw", context.packageName)
            if (resourceId == 0) {
                Timber.tag(TAG).e("找不到资源: $resourceName")
                return null
            }
            
            inputStream = context.resources.openRawResource(resourceId)
            
            // 添加调试：检查实际读取的内容
            val rawContent = inputStream.bufferedReader().use { it.readText() }
            Timber.tag(TAG).d("实际读取的文件内容大小: ${rawContent.length} 字符")
            Timber.tag(TAG).d("文件开头: ${rawContent.take(100)}")
            
            // 重新创建输入流进行解析
            inputStream.close()
            inputStream = context.resources.openRawResource(resourceId)
            
            val svgContent = replaceColorReferences(inputStream)
            val svg = SVG.getFromString(svgContent)
            
            // 渲染设置
            svg.setDocumentWidth("100%")
            svg.setDocumentHeight("100%")
            
            val picture = svg.renderToPicture()
            
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
     * 加载车道图标 - 从 res/raw 加载
     */
    fun loadLandIcon(context: Context, iconNumber: String): PictureDrawable? {
        val resourceName = "ic_land_$iconNumber"
        return loadSvgFromResources(context, resourceName)
    }
    
    // ========== 新增：标准化图标大小和位置的方法 ==========
    
    /**
     * 加载标准化的车道图标 - 统一大小和位置
     */
    fun loadStandardizedLandIcon(
        context: Context, 
        iconNumber: String, 
        targetWidth: Int = 60,  // 目标宽度
        targetHeight: Int = 80  // 目标高度
    ): PictureDrawable? {
        val resourceName = "ic_land_$iconNumber"
        val cacheKey = "${resourceName}_standard_${targetWidth}x${targetHeight}"
        
        cache[cacheKey]?.let {
            Timber.tag(TAG).d("从缓存加载标准化 SVG: $cacheKey")
            return it
        }
        
        var inputStream: InputStream? = null
        return try {
            Timber.tag(TAG).d("开始加载标准化 SVG: $resourceName, 目标尺寸: ${targetWidth}x${targetHeight}")
            
            val resourceId = context.resources.getIdentifier(resourceName, "raw", context.packageName)
            if (resourceId == 0) {
                Timber.tag(TAG).e("找不到资源: $resourceName")
                return null
            }
            
            inputStream = context.resources.openRawResource(resourceId)
            val svgContent = inputStream.bufferedReader().use { it.readText() }
            
            // 标准化SVG内容（统一transform）
            val standardizedContent = standardizeSvgTransform(svgContent, targetWidth, targetHeight)
            
            // 替换颜色
            val finalContent = replaceColorInContent(standardizedContent)
            
            val svg = SVG.getFromString(finalContent)
            svg.setDocumentWidth(targetWidth.toFloat())
            svg.setDocumentHeight(targetHeight.toFloat())
            
            val picture = svg.renderToPicture()
            val drawable = PictureDrawable(picture)
            
            cache[cacheKey] = drawable
            Timber.tag(TAG).d("成功加载标准化 SVG: $resourceName, 实际尺寸: ${picture.width}x${picture.height}")
            drawable
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "加载标准化 SVG 失败: $resourceName - ${e.message}")
            null
        } finally {
            inputStream?.close()
        }
    }
    
    /**
     * 标准化SVG的transform，智能处理翻转图标
     * 关键参数：
     * - UNIFORM_SCALE: 控制图标大小（值越大图标越大）
     * - FLIP_OFFSET_X: 控制翻转图标的水平位置补偿
     */
    private fun standardizeSvgTransform(svgContent: String, targetWidth: Int, targetHeight: Int): String {
        // 检测是否包含翻转transform
        val hasFlip = svgContent.contains("scale(-1,1)") || svgContent.contains("scale\\(-1,1\\)")
        Timber.tag(TAG).d("检测到翻转: $hasFlip")
        
        // 移除原有的所有transform
        val transformPattern = """transform="[^"]*"""".toRegex()
        
        val centerX = targetWidth / 2
        val centerY = targetHeight / 2
        
        // 根据是否翻转设置不同的transform
        val standardTransform = if (hasFlip) {
            // 翻转图标：先移动到中心，再翻转，再调整位置补偿
            "transform=\"translate(${centerX + FLIP_OFFSET_X},$centerY) scale(-$UNIFORM_SCALE,-$UNIFORM_SCALE)\""
        } else {
            // 正常图标
            "transform=\"translate($centerX,$centerY) scale($UNIFORM_SCALE,-$UNIFORM_SCALE)\""
        }
        
        Timber.tag(TAG).d("应用标准化transform: $standardTransform")
        return transformPattern.replace(svgContent, standardTransform)
    }
    
    /**
     * 替换颜色（不涉及transform）
     */
    private fun replaceColorInContent(svgContent: String): String {
        val primaryColor = colorToHexString(colorManager.getLandPrimaryColor())
        val secondaryColor = colorToHexString(colorManager.getLandSecondaryColor())
        
        return svgContent
            .replace("@color/land_arrow_primary", primaryColor)
            .replace("@color/land_arrow_secondary", secondaryColor)
    }
    
    // ========== 原有其他方法保持不变 ==========
    
    /**
     * 调试方法：检查SVG文件是否能正常加载
     */
    fun debugLoadLandIcon(context: Context, iconNumber: String): Boolean {
        val resourceName = "ic_land_$iconNumber"
        return try {
            val resourceId = context.resources.getIdentifier(resourceName, "raw", context.packageName)
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
     * 诊断 raw 中的 SVG 文件
     */
    fun diagnoseSvgLoading(context: Context, resourceName: String) {
        Timber.tag(TAG).i("=== 诊断 raw SVG: $resourceName ===")
        
        try {
            val resourceId = context.resources.getIdentifier(resourceName, "raw", context.packageName)
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
            val hasXmlDeclaration = rawContent.startsWith("<?xml")
            val isSvgFormat = rawContent.contains("<svg") && rawContent.contains("</svg>")
            val hasPrimaryColor = rawContent.contains("@color/land_arrow_primary")
            val hasSecondaryColor = rawContent.contains("@color/land_arrow_secondary")
            
            Timber.tag(TAG).d("📊 格式分析:")
            Timber.tag(TAG).d("   - XML声明: $hasXmlDeclaration")
            Timber.tag(TAG).d("   - SVG格式: $isSvgFormat")
            Timber.tag(TAG).d("   - 包含主色引用: $hasPrimaryColor")
            Timber.tag(TAG).d("   - 包含次色引用: $hasSecondaryColor")
            
            // 检查是否有二进制字符
            val binaryChars = rawContent.take(1000).count { it.code < 32 && it !in listOf('\t', '\n', '\r') }
            Timber.tag(TAG).d("🔧 二进制字符数量: $binaryChars")
            
            if (binaryChars > 10) {
                Timber.tag(TAG).e("❌ 文件可能被损坏，包含过多二进制字符")
            }
            
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
            
            // 尝试修复常见问题
            Timber.tag(TAG).d("🧪 测试4: 尝试修复解析")
            try {
                // 移除可能的 BOM 字符
                val cleanedContent = rawContent.trim().removePrefix("\uFEFF")
                val svg = SVG.getFromString(cleanedContent)
                val picture = svg.renderToPicture()
                Timber.tag(TAG).d("   ✅ 修复后解析成功 - 尺寸: ${picture.width}x${picture.height}")
            } catch (e: Exception) {
                Timber.tag(TAG).e("   ❌ 修复后解析失败: ${e.message}")
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
            val resourceId = context.resources.getIdentifier(resourceName, "raw", context.packageName)
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
    
    /**
     * 安全加载 SVG，如果失败返回 null
     */
    fun safeLoadLandIcon(context: Context, iconNumber: String): PictureDrawable? {
        return try {
            loadLandIcon(context, iconNumber)
        } catch (e: Exception) {
            Timber.tag(TAG).w("SVG 加载失败，使用降级处理: ic_land_$iconNumber")
            // 返回默认图标或 null
            null
        }
    }
    
    /**
     * 安全加载标准化 SVG，如果失败返回 null
     */
    fun safeLoadStandardizedLandIcon(
        context: Context, 
        iconNumber: String, 
        targetWidth: Int = 60,
        targetHeight: Int = 80
    ): PictureDrawable? {
        return try {
            loadStandardizedLandIcon(context, iconNumber, targetWidth, targetHeight)
        } catch (e: Exception) {
            Timber.tag(TAG).w("标准化 SVG 加载失败，使用降级处理: ic_land_$iconNumber")
            null
        }
    }
}
