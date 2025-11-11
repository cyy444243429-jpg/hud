package com.fkdeepal.tools.ext.bean

import androidx.annotation.Keep

@Keep
class AmapDriveWayBean {
    // 车道线是否有效 true/false 为false则不显示车道线信息
    val drive_way_enabled: String? = ""
    // 车道数
    val drive_way_size: Int? = 0
    val drive_way_info: ArrayList<AmapDriveWayInfoBean>? = null

    fun isEnable(): Boolean{
        return  "true".equals(drive_way_enabled,true) && drive_way_size!=null && drive_way_size>0
    }
}