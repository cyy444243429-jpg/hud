package com.fkdeepal.tools.ext

import android.content.Context
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.scale
import androidx.recyclerview.widget.LinearLayoutManager
import com.fkdeepal.tools.ext.adapter.HudAmapDriveWayAdapter
import com.fkdeepal.tools.ext.bean.AmapDriveWayInfoBean
import com.fkdeepal.tools.ext.databinding.FloatHudDriveWayBinding
import com.fkdeepal.tools.ext.databinding.FloatHudNaviInfoBinding
import com.fkdeepal.tools.ext.event.hud.AmapNaviDriveWayEvent
import com.fkdeepal.tools.ext.event.hud.AmapNaviGuideEndEvent
import com.fkdeepal.tools.ext.event.hud.AmapNaviGuideEvent
import com.fkdeepal.tools.ext.event.hud.AmapNaviGuideInfoEvent
import com.fkdeepal.tools.ext.receiver.AmapNaviGuideReceiver
import com.fkdeepal.tools.ext.ui.decoration.HorizontalSpaceItemDecoration
import com.fkdeepal.tools.ext.ui.setting.SettingActivity
import com.fkdeepal.tools.ext.utils.AppUtils
import com.fkdeepal.tools.ext.utils.ColorPreferenceManager
import com.fkdeepal.tools.ext.utils.PreferenceUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import java.text.DecimalFormat
import java.text.SimpleDateFormat

object AmapFloatManager {
    var isLogEnable = false
    private var mHudFloatNaviInfoBinding: FloatHudNaviInfoBinding? = null
    private var mWindowManager: WindowManager? = null

    private val mNaviIconArray: IntArray = intArrayOf(
        R.drawable.ic_hud_sou_1, R.drawable.ic_hud_sou_2, R.drawable.ic_hud_sou_3,
        R.drawable.ic_hud_sou_4, R.drawable.ic_hud_sou_5, R.drawable.ic_hud_sou_6,
        R.drawable.ic_hud_sou_7, R.drawable.ic_hud_sou_8, R.drawable.ic_hud_sou_9,
        R.drawable.ic_hud_sou_10, R.drawable.ic_hud_sou_11, R.drawable.ic_hud_sou_12,
        R.drawable.ic_hud_sou_13, R.drawable.ic_hud_sou_14, R.drawable.ic_hud_sou_15,
        R.drawable.ic_hud_sou_16, R.drawable.ic_hud_sou_17, R.drawable.ic_hud_sou_18,
        R.drawable.ic_hud_sou_19, R.drawable.ic_hud_sou_20, R.drawable.ic_hud_sou_21,
        R.drawable.ic_hud_sou_22, R.drawable.ic_hud_sou_23, R.drawable.ic_hud_sou_24,
        R.drawable.ic_hud_sou_25, R.drawable.ic_hud_sou_26, R.drawable.ic_hud_sou_27,
        R.drawable.ic_hud_sou_28
    )
    
    private val mAmapDriveWayInfoData = arrayListOf<AmapDriveWayInfoBean>()
    private var mAmapDriveWayInfoAdapter: HudAmapDriveWayAdapter? = null
    // ========== 新增：间距装饰器引用 ==========
    private var mSpacingItemDecoration: HorizontalSpaceItemDecoration? = null

    private val mDistanceDecimalFormat = DecimalFormat("0.#")
    private val mNaviArriveTimeDateFormat = SimpleDateFormat("HH:mm")
    private val mNextRoadEnterTextColor = "#B3FFFFFF".toColorInt()
    
    // ========== 新增：高亮动画相关 ==========
    private var highlightAnimationHandler: Handler? = null
    private val highlightAnimationRunnable = object : Runnable {
        override fun run() {
            if (ColorPreferenceManager.isHighlightActive()) {
                // 强制清理SVG缓存并重新加载，这样会重新应用颜色
                com.fkdeepal.tools.ext.utils.SvgLoader.clearCache()
                // 强制刷新所有图标
                mAmapDriveWayInfoAdapter?.notifyDataSetChanged()
                // 每16毫秒刷新一次（约60fps）
                highlightAnimationHandler?.postDelayed(this, 16)
            }
        }
    }
    
