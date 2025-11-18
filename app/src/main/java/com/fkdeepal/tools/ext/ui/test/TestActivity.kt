package com.fkdeepal.tools.ext.ui.test

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.fkdeepal.tools.ext.AmapFloatManager
import com.fkdeepal.tools.ext.BuildConfig
import com.fkdeepal.tools.ext.base.BaseActivity
import com.fkdeepal.tools.ext.databinding.ActivityHudBinding
import com.fkdeepal.tools.ext.databinding.ActivityTestBinding
import com.fkdeepal.tools.ext.databinding.FloatAmapInfoBinding
import com.fkdeepal.tools.ext.event.hud.HudCloseEvent
import com.fkdeepal.tools.ext.exts.toast
import com.fkdeepal.tools.ext.manager.UserDataManager
import com.fkdeepal.tools.ext.presentation.HudPresentation
import com.fkdeepal.tools.ext.receiver.AmapNaviGuideReceiver
import com.fkdeepal.tools.ext.ui.HudDisplayActivity
import com.fkdeepal.tools.ext.utils.SvgLoader
import com.jeremyliao.liveeventbus.LiveEventBus

class TestActivity : BaseActivity<ActivityTestBinding>() {
    private var mHudPresentation: HudPresentation? = null
    private val mDisplayManager by lazy { ContextCompat.getSystemService<DisplayManager>(this, DisplayManager::class.java) }
    private var mIsShowAmapInfo: Boolean = false

    companion object {

        fun startActivity(activity: Activity) {
            val intent = Intent(activity, TestActivity::class.java)
            activity.startActivity(intent)
        }
    }


    override fun initViews() {
        mViewBinding.apply {
            setOnClickListener(btnDisplayInfo, btnHudPresentation, btnHudActivity, btnHudCancel, btnAmap,
                               btnAmapHud, btnHud3, btnHud4)
            setOnClickListener(btnNaviStart, btnLogChange, btnSvgDebug)
            etDisplayIndex.setText(UserDataManager.getHudDisplayId()?.toString())
        }
        displayLogState()
    }

    fun displayLogState() {
        mViewBinding.btnLogChange.setText("日志记录(${if (AmapFloatManager.isLogEnable) "开" else "关"} )")
    }

    fun getHubDisplay(): Display? {
        val index = mViewBinding.etDisplayIndex.text.toString()
            .toIntOrNull()
        if (index == null) {
            toast("请填写副屏信息")
            return null
        }
        val display = mDisplayManager?.displays?.get(index)
        if (display == null) {
            toast("副屏index有误")
            return null
        }
        return display
    }

