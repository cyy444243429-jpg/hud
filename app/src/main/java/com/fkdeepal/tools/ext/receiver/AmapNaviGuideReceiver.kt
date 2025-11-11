package com.fkdeepal.tools.ext.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fkdeepal.tools.ext.AmapFloatManager
import com.fkdeepal.tools.ext.bean.AmapDriveWayBean
import com.fkdeepal.tools.ext.bean.AmapNaviGuideInfoBean
import com.fkdeepal.tools.ext.constants.AmapExtraTypeConstants
import com.fkdeepal.tools.ext.constants.GuideInfoExtraKey
import com.fkdeepal.tools.ext.event.hud.AmapNaviDriveWayEvent
import com.fkdeepal.tools.ext.event.hud.AmapNaviGuideEndEvent
import com.fkdeepal.tools.ext.event.hud.AmapNaviGuideEvent
import com.fkdeepal.tools.ext.event.hud.AmapNaviGuideInfoEvent
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import org.json.JSONObject
import timber.log.Timber

/**
 * 高德地图车机版导航引导信息广播接收器
 * 基于AmapAuto标准广播协议
 */
class AmapNaviGuideReceiver : BroadcastReceiver() {
    private val mGson = Gson()

    companion object {
        private const val TAG = "AmapNaviGuideReceiver"

        // 导航引导信息广播Action
        const val ACTION_NAVI_GUIDE = "AUTONAVI_STANDARD_BROADCAST_SEND"


    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null || intent.action != ACTION_NAVI_GUIDE) {
            return
        }

        try {
            val type = intent.getIntExtra("KEY_TYPE", 0)
            val infoBuilder = StringBuilder()
            when (type) {
                AmapExtraTypeConstants.APP_STATUS -> {
                    // 心跳
                    val state = intent.getIntExtra("EXTRA_STATE", -1)
                    when (state) {
                        2,9 ,45-> {
                            // 2运行结束，退出程序
                            // 9 结束导航
                            // 45 完全运行结束，退出程序
                            LiveEventBus.get(AmapNaviGuideEndEvent.KEY, AmapNaviGuideEndEvent::class.java)
                                .post(AmapNaviGuideEndEvent(state))
                        }


                    }
                }

                AmapExtraTypeConstants.GPS_STATUS -> {

                }


                10109 -> {
                    // 前方路况

                }
13011->{
    //  实时交通光柱图
    // 导航状态下，通过该接⼝可将路况柱状图的信息透出，频率6s发出⼀次
}
                10001 -> {
                    if (AmapFloatManager.isLogEnable){
                        intent.extras?.let {
                            Timber.tag("路况").i (Gson().toJson(it))
                        }
                    }

                    // 解析导航信息字段
                    val guideInfo = AmapNaviGuideInfoBean.parseFromIntent(intent)
                    if (guideInfo != null) {
                        LiveEventBus.get(AmapNaviGuideInfoEvent.KEY, AmapNaviGuideInfoEvent::class.java)
                            .post(AmapNaviGuideInfoEvent(guideInfo))
                    }
                }

                13012 -> {
                    // 车道信息
                    intent.getStringExtra("EXTRA_DRIVE_WAY")?.let {
                        runCatching {
                            val driveWayBean = mGson.fromJson<AmapDriveWayBean>(it, AmapDriveWayBean::class.java)
                          //  Log.d("车道线", mGson.toJson(driveWayBean))
                            LiveEventBus.get(AmapNaviDriveWayEvent.KEY, AmapNaviDriveWayEvent::class.java)
                                .post(AmapNaviDriveWayEvent(driveWayBean))
                        }
                    }
                }

                else -> {
                    intent.extras?.let {
                        if (AmapFloatManager.isLogEnable){
                            Timber.tag("").i (Gson().toJson(it))
                        }

                        infoBuilder.append(it.toString())
                    }
                }
            }

            if (infoBuilder.isNotBlank()) {
                Log.d(TAG, "收到导航信息: $infoBuilder")
              /*  // 显示导航信息
                showNaviInfo(context, infoBuilder.toString())
                LiveEventBus.get(AmapNaviGuideEvent.KEY, AmapNaviGuideEvent::class.java)
                    .post(AmapNaviGuideEvent(infoBuilder.toString()))*/

            }
        } catch (e: Exception) {
            Log.e(TAG, "解析导航信息出错", e)
        }
    }

    /**
     * 转换转向类型为文字描述
     * 具体类型值参考AmapAuto官方协议文档
     */
    private fun getTurnTypeDesc(turnType: Int): String {
        return when (turnType) {
            0 -> "直行"
            1 -> "左转"
            2 -> "右转"
            3 -> "掉头"
            4 -> "左前方"
            5 -> "右前方"
            6 -> "左后方"
            7 -> "右后方"
            else -> "未知转向($turnType)"
        }
    }

    /**
     * 显示导航信息
     * 实际项目中可替换为自定义UI展示
     */
    private fun showNaviInfo(context: Context, info: String) {
        // 简单使用Toast展示，实际应用中建议使用更友好的UI组件
        //Toast.makeText(context, info, Toast.LENGTH_LONG).show()

        // 示例：如果需要在Activity中更新UI
        // if (context is NaviActivity) {
        //     context.runOnUiThread {
        //         context.updateNaviInfo(info)
        //     }
        // }
    }


}