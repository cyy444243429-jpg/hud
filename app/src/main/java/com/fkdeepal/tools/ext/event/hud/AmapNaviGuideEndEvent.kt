package com.fkdeepal.tools.ext.event.hud


data class AmapNaviGuideEndEvent(val state: Int)  {
    companion object {
        const val KEY = "event_amap_navi_guide_end"
    }
}