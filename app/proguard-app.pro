# 忽略警告
#-ignorewarning

# 混淆保护自己项目的部分代码以及引用的第三方jar包
#-libraryjars libs/xxxxxxxxx.jar

# 不混淆这个包下的类
-keep class fit.staytrue.android.app.api.resp.** { *; }
-keep class fit.staytrue.android.app.api.req.** { *; }
-keep class fit.staytrue.android.app.event.** { *; }
-keep class fit.staytrue.android.app.widget.** { *; }
-keep class fit.staytrue.android.core.widget.** { *; }


-keep class * extends fit.staytrue.android.core.KeepClass{ *; }

#-keep class fit.staytrue.android.ble.**
-keep class fit.staytrue.android.core.http.converter.** {*;}
-keep class fit.staytrue.android.core.adapter.** {*;}

-keep public class * extends fit.staytrue.android.core.adapter.BaseAdapter{*;}
-keep public class * extends fit.staytrue.android.core.adapter.BaseViewHolder{
 <init>(...);
}
-keepclassmembers  class **$** extends fit.staytrue.android.core.adapter.BaseViewHolder {
     <init>(...);
}