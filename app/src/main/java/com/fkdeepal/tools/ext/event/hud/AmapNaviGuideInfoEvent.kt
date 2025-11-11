package com.fkdeepal.tools.ext.event.hud

import com.fkdeepal.tools.ext.bean.AmapNaviGuideInfoBean


data class AmapNaviGuideInfoEvent(val info: AmapNaviGuideInfoBean)  {
    companion object {
        const val KEY = "event_amap_navi_guide_info"
    }
}