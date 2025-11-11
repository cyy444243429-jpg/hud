package com.fkdeepal.tools.ext.constants

object GuideInfoExtraKey {
    /**
     * 导航类型，对应的值为int类型<br>
     * 0：GPS导航<br>
     * 1：模拟导航
     * 2：巡航
     */
    const val TYPE = "TYPE";
    /**
     * 当前道路名称，对应的值为String类型
     */
     const val  CUR_ROAD_NAME = "CUR_ROAD_NAME";
    /**
     * 下一道路名，对应的值为String类型
     */
     const val  NEXT_ROAD_NAME = "NEXT_ROAD_NAME";
    /**
     * 距离最近服务区的距离，对应的值为int类型，单位：米
     */
     const val  SAPA_DIST = "SAPA_DIST";
    /**
     * 服务区类型，对应的值为int类型<br>
     * 0：高速服务区<br>
     * 1：其他服务器
     */
     const val  SAPA_TYPE = "SAPA_TYPE";
    /**
     * 距离最近的电子眼距离，对应的值为int类型，单位：米<br>
     */
     const val  CAMERA_DIST = "CAMERA_DIST";
    /**
     * 电子眼类型，对应的值为int类型<br>
     * 0 测速摄像头，<br>
     * 1为监控摄像头，<br>
     * 2为闯红灯拍照，<br>
     * 3为违章拍照，<br>
    51 |
     * 4为公交专用道摄像头
     */
     const val  CAMERA_TYPE = "CAMERA_TYPE";
    /**
     * 电子眼限速度，对应的值为int类型，无限速则为0，单位：公里/小时
     */
     const val  CAMERA_SPEED = "CAMERA_SPEED";
    /**
     * 下一个将要路过的电子眼编号，若为-1则对应的道路上没有电子眼，对应的值为int类型
     */
     const val  CAMERA_INDEX = "CAMERA_INDEX";
    /**
     * 导航转向图标，对应的值为int类型
     */
     const val  ICON = "ICON";
    /**
     * 导航最新的转向图标，对应的值为int类型
     */
     const val  NEW_ICON = "NEW_ICON";
    /**
     * 路径剩余距离，对应的值为int类型，单位：米
     */
     const val  ROUTE_REMAIN_DIS = "ROUTE_REMAIN_DIS";
    /**
     * 路径剩余时间，对应的值为int类型，单位：秒
     */
     const val  ROUTE_REMAIN_TIME = "ROUTE_REMAIN_TIME";
    /**
     * 当前导航段剩余距离，对应的值为int类型，单位：米
     */
     const val  SEG_REMAIN_DIS = "SEG_REMAIN_DIS";
    /**
     * 当前导航段剩余时间，对应的值为int类型，单位：秒
     */
     const val  SEG_REMAIN_TIME = "SEG_REMAIN_TIME";
    /**
    52 |
     * 自车方向，对应的值为int类型，单位：度，以正北为基准，顺时针增加
     */
     const val  CAR_DIRECTION = "CAR_DIRECTION";
    /**
     * 自车纬度，对应的值为double类型
     */
     const val  CAR_LATITUDE = "CAR_LATITUDE";
    /**
     * 自车经度，对应的值为double类型
     */
     const val  CAR_LONGITUDE = "CAR_LONGITUDE";
    /**
     * 当前道路速度限制，对应的值为int类型，单位：公里/小时
     */
     const val  LIMITED_SPEED = "LIMITED_SPEED";
    /**
     * 当前自车所在Link，对应的值为int类型，从0开始
     */
     const val  CUR_SEG_NUM = "CUR_SEG_NUM";
    /**
     * 当前位置的前一个形状点号，对应的值为int类型，从0开始
     */
     const val  CUR_POINT_NUM = "CUR_POINT_NUM";
    /**
     * 环岛出口序号，对应的值为int类型，从0开始，只有在icon为11和12时有效，其余为无效值0
     */
     const val  ROUND_ABOUT_NUM = "ROUNG_ABOUT_NUM";
    /**
     * 环岛出口个数，对应的值为int类型，只有在icon为11和12时有效，其余为无效值0
     */
     const val  ROUND_ALL_NUM = "ROUND_ALL_NUM";
    /**
     * 路径总距离，对应的值为int类型，单位：米
     */
     const val  ROUTE_ALL_DIS = "ROUTE_ALL_DIS";
    /**
    53 |
     * 路径总时间，对应的值为int类型，单位：秒
     */
     const val  ROUTE_ALL_TIME = "ROUTE_ALL_TIME";
    /**
     * 当前车速，对应的值为int类型，单位：公里/小时
     */
     const val  CUR_SPEED = "CUR_SPEED";
    /**
     * 红绿灯个数，对应的值为int类型
     */
     const val  TRAFFIC_LIGHT_NUM = "TRAFFIC_LIGHT_NUM";
    /**
     * 服务区个数，对应的值为int类型
     */
     const val  SAPA_NUM = "SAPA_NUM";
    /**
     * 下一个服务区名称，对应的值为String类型
     */
     const val  SAPA_NAME = "SAPA_NAME";
    /**
     * 当前道路类型，对应的值为int类型
     * 0：高速公路
     * 1：国道
     * 2：省道
     * 3：县道
     * 4：乡公路
     * 5：县乡村内部道路
     * 6：主要大街、城市快速道
     * 7：主要道路
     * 8：次要道路
     * 9：普通道路
     * 10：非导航道路
     */
     const val  ROAD_TYPE = "ROAD_TYPE";
    /**
     * 路径剩余时间，对应的值为String类型，单位：天/小时/分钟 比如：1天2小时， 21小时30分
    钟（只用于长安）
     */
     const val  ROUTE_REMAIN_TIME_STRING = "ROUTE_REMAIN_TIME_STRING";
    /**
     * 下下个路名名称，对应的值为String类型
     */
     const val  NEXT_NEXT_ROAD_NAME = "NEXT_NEXT_ROAD_NAME";
    /**
     * 下下个路口转向图标，对应的值为int类型
     */
     const val  NEXT_NEXT_TURN_ICON = "NEXT_NEXT_TURN_ICON";
    /**
     * 距离下下个路口剩余距离，对应的值为int类型，单位：米
     */
     const val  NEXT_SEG_REMAIN_DIS = "NEXT_SEG_REMAIN_DIS";
    /**
     * 距离下下个路口剩余时间，对应的值为int类型，单位：秒
     */
     const val  NEXT_SEG_REMAIN_TIME = "NEXT_SEG_REMAIN_TIME";
    /**
     * 转换后的路径剩余距离（带单位）
     */
     const val  ROUTE_REMAIN_DIS_AUTO = "ROUTE_REMAIN_DIS_AUTO";
    /**
     * 转换后的路径剩余时间（带单位）
     */
     const val  ROUTE_REMAIN_TIME_AUTO = "ROUTE_REMAIN_TIME_AUTO";
    /**
     * 转换后距离最近服务区的距离，对应的值为String类型，由距离和单位组成
     */
     const val  SAPA_DIST_AUTO = "SAPA_DIST_AUTO";
    /**
     * 转换后当前导航段剩余距离，对应的值为String类型，由距离和单位组成
     */
     const val  SEG_REMAIN_DIS_AUTO = "SEG_REMAIN_DIS_AUTO"
}