    init {
        registerEvent()
    }

    var amapNaviGuideReceiver: AmapNaviGuideReceiver? = null

    /**
     * 注册监听高德广播
     */
    private fun registerAMapReceiver() {
        amapNaviGuideReceiver = AmapNaviGuideReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(AmapNaviGuideReceiver.ACTION_NAVI_GUIDE)
        unregisterReceiver()
        ContextCompat.registerReceiver(AppUtils.appContext, amapNaviGuideReceiver, intentFilter, ContextCompat.RECEIVER_EXPORTED)
    }

    fun unregisterReceiver() {
        try {
            amapNaviGuideReceiver?.let {
                AppUtils.appContext.unregisterReceiver(it)
            }
        } catch (e: Exception) {
            // 忽略取消注册异常
        }
    }

    fun hideHudFloat() {
        unregisterReceiver()
        stopHighlightAnimation()
        mHudFloatNaviInfoBinding?.let {
            mWindowManager?.removeView(it.root)
        }
        mHudFloatNaviInfoBinding = null
        // ========== 新增：清理间距装饰器引用 ==========
        mSpacingItemDecoration = null
    }

    fun registerEvent() {
        LiveEventBus.get(AmapNaviDriveWayEvent.KEY, AmapNaviDriveWayEvent::class.java)
            .observeForever { event ->
                val wayInfo = event.info
                mAmapDriveWayInfoData.clear()
                if (wayInfo.isEnable()) {
                    wayInfo.drive_way_info?.let {
                        mAmapDriveWayInfoData.addAll(it)
                    }
                }
                mAmapDriveWayInfoAdapter?.notifyDataSetChanged()
            }
            
        LiveEventBus.get(AmapNaviGuideInfoEvent.KEY, AmapNaviGuideInfoEvent::class.java)
            .observeForever { event ->
                mHudFloatNaviInfoBinding?.apply {
                    val info = event.info
                    
                    // ========== 新增：高亮显示检测逻辑 ==========
                    val shouldHighlight = shouldHighlightNavigation(info)
                    setHighlightState(shouldHighlight)
                    
                    // 原有的图标显示逻辑保持不变
                    tvIconType.visibility = View.GONE
                    val newIcon = info.newIcon
                    when (newIcon) {
                        65 -> ivIcon.setImageResource(R.drawable.ic_hud_sou_4)
                        66 -> ivIcon.setImageResource(R.drawable.ic_hud_sou_5)
                        11, 12 -> {
                            when (info.roundAboutNum) {
                                1 -> ivIcon.setImageResource(R.drawable.ic_hud_rotary_right_1)
                                2 -> ivIcon.setImageResource(R.drawable.ic_hud_rotary_right_2)
                                3 -> ivIcon.setImageResource(R.drawable.ic_hud_rotary_right_3)
                                4 -> ivIcon.setImageResource(R.drawable.ic_hud_rotary_right_4)
                                5 -> ivIcon.setImageResource(R.drawable.ic_hud_rotary_right_5)
                                6 -> ivIcon.setImageResource(R.drawable.ic_hud_rotary_right_6)
                                7 -> ivIcon.setImageResource(R.drawable.ic_hud_rotary_right_7)
                                8 -> ivIcon.setImageResource(R.drawable.ic_hud_rotary_right_8)
                                9 -> ivIcon.setImageResource(R.drawable.ic_hud_rotary_right_9)
                                10 -> ivIcon.setImageResource(R.drawable.ic_hud_rotary_right_10)
                                else -> {
                                    if (newIcon == 11) {
                                        ivIcon.setImageResource(R.drawable.ic_hud_sou_11)
                                    } else {
                                        ivIcon.setImageResource(R.drawable.ic_hud_sou_12)
                                    }
                                }
                            }
                        }
                        else -> {
                            val iconIndex = newIcon - 1
                            val iconResId = mNaviIconArray.getOrNull(iconIndex)
                            if (iconResId != null) {
                                ivIcon.setImageResource(iconResId)
                            } else {
                                ivIcon.setImageResource(R.color.transparent)
                                tvIconType.visibility = View.VISIBLE
                                tvIconType.setText(info.newIcon.toString())
                            }
                        }
                    }

                    val segRemainDis = info.segRemainDis
                    tvSegRemain.setText(buildSpannedString {
                        bold {
                            if (segRemainDis < 10) {
                                append("现在")
                            } else if (segRemainDis > 1000) {
                                append(mDistanceDecimalFormat.format(segRemainDis / 1000.0))
                            } else {
                                append((segRemainDis).toString())
                            }
                        }
                        scale(0.7f) {
                            if (segRemainDis < 10) {
                                // 什么都不添加
                            } else if (segRemainDis > 1000) {
                                append("公里")
                            } else {
                                append("米")
                            }
                            color(mNextRoadEnterTextColor) {
                                append(" 进入")
                            }
                        }
                    })
                    tvRoadName.setText(info.nextRoadName)
                    val routeRemainDis = info.routeRemainDis
                    val routeRemainTime = info.routeRemainTime
                    if (routeRemainDis > -1 && routeRemainTime > -1) {
                        if (routeRemainDis == 0 || routeRemainTime == 0) {
                            tvRemainInfo.setText("到达")
                        } else {
                            tvRemainInfo.setText("${info.routeRemainDisStr} · ${info.routeRemainTimeStr}")
                        }
                    } else {
                        tvRemainInfo.setText("")
                    }
                    val cameraSpeed = info.cameraSpeed
                    if (cameraSpeed > 0) {
                        tvCameraSpeed.setText(cameraSpeed.toString())
                        tvCameraSpeed.visibility = View.VISIBLE
                    } else {
                        tvCameraSpeed.visibility = View.GONE
                    }
                    if (routeRemainTime > 0) {
                        val endTime = System.currentTimeMillis() + routeRemainTime * 1000
                        tvArriveTime.setText("${mNaviArriveTimeDateFormat.format(endTime)}到")
                    } else {
                        tvArriveTime.setText("")
                    }
                }
            }
            
        LiveEventBus.get(AmapNaviGuideEndEvent.KEY, AmapNaviGuideEndEvent::class.java)
            .observeForever { event ->
                mHudFloatNaviInfoBinding?.apply {
                    ivIcon.setImageResource(R.color.transparent)
                    tvIconType.visibility = View.GONE
                    tvCameraSpeed.visibility = View.GONE
                    tvSegRemain.setText("")
                    tvRoadName.setText("")
                    tvRemainInfo.setText("")
                    tvArriveTime.setText("")
                    
                    // ========== 新增：导航结束时清除高亮状态 ==========
                    setHighlightState(false)
                }
            }
    }

