#############################################
#
# 基础混淆配置
#
#############################################


####################基本混淆指令的设置####################

#不混淆指定的包名。有多个包名可以用逗号隔开。包名可以包含 ？、*、** 通配符，还可以在包名前加上 ! 否定符。只有开启混淆时可用。如果你使用了 mypackage.MyCalss.class.getResource(""); 这些代码获取类目录的代码，就会出现问题。需要使用 -keeppackagenames 保留包名。
##-keeppackagenames com.example.testdemo
#指定类、方法及字段混淆后时用的混淆字典。默认使用 ‘a’，’b’ 等短名称作为混淆后的名称。
#-obfuscationdictionary dictionary.txt
# 打印 usage
-printusage proguard/usage.txt
# 打印 mapping
-printmapping proguard/mapping.txt
# 打印 seeds
-printseeds proguard/seeds.txt
# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile
#关闭优化功能。默认情况下启用优化。
-dontoptimize

# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

# 指定不忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 指定不忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

# 记录日志，使我们的项目混淆后产生映射文件（类名->混淆后类名）
-verbose

# 忽略警告，避免打包时某些警告出现，没有这个的话，构建报错
-ignorewarnings

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

# 不混淆Annotation(保留注解)
-keepattributes *Annotation*,InnerClasses

# 避免混淆泛型
-keepattributes Signature

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*


####################Android开发中需要保留的公共部分####################

# 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留了继承自Activity、Application、Fragment这些类的子类
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View


# support-v4
-dontwarn android.support.v4.**
-keep class androidx.** { *; }
# support-v7
-dontwarn android.support.v7.**                                             #去掉警告

#----------------保护指定的类和类的成员，但条件是所有指定的类和类成员是要存在------------------------------------
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保持自定义控件类不被混淆，指定格式的构造方法不去混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保持自定义控件类不被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
    *** get*();
}

# 保留在Activity中的方法参数是View的方法
# 从而我们在layout里边编写onClick就不会被影响
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# 保留枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 不混淆资源类
-keepclassmembers class **.R$* { *; }

# 对于带有回调函数onXXEvent()的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
}


-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}
# 对WebView的处理
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.

#-keepattributes Signature

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.** { *;}

# Application classes that will be serialized/deserialized over Gson
-keep class com.antew.redditinpictures.library.imgur.** { *; }
-keep class com.antew.redditinpictures.library.reddit.** { *; }

##---------------End: proguard configuration for Gson  ----------


#ViewBinding
-keepclassmembers class ** implements androidx.viewbinding.ViewBinding {
    public static ** bind(***);
    public static ** inflate(***);
}

#ROOM
-keep class com.speraxsport.app.common.dao.** { *;}

# 保持 Room 的注解不被混淆
-keepattributes *Annotation*

# 保持 Room 的注解处理类
-keep class androidx.room.** { *; }
-keep class androidx.sqlite.db.** { *; }

# 保持带有 @Entity 注解的实体类
-keep @androidx.room.Entity class * { *; }

# 保持带有 @Dao 注解的接口
-keep @androidx.room.Dao interface * { *; }

# 保持带有 @Database 注解的类
-keep @androidx.room.Database class * { *; }

# 保持带有 @TypeConverter 注解的方法
-keep @androidx.room.TypeConverters class * { *; }

# 保持 Room 生成的类的访问权限，避免混淆
-keep class * extends androidx.room.RoomDatabase { *; }

-keep class android.database.** { *; }
-keep class android.database.sqlite.** { *; }

# 保持 Kotlin 数据类中的方法不被混淆
-keepclassmembers class ** {
    *** toString();
    *** hashCode();
    *** equals(...);
}




-keep class fit.staytrue.android.data.bean.**{*;}
-keep class fit.staytrue.android.data.api.resp.**{*;}
-keep class fit.staytrue.android.data.api.req.**{*;}
-keep class fit.staytrue.android.app.bean.**{*;}
-keep class fit.staytrue.android.app.event.**{*;}






