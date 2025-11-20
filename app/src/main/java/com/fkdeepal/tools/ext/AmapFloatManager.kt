package com.fkdeepal.tools.ext

import android.content.Context
import android.content.IntentFilter
import android.graphics.PixelFormat
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
import com.fkdeepal.tools.ext.adapter.HudAmapDriveWayAdapter
import com.fkdeepal.tools.ext.bean.AmapDriveWayInfoBean
import com.fkdeepal.tools.ext.databinding.FloatHudDriveWayBinding
import com.fkdeepal.tools.ext.databinding.FloatHudNaviInfoBinding
import com.fkdeepal.tools.ext.event.hud.AmapNaviDriveWayEvent
import com.fkdeepal.tools.ext.event.hud.AmapNaviGuideEndEvent
import com.fkdeepal.tools.ext.event.hud.AmapNaviGuideEvent
import com.fkdeepal.tools.ext.event.hud.AmapNaviGuideInfoEvent
import com.fkdeepal.tools.ext.receiver.AmapNaviGuideReceiver
import com.fkdeepal.tools.ext.ui.setting.SettingActivity
import com.fkdeepal.tools.ext.utils.AppUtils
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

    private val mDistanceDecimalFormat = DecimalFormat("0.#")
    private val mNaviArriveTimeDateFormat = SimpleDateFormat("HH:mm")
    private val mNextRoadEnterTextColor = "#B3FFFFFF".toColorInt()
    
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
        mHudFloatNaviInfoBinding?.let {
            mWindowManager?.removeView(it.root)
        }
        mHudFloatNaviInfoBinding = null
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
                }
            }
    }

    fun showFloat(context: Context, elevation: Float? = 0.001f, translationZ: Float = 0.01f) {
        hideHudFloat()
        registerAMapReceiver()

        mHudFloatNaviInfoBinding = FloatHudNaviInfoBinding.inflate(LayoutInflater.from(context))
        mWindowManager = ContextCompat.getSystemService(context, WindowManager::class.java)
        val naviInfoLayoutParams = WindowManager.LayoutParams(
            275,
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
        
        // ========== 新增：设置适配器引用 ==========
        SettingActivity.setHudAdapter(mAmapDriveWayInfoAdapter)
        
        mHudFloatNaviInfoBinding?.rvDriverWay?.adapter = mAmapDriveWayInfoAdapter

        val naviInfoView = mHudFloatNaviInfoBinding!!.root

        runCatching {
            elevation?.let { naviInfoView.elevation = it }
            naviInfoView.translationZ = translationZ
        }
        naviInfoLayoutParams.gravity = Gravity.RIGHT or Gravity.BOTTOM

        mWindowManager?.addView(naviInfoView, naviInfoLayoutParams)
    }
}
