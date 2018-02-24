# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\as\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-keepattributes EnclosingMethod
-keepattributes InnerClasses
#-dontoptimize

#################极光推送#################
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

#==================gson && protobuf==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
-keep class com.google.protobuf.** {*;}

#########混淆实体类和json类########
-keep class com.mobileoffice.entity.** {*;}
-keep class com.mobileoffice.json.** {*;}

#######################融云混淆#######################
-keepattributes Exceptions,InnerClasses

-keepattributes Signature

# RongCloud SDK
-keep class io.rong.** {*;}
-keep class * implements io.rong.imlib.model.MessageContent {*;}
-dontwarn io.rong.push.**
-dontnote com.xiaomi.**
-dontnote com.google.android.gms.gcm.**
-dontnote io.rong.**

# VoIP
-keep class io.agora.rtc.** {*;}

# Location
-keep class com.amap.api.**{*;}
-keep class com.amap.api.services.**{*;}

# 红包
-keep class com.google.gson.** { *; }
-keep class com.uuhelper.Application.** {*;}
-keep class net.sourceforge.zbar.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.alipay.** {*;}
-keep class com.jrmf360.rylib.** {*;}

#自己写的
#-keep com.mobileoffice.controller.message.** { *; }

-ignorewarnings

#自定义的服务
-keep class com.mobileoffice.controller.send.RongYunReceiver {*;}

############高德地图###############

#-keep class com.acker.simplezxing.** {*;}
 #   3D 地图 V5.0.0之前：
#    -keep   class com.amap.api.maps.**{*;}
#    -keep   class com.autonavi.amap.mapcore.*{*;}
#    -keep   class com.amap.api.trace.**{*;}

 #   3D 地图 V5.0.0之后：
    -keep   class com.amap.api.maps.**{*;}
    -keep   class com.autonavi.**{*;}
    -keep   class com.amap.api.trace.**{*;}

 #   定位
    -keep class com.amap.api.location.**{*;}
    -keep class com.amap.api.fence.**{*;}
    -keep class com.autonavi.aps.amapapi.model.**{*;}

  #  搜索
#    -keep   class com.amap.api.services.**{*;}

  #  2D地图
#    -keep class com.amap.api.maps2d.**{*;}
#    -keep class com.amap.api.mapcore2d.**{*;}

  #  导航
#    -keep class com.amap.api.navi.**{*;}
#    -keep class com.autonavi.**{*;}


##################################通用混淆################################
##指定压缩级别
-optimizationpasses 5
#
##不跳过非公共的库的类成员
#-dontskipnonpubliclibraryclassmembers
#
##混淆时采用的算法
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#
##把混淆类中的方法名也混淆了
-useuniqueclassmembernames
#
##优化时允许访问并修改有修饰符的类和类的成员
#-allowaccessmodification
#
##将文件来源重命名为“SourceFile”字符串
#-renamesourcefileattribute SourceFile
##保留行号
-keepattributes SourceFile,LineNumberTable
##保持泛型
#-keepattributes Signature
#
##保持所有实现 Serializable 接口的类成员
#-keepclassmembers class * implements java.io.Serializable {
#    static final long serialVersionUID;
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}
#
##Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
#-keep public class * extends android.support.v4.app.Fragment
#-keep public class * extends android.app.Fragment
#
## 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**


################################qqbugly####################
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}


################################Retrofit####################
# Retrofit
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

 -keep class rx.**{*;}
# okhttp
-dontwarn okio.**

################################eventBus####################
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

###################阿里热修复#########
#基线包使用，生成mapping.txt
-printmapping mapping.txt
#生成的mapping.txt在app/buidl/outputs/mapping/release路径下，移动到/app路径下
#修复后的项目使用，保证混淆结果一致
#-applymapping mapping.txt
#hotfix
-keep class com.taobao.sophix.**{*;}
-keep class com.ta.utdid2.device.**{*;}
#防止inline
-dontoptimize

###################xutils3#########
-keep class org.xutils.** { *; }

###################glide#########
 -keep public class * implements com.bumptech.glide.module.GlideModule
    -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
      **[] $VALUES;
      public *;
    }

    ##########动态加载gif#######
    -keep class pl.droidsonroids.gif.** {*;}

    ######小米推送#########
    #这里com.xiaomi.mipushdemo.DemoMessageRreceiver改成app中定义的完整类名
    -keep class com.xiaomi.mipush.sdk.DemoMessageReceiver {*;}
    #可以防止一个误报的 warning 导致无法成功编译，如果编译使用的 Android 版本是 23。
    -dontwarn com.xiaomi.push.**

    #########华为推送#########
#    -ignorewarning
#
#    -keepattributes *Annotation*
#    -keepattributes Exceptions
#    -keepattributes InnerClasses
#    -keepattributes Signature
#    -keepattributes SourceFile,LineNumberTable
#
#    -keep class com.huawei.android.**{*;}

#-ignorewarning
#-keepattributes *Annotation*
#-keepattributes Exceptions
#-keepattributes InnerClasses
#-keepattributes Signature
#-keepattributes SourceFile,LineNumberTable
#-keep class com.hianalytics.android.**{*;}
#-keep class com.huawei.updatesdk.**{*;}
#-keep class com.huawei.hms.**{*;}

-ignorewarning
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

-keep class com.huawei.gamebox.plugin.gameservice.**{*;}

-keep public class com.huawei.android.hms.agent.** extends android.app.Activity { public *; protected *; }
-keep interface com.huawei.android.hms.agent.common.INoProguard {*;}
-keep class * extends com.huawei.android.hms.agent.common.INoProguard {*;}