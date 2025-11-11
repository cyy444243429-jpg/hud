
##---------------Begin: OkHttp3  ----------
# OkHttp3 框架混淆规则
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**



# Understand the @Keep support annotation.
-keep class androidx.annotation.Keep

-keep @androidx.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}
#retrofit
-dontwarn okio.**
-dontwarn javax.annotation.**
#解决使用Retrofit+rxJava联网时，在6.0系统出现java.lang.InternalError奔溃的问题:http://blog.csdn.net/mp624183768/article/details/79242147
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}

-keepattributes Signature
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keepattributes AnnotationDefault,RuntimeVisibleAnnotations




-adaptclassstrings
-keepattributes InnerClasses, EnclosingMethod, Signature, *Annotation*


##---------------End: Aspectj   ----------
##---------------Begin: glide   ----------
#-keep class com.bumptech.** {*;}
#-keep class jp.wasabeef.glide.** {*;}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}


#photoview
-keep class com.github.chrisbanes.photoview.** { *; }


##---------------Begin: MultiLanguages   ----------
-keep class fit.staytrue.android.language.** {*;}
##---------------End: MultiLanguages   ----------
##---------------Begin: GSYVideoPlayer   ----------
-keep class com.shuyu.gsyvideoplayer.video.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.**
-keep class com.shuyu.gsyvideoplayer.video.base.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.base.**
-keep class com.shuyu.gsyvideoplayer.utils.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.utils.**
-keep class com.shuyu.gsyvideoplayer.player.** {*;}
-dontwarn com.shuyu.gsyvideoplayer.player.**
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**
-keep class androidx.media3.** {*;}

-keep interface androidx.media3.**

-keep class android.view.** {*;}
-keep class com.shuyu.alipay.** {*;}
-keep interface com.shuyu.alipay.**

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, java.lang.Boolean);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class com.alivc.**{*;}
-keep class com.aliyun.**{*;}
-keep class com.cicada.**{*;}
-dontwarn com.alivc.**
-dontwarn com.aliyun.**
-dontwarn com.cicada.**
##---------------End: GSYVideoPlayer   ----------
##---------------Begin: Rxjava   ----------
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent
##---------------End: Rxjava   ----------
##---------------Begin: RxAndroidBle   ----------
-keepclassmembers class * extends android.bluetooth.BluetoothGattCallback {
    # This method is hidden in AOSP sources and therefore Proguard strips it by default
    public void onConnectionUpdated(android.bluetooth.BluetoothGatt, int, int, int, int);
}
##---------------End: RxAndroidBle   ----------
##---------------Begin: LiveEventBus   ----------
-dontwarn com.jeremyliao.liveeventbus.**
-keep class com.jeremyliao.liveeventbus.** { *; }
-keep class androidx.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.arch.core.** { *; }
-keep class androidx.viewpager2.widget.** { *; }
-keep class androidx.recyclerview.widget.** { *; }
##---------------End: LiveEventBus   ----------
##---------------Begin: PictureSelector 、ucrop  ----------
-keep class com.luck.picture.lib.** { *; }

## 如果引入了Camerax库请添加混淆
-keep class com.luck.lib.camerax.** { *; }

## 如果引入了Ucrop库请添加混淆
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
##---------------End: PictureSelector   ----------


# Firebase相关的类
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Firebase消息推送的类
-keep class com.google.android.gms.measurement.** { *; }
-dontwarn com.google.android.gms.measurement.**

# Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Keep specific FCM interfaces
-keep interface com.google.firebase.messaging.** { *; }

# 如果您使用动态链接
-keep class com.google.android.gms.common.api.internal.** { *; }

# JSON 反序列化所需规则
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keepattributes Signature
-keepattributes *Annotation*

# 防止混淆Kotlin的内联类（如果您使用Kotlin）
-keepattributes KotlinMetadata

# 防止混淆带有@Keep注解的类和成员
-keep @androidx.annotation.Keep class * { *; }

# Firebase Analytics
-keep class com.google.firebase.analytics.** { *; }
##---------------End: Firebase  ----------

-keep class *.R$ {*;}
-keepattributes InnerClasses
# 使用R8全模式，对未保留的类剥离通用签名。挂起函数被包装在使用类型参数的continuation中。
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# 如果不保留，R8完整模式将从返回类型中剥离通用签名。
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# 在R8全模式下，对未保留的类剥离通用签名。
-keep,allowobfuscation,allowshrinking class retrofit2.Response