    fun showFloat(context: Context, elevation: Float? = 0.001f, translationZ: Float = 0.01f) {
        hideHudFloat()
        registerAMapReceiver()

        mHudFloatNaviInfoBinding = FloatHudNaviInfoBinding.inflate(LayoutInflater.from(context))
        mWindowManager = ContextCompat.getSystemService(context, WindowManager::class.java)
        
        // ========== 修改：恢复原来的宽度 ==========
        val naviInfoLayoutParams = WindowManager.LayoutParams(
            275,  // 恢复原来的宽度
            134,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        naviInfoLayoutParams.apply {
            x = 0
            y = 60
        }
        mAmapDriveWayInfoAdapter = HudAmapDriveWayAdapter(mAmapDriveWayInfoData)
        
        SettingActivity.setHudAdapter(mAmapDriveWayInfoAdapter)
        
        mHudFloatNaviInfoBinding?.rvDriverWay?.adapter = mAmapDriveWayInfoAdapter

        // 设置水平线性布局管理器
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mHudFloatNaviInfoBinding?.rvDriverWay?.layoutManager = layoutManager

        // ========== 修改：使用动态间距并保存引用 ==========
        val currentSpacing = PreferenceUtils.getLandIconSpacing(context)
        mSpacingItemDecoration = HorizontalSpaceItemDecoration(currentSpacing)
        mHudFloatNaviInfoBinding?.rvDriverWay?.addItemDecoration(mSpacingItemDecoration!!)

        val naviInfoView = mHudFloatNaviInfoBinding!!.root

        runCatching {
            elevation?.let { naviInfoView.elevation = it }
            naviInfoView.translationZ = translationZ
        }
        naviInfoLayoutParams.gravity = Gravity.RIGHT or Gravity.BOTTOM

        mWindowManager?.addView(naviInfoView, naviInfoLayoutParams)
    }
    
    // ========== 新增：实时刷新间距的方法 ==========
    fun refreshIconSpacing() {
        Timber.d("AmapFloatManager.refreshIconSpacing() - 刷新图标间距")
        mHudFloatNaviInfoBinding?.rvDriverWay?.let { recyclerView ->
            // 移除旧的间距装饰器
            mSpacingItemDecoration?.let { oldDecoration ->
                recyclerView.removeItemDecoration(oldDecoration)
            }
            
            // 添加新的间距装饰器
            val newSpacing = PreferenceUtils.getLandIconSpacing(AppUtils.appContext)
            mSpacingItemDecoration = HorizontalSpaceItemDecoration(newSpacing)
            recyclerView.addItemDecoration(mSpacingItemDecoration!!)
            
            // 强制刷新布局
            recyclerView.invalidateItemDecorations()
            mAmapDriveWayInfoAdapter?.notifyDataSetChanged()
            
            Timber.d("AmapFloatManager.refreshIconSpacing() - 间距已更新为: ${newSpacing}px")
        }
    }
    
    // ========== 新增：判断是否需要高亮显示的方法 ==========
    private fun shouldHighlightNavigation(info: com.fkdeepal.tools.ext.bean.AmapNaviGuideInfoBean): Boolean {
        val segRemainDis = info.segRemainDis // 当前路段剩余距离
        
        // 检查距离是否在100米以内
        val isWithin100Meters = segRemainDis <= 100 && segRemainDis > 0
        
        if (!isWithin100Meters) {
            return false
        }
        
        // 检查导航动作类型（需要根据实际图标判断）
        val newIcon = info.newIcon
        return when (newIcon) {
            // 转弯相关图标
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10 -> true // 各种转弯
            // 汇入相关图标  
            13, 14, 15 -> true // 汇入主路等
            // 掉头图标
            19 -> true // 掉头
            // 终点相关图标
            28 -> true // 到达目的地
            // 环岛相关
            11, 12 -> true // 环岛
            // 服务区、收费站等也可以考虑加入
            20, 21, 22, 23 -> true
            else -> false
        }.also { shouldHighlight ->
            if (shouldHighlight) {
                Log.d("AmapFloatManager", "检测到需要高亮的导航动作: 图标=$newIcon, 距离=${segRemainDis}米")
            }
        }
    }
    
    // ========== 新增：高亮状态管理方法 ==========
    private fun setHighlightState(active: Boolean) {
        val currentState = ColorPreferenceManager.isHighlightActive()
        Log.d("AmapFloatManager", "设置高亮状态: $active, 当前状态: $currentState")
        
        if (active) {
            ColorPreferenceManager.setHighlightActive(true)
            startHighlightAnimation()
            Log.d("AmapFloatManager", "✅ 高亮状态已设置为true，启动动画")
        } else {
            ColorPreferenceManager.setHighlightActive(false)
            stopHighlightAnimation()
            Log.d("AmapFloatManager", "高亮状态已设置为false，停止动画")
        }
    }
    
    private fun startHighlightAnimation() {
        if (highlightAnimationHandler == null) {
            highlightAnimationHandler = Handler(Looper.getMainLooper())
            highlightAnimationHandler?.post(highlightAnimationRunnable)
            Log.d("AmapFloatManager", "开始高亮动画")
        }
    }
    
    private fun stopHighlightAnimation() {
        highlightAnimationHandler?.removeCallbacks(highlightAnimationRunnable)
        highlightAnimationHandler = null
        Log.d("AmapFloatManager", "停止高亮动画")
    }
}
