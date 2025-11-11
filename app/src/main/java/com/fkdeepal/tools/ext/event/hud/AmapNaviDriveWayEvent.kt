package com.fkdeepal.tools.ext.event.hud

import com.fkdeepal.tools.ext.bean.AmapDriveWayBean
import com.fkdeepal.tools.ext.bean.AmapNaviGuideInfoBean


data class AmapNaviDriveWayEvent(val info: AmapDriveWayBean)  {
    companion object {
        const val KEY = "event_amap_drive_way"
    }
}