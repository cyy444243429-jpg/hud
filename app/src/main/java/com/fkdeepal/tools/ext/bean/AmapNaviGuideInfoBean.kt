package com.fkdeepal.tools.ext.bean

import android.content.Intent
import android.util.Log
import com.fkdeepal.tools.ext.constants.GuideInfoExtraKey
import com.fkdeepal.tools.ext.exts.safe

data class AmapNaviGuideInfoBean(
    val type: Int,
    val roadType: Int,
    val currentRoadName: String,
    val nextRoadName: String,
    val nextNextTurnIcon: Int,
    val nextNextRoadName: String,
    val newIcon: Int,
    val roundAboutNum: Int,
    val segRemainDis: Int,
    val segRemainDisStr: String,
    val nextSegRemainDis: Int,
    val routeRemainDis: Int,
    val routeRemainDisStr: String,
    val routeRemainTime: Int,
    val routeRemainTimeStr: String,
    /**
     * 路径总距离，对应的值为int类型，单位：米
     */
    val routeAllDis: Int,
    /**
     * 路径总时间，对应的值为int类型，单位：秒
     */
    val routeAllTime: Int,
    /**
     * 当前车速，对应的值为int类型，单位：公里/小时
     */
    val curSpeed: Int,
    val cameraSpeed: Int) {

    companion object {

        fun parseFromIntent(intent: Intent): AmapNaviGuideInfoBean? {
            val info = AmapNaviGuideInfoBean(intent.getIntExtra(GuideInfoExtraKey.TYPE, -1),
                                             intent.getIntExtra(GuideInfoExtraKey.ROAD_TYPE, -1),
                                             intent.getStringExtra(GuideInfoExtraKey.CUR_ROAD_NAME)
                                                 .safe(),
                                             intent.getStringExtra(GuideInfoExtraKey.NEXT_ROAD_NAME)
                                                 .safe(),
                                             intent.getIntExtra(GuideInfoExtraKey.NEXT_NEXT_TURN_ICON, -1),
                                             intent.getStringExtra(GuideInfoExtraKey.NEXT_NEXT_ROAD_NAME)
                                                 .safe(),

                                             intent.getIntExtra(GuideInfoExtraKey.NEW_ICON, -1),
                                             intent.getIntExtra(GuideInfoExtraKey.ROUND_ABOUT_NUM, -1),
                                             intent.getIntExtra(GuideInfoExtraKey.SEG_REMAIN_DIS, -1),
                                             intent.getStringExtra(GuideInfoExtraKey.SEG_REMAIN_DIS_AUTO)
                                                 .safe(),
                                             intent.getIntExtra(GuideInfoExtraKey.NEXT_SEG_REMAIN_DIS, -1),
                                             intent.getIntExtra(GuideInfoExtraKey.ROUTE_REMAIN_DIS, -1),
                                             intent.getStringExtra(GuideInfoExtraKey.ROUTE_REMAIN_DIS_AUTO)
                                                 .safe(),
                                             intent.getIntExtra(GuideInfoExtraKey.ROUTE_REMAIN_TIME, -1),
                                             intent.getStringExtra(GuideInfoExtraKey.ROUTE_REMAIN_TIME_AUTO)
                                                 .safe(),
                                             intent.getIntExtra(GuideInfoExtraKey.ROUTE_ALL_DIS, -1),
                                             intent.getIntExtra(GuideInfoExtraKey.ROUTE_ALL_TIME, -1),
                                             intent.getIntExtra(GuideInfoExtraKey.CUR_SPEED, -1),
                                             intent.getIntExtra(GuideInfoExtraKey.CAMERA_SPEED, -1))
            Log.d("导航透传信息", info.toString())
            if (info.nextNextTurnIcon != -1 && info.nextNextRoadName.isNotBlank() && info.nextSegRemainDis > 0) {
                val distanceDiff = info.nextSegRemainDis - info.segRemainDis
                if (distanceDiff > 0 && !info.nextNextRoadName.equals(info.nextRoadName)) {
                    Log.d("下下路况", "icon:${info.nextNextTurnIcon},路名:${info.nextNextRoadName},距离:${distanceDiff}")
                }

            }
            if (info.newIcon != -1 && info.currentRoadName.isNotBlank() && info.currentRoadName.isNotBlank()) {
                return info
            }

            return null
        }
    }

}