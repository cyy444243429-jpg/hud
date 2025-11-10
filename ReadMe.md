# 高德车机版地图导航信息投屏 Hud
## 限制
1. 仅适用Android 8以上，仅在Android 11系统上测试过
2. 投屏的信息仅有车道线和基于导航信息，如下图
  ![投屏效果示意图](/参考文档/投屏效果示意图.png "")
## 原理

基于Android 多屏幕开发 （Presentation），如果HUD不是基于这个的话，就无法使用了。

## 开发流程

1. 获取屏幕 Display 数据

```kotlin
private val mDisplayManager by lazy { ContextCompat.getSystemService<DisplayManager>(this, DisplayManager::class.java) }
val displays = mDisplayManager?.getDisplays()
```

2. 获取对应hud的屏幕id，并启动Activity显示在该屏幕上

```kotlin
  fun startActivity(context: Context,launchDisplayId:Int){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O   ) {
                val options = ActivityOptions.makeBasic()
                options.setLaunchDisplayId(launchDisplayId)
                val intent = Intent(context, xx::class.java)
                if (context is Activity){

                }else{
                    intent. addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent, options.toBundle());
            }
     }
```

## 其他
在hud上启动整个Activity 可能会遮住原本的内容，可考虑在hud上显示悬浮窗。Android上显示悬浮窗的没有设置目标显示屏幕的接口。但可先在目标显示屏幕启动一个Activity，然后在这个Activity上显示悬浮窗，接着关闭Activity。