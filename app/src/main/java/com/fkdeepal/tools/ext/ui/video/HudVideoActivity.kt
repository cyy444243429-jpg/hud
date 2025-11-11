package com.fkdeepal.tools.ext.ui.video

import android.app.Activity
import android.content.Intent
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Build
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.GridLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.fkdeepal.tools.ext.adapter.SpinnerArrayAdapter
import com.fkdeepal.tools.ext.base.BaseActivity
import com.fkdeepal.tools.ext.databinding.ActivityHudVideoBinding
import com.fkdeepal.tools.ext.exts.toast
import com.fkdeepal.tools.ext.ui.test.TestActivity
import kotlin.toString

class HudVideoActivity : BaseActivity<ActivityHudVideoBinding>(){
    private val mDisplayManager by lazy { ContextCompat.getSystemService<DisplayManager>(this, DisplayManager::class.java) }
    private val mDisplayData = arrayListOf<Display>()
    private var mVideoMediaPicker: ActivityResultLauncher<PickVisualMediaRequest>? = null
    private val mDisplayDataAdapter by lazy {
        SpinnerArrayAdapter(mActivity,mDisplayData,{
            "${it.displayId} - ${it.width}x${it.height}"
        })
    }
    private val mVideoPickActivityResult= registerForActivityResult (ActivityResultContracts.GetContent()){uri->
        if (uri!=null) {
            onFileSelected(uri)
        }
    }
    companion object{
        var videoPresentation:VideoPresentation?=null

            fun startActivity(activity: Activity) {
                val intent = Intent(activity, HudVideoActivity::class.java)
                activity.startActivity(intent)
            }
    }
    override fun initViewBinding(layoutInflater: LayoutInflater): ActivityHudVideoBinding?  = ActivityHudVideoBinding.inflate(layoutInflater)
    override fun initViews() {
        mVideoMediaPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it == null) {

            } else {
             onFileSelected(it)
            }
        }
        mViewBinding.apply {
          val  childCount =  layoutGrid.childCount
            for (i in 0 until childCount) {
                runCatching {
                    val view = layoutGrid.getChildAt(i)
                    if (view!=null){
                        val layoutParams =  view.layoutParams
                        if (layoutParams is GridLayout.LayoutParams){
                            layoutParams.setMargins(16,8,16,8)
                            view.layoutParams = layoutParams
                        }
                    }
                }
            }
            setOnClickListener(btnOpenFile,btnPlay,btnStop,btnPause,btnClose)
            spDisplay.adapter = mDisplayDataAdapter
            spDisplay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
        getDisplayInfo()
    }
    fun onFileSelected(uri: Uri){
        if (videoPresentation == null || !videoPresentation!!.isShowing){
            val display = mDisplayData.getOrNull(mViewBinding.spDisplay.selectedItemPosition)
            display?.let {
                videoPresentation = VideoPresentation(mActivity,display)
                videoPresentation?.show()
            }
        }
        videoPresentation?.setVideoUrl(uri)
    }
    fun getDisplayInfo(){

        mDisplayManager?.let {
            mDisplayData.clear()
            mDisplayData.addAll(it.displays)
            mDisplayDataAdapter.notifyDataSetChanged()
            mDisplayData.forEachIndexed { index, display ->
                if (display.width == 800 && display.height==480){
                    mViewBinding.spDisplay.setSelection(index)
                }
            }
        }
    }
    override fun onViewClick(v: View) {
        mViewBinding.apply {
            when(v){
                btnClose->{
                    videoPresentation?.dismiss()
                    videoPresentation = null
                }
                btnOpenFile ->{
                    if (mDisplayData.size<=1){
                        toast("无副屏")
                        return
                    }
                    val display = mDisplayData.getOrNull(spDisplay.selectedItemPosition)
                    if (display ==null){
                        toast("请选择正确的副屏")
                    }else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            mVideoMediaPicker?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                        }else{
                            try {
                                mVideoPickActivityResult.launch("video/*")
                            }catch (e: Exception){
                                try {
                                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                                    intent.setType("video/*")
                                    startActivityForResult(intent,1)
                                }catch (e: Exception){

                                }
                            }

                        }
                    }

                }
                btnPlay ->{
                    videoPresentation?.resume()
                }
                btnPause ->{
                    videoPresentation?.pause()
                }
            }
        }
    }
}