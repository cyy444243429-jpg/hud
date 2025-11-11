package com.fkdeepal.tools.ext.bean

/**
 * {"waitRound":0,"trafficLightStatus":1,"greenLightLastSecond":0,"dir":4,"redLightCountDownSeconds":37,"KEY_TYPE":60073}
 *
 */
data class AmapTrafficLightBean(val waitRound:Int,
                                val trafficLightStatus:Int,
                                val greenLightLastSecond:Int,
                                val dir:Int,
                                val redLightCountDownSeconds: Int) {
}