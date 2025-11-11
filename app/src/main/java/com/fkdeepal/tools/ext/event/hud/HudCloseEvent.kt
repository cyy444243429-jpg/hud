package com.fkdeepal.tools.ext.event.hud

data class HudCloseEvent(val number: Int)  {
    companion object {
        const val KEY = "event_hub_close"
    }
}