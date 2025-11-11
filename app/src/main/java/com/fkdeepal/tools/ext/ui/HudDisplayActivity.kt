package com.fkdeepal.tools.ext.ui

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fkdeepal.tools.ext.AmapFloatManager
import com.fkdeepal.tools.ext.BuildConfig
import com.fkdeepal.tools.ext.R
import com.fkdeepal.tools.ext.base.BaseActivity
import com.fkdeepal.tools.ext.databinding.ActivityHudBinding
import com.fkdeepal.tools.ext.event.hud.HudCloseEvent
import com.fkdeepal.tools.ext.exts.toast
import com.fkdeepal.tools.ext.manager.UserDataManager
import com.jeremyliao.liveeventbus.LiveEventBus

class HudDisplayActivity : AppCompatActivity() {
    companion object{
        fun startActivity(context: Context,launchDisplayId:Int){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O   ) {
                val options = ActivityOptions.makeBasic()
                options.setLaunchDisplayId(launchDisplayId)
                val intent = Intent(context, HudDisplayActivity::class.java)
                if (context is Activity){

                }else{
                    intent. addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent, options.toBundle());
            }
        }

        fun startActivity(context: Context){
            val launchDisplayId =   UserDataManager.getHudDisplayId()
            if (launchDisplayId == null){
                context.toast("请先到设置页面中设置 投屏屏幕ID ")
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O   &&   Settings.canDrawOverlays(context)) {
                val options = ActivityOptions.makeBasic()
                options.setLaunchDisplayId(launchDisplayId)
                val intent = Intent(context, HudDisplayActivity::class.java)
                if (context is Activity){

                }else{
                    intent. addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                intent.putExtra("type",1)
                context.startActivity(intent, options.toBundle());
            }

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hud)
        Log.d("HudDisplayActivity", "create")
        val type = intent.getIntExtra("type", 1)
        when (type) {

            2 -> AmapFloatManager.showFloat(this, null, -1f)
            3 -> AmapFloatManager.showFloat(this, -1f, -1f)
            else -> AmapFloatManager.showFloat(this)
        }
        Handler().postDelayed({ finish() }, 2000)
    }
}