    fun startHudActivity(type: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!Settings.canDrawOverlays(mActivity)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 1)
                return
            }
            getHubDisplay()?.let {

                val options = ActivityOptions.makeBasic()
                options.setLaunchDisplayId(it.getDisplayId())
                val intent = Intent(mActivity, HudDisplayActivity::class.java)
                intent.putExtra("type", type)
                startActivity(intent, options.toBundle());
            }
        }
    }

    fun getCarInfo() {
        // 初始化 Car 服务
    }

    override fun initViewBinding(layoutInflater: LayoutInflater): ActivityTestBinding? = ActivityTestBinding.inflate(layoutInflater)
    
    override fun onViewClick(v: View) {
        mViewBinding.apply {
            when (v) {
                btnDisplayInfo -> {
                    mIsShowAmapInfo = false
                    mDisplayManager?.let {
                        val displays = it.getDisplays()
                        val stringBuilder = StringBuilder()
                        for (display in displays) {
                            val name = display.name
                            stringBuilder.append(display.displayId)
                                .append(" -- ")
                                .append(name)
                                .append("\n")
                            val metrics = DisplayMetrics();
                            display.getMetrics(metrics)
                            stringBuilder.append("      width:")
                                .append(display.width)
                                .append("\n")
                            stringBuilder.append("      height:")
                                .append(display.height)
                                .append("\n")
                            stringBuilder.append("      refreshRate:")
                                .append(display.refreshRate)
                                .append("\n")
                            stringBuilder.append("      density :")
                                .append(metrics.density)
                                .append("\n")
                            stringBuilder.append("      densityDpi  :")
                                .append(metrics.densityDpi)
                                .append("\n")
                            stringBuilder.append("      scaledDensity   :")
                                .append(metrics.scaledDensity)
                                .append("\n")
                            val supportedModes = display.getSupportedModes();
                            stringBuilder.append("      支持模式")
                                .append("\n")
                            for (mode in supportedModes) {
                                stringBuilder.append("" + mode.getPhysicalWidth() + "x" + mode.getPhysicalHeight() + " @ " + mode.getRefreshRate() + "Hz")
                                    .append("\n")
                            }
                        }
                        tvResult.setText(stringBuilder)
                    }

                }

                btnHudPresentation -> {
                    getHubDisplay()?.let {
                        mHudPresentation?.dismiss()
                        mHudPresentation = HudPresentation(applicationContext, it)
                        mHudPresentation?.show()
                    }
                }

                btnHudActivity -> {
                    startHudActivity(1)

                }

                btnHud3 -> {
                    startHudActivity(2)
                }

                btnHud4 -> {
                    startHudActivity(3)
                }

                btnLogChange -> {
                    AmapFloatManager.isLogEnable = !AmapFloatManager.isLogEnable
                    displayLogState()
                }

                btnSvgDebug -> {
                    // 测试SVG加载
                    val testIcons = listOf("1", "13", "38", "66", "89")
                    val result = StringBuilder()
                    testIcons.forEach { iconNumber ->
                        val success = SvgLoader.debugLoadLandIcon(mActivity, iconNumber)
                        result.append("ic_land_$iconNumber: ${if (success) "成功" else "失败"}\n")
                    }
                    toast("SVG调试完成，查看日志")
                    tvResult.setText(result.toString())
                }

                btnHudCancel -> {
                    mHudPresentation?.dismiss()
                    AmapFloatManager.hideHudFloat()
                    LiveEventBus.get(HudCloseEvent.KEY, HudCloseEvent::class.java)
                        .post(HudCloseEvent(1))
                }

                btnAmapHud -> {
                    getHubDisplay()?.let {
                        val id = it.displayId
                        val intent = Intent();
                        intent.setAction("AUTONAVI_STANDARD_BROADCAST_RECV");
                        intent.putExtra("KEY_TYPE", 10104);
                        intent.putExtra("EXTRA_EXTERNAL_ENGINE_ID", id);
                        // 仪表模式   0 : 2D车首上 1 : 2D北首上  2 : 3D车首上 其它值表示取消固定
                        intent.putExtra("EXTRA_EXTERNAL_MAP_MODE", 2);
                        // 仪表车标位置 1 : 左侧  2 : 居中 3 : 右侧  其它值表示取消固定
                        intent.putExtra("EXTRA_EXTERNAL_MAP_POSITION", 1)
                        // 仪表比例尺级别（0~17）
                        intent.putExtra("EXTRA_EXTERNAL_MAP_LEVEL", 17)
                        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        sendBroadcast(intent)

                        toast("已发送广播")
                        // 开启路况大图
                        v.postDelayed({
                                          val intent = Intent()
                                          intent.setAction("AUTONAVI_STANDARD_BROADCAST_RECV")
                                          intent.putExtra("KEY_TYPE", 10105)
                                          intent.putExtra("EXTRA_EXTERNAL_ENGINE_ID", 3)
                                          intent.putExtra("EXTRA_EXTERNAL_CROSS_CONTROL", true)
                                          intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                          sendBroadcast(intent)
                                      }, 2000)
                    }


                }

                btnAmap -> {
                    mIsShowAmapInfo = !mIsShowAmapInfo
                    if (!mIsShowAmapInfo) {
                        mViewBinding.tvResult.setText("")
                        unregisterReceiver()
                    } else {
                        mViewBinding.tvResult.setText("开始监听")
                        registerAMapReceiver()
                    }
                }

                btnNaviStart -> {
                    val intent = Intent()
                    intent.setAction("AUTONAVI_STANDARD_BROADCAST_RECV")
                    intent.putExtra("KEY_TYPE", 10076)
                    intent.putExtra("EXTRA_SLAT", 24.496706)
                    intent.putExtra("EXTRA_SLON", 118.182682)
                    intent.putExtra("EXTRA_SNAME", "佰翔软件园酒店")
                    intent.putExtra("EXTRA_FMIDLAT", 24.492793)
                    intent.putExtra("EXTRA_FMIDLON", 118.162947)
                    intent.putExtra("EXTRA_FMIDNAME", "蔡塘")
                    intent.putExtra("EXTRA_SMIDLAT", 24.483256)
                    intent.putExtra("EXTRA_SMIDLON", 118.148825)
                    intent.putExtra("EXTRA_SMIDNAME", "太川大楼")
                    intent.putExtra("EXTRA_TMIDLAT", 24.47658)
                    intent.putExtra("EXTRA_TMIDLON", 118.163917)
                    intent.putExtra("EXTRA_TMIDNAME", "世界山庄")
                    intent.putExtra("EXTRA_DLAT", 24.453688)
                    intent.putExtra("EXTRA_DLON", 118.17581)
                    intent.putExtra("EXTRA_DNAME", "椰风寨")
                    intent.putExtra("EXTRA_DEV", 0)
                    intent.putExtra("EXTRA_M", 0)
                    intent.putExtra("KEY_RECYLE_SIMUNAVI", true)
                    sendBroadcast(intent)
                    v.postDelayed({
                                      val intent2 = Intent()
                                      intent2.setAction("AUTONAVI_STANDARD_BROADCAST_RECV")
                                      intent2.putExtra("KEY_TYPE", 10031)
                                      sendBroadcast(intent2)
                                  }, 1000)
                }
            }
        }
    }

    private var mHudFloatViewBinding: ActivityHudBinding? = null
    fun hideHudFloat() {
        mHudFloatViewBinding?.let {
            windowManager.removeView(it.root)
        }
        mHudFloatViewBinding = null
    }

    fun showHudFloatView() {
        getHubDisplay()?.let {
            hideHudFloat()

            mHudFloatViewBinding = ActivityHudBinding.inflate(LayoutInflater.from(applicationContext))
            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // 窗口类型
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            );
            val view = mHudFloatViewBinding!!.root
            windowManager.addView(view, layoutParams)
        }

    }

    private var floatAmapInfoBinding: FloatAmapInfoBinding? = null
    private fun hideAmapInfoFloat() {
        floatAmapInfoBinding?.let {
            windowManager.removeView(it.root)
        }
        floatAmapInfoBinding = null
    }

    private fun showAmapInfoFloat() {
        hideAmapInfoFloat()
        floatAmapInfoBinding = FloatAmapInfoBinding.inflate(layoutInflater, null, false)
        var layoutParam = WindowManager.LayoutParams()
            .apply {
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE
                }
                format = PixelFormat.RGBA_8888
                flags =
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                //位置大小设置
                width = 300
                height = 400
                gravity = Gravity.RIGHT or Gravity.TOP
                //设置剧中屏幕显示
                //x = 0
                y = 150
            }
        windowManager.addView(floatAmapInfoBinding!!.root, layoutParam)
    }

    var amapNaviGuideReceiver: AmapNaviGuideReceiver? = null

    /**
     * 注册监听高德广播
     */
    private fun registerAMapReceiver() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 1)
            return
        }
        amapNaviGuideReceiver = AmapNaviGuideReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(AmapNaviGuideReceiver.ACTION_NAVI_GUIDE)
        //intentFilter.addAction(SEND_ACTION)
        unregisterReceiver()
        ContextCompat.registerReceiver(mActivity, amapNaviGuideReceiver, intentFilter, ContextCompat.RECEIVER_EXPORTED) //再进行监听
        showAmapInfoFloat()

    }

    fun unregisterReceiver() {
        try {
            if (amapNaviGuideReceiver != null) {
                unregisterReceiver(amapNaviGuideReceiver); //先取消监听
            }
        } catch (e: Exception) {
        }
    }
}
