package com.fkdeepal.tools.ext.base

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.fkdeepal.tools.ext.utils.ViewClickUtils
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), View.OnClickListener {
    protected var TAG = this.javaClass.simpleName
    protected val mActivity by lazy { this }
    protected val mWrActivity by lazy { WeakReference<Activity>(mActivity) }
    protected val mViewBinding: VB by lazy { initViewBinding() }
    protected val mLifecycleOwner: LifecycleOwner by lazy { this }
    protected val mOnClickListener: View.OnClickListener by lazy { this }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            onBeforeSetContentLayout()
            setContentView(mViewBinding.root)
            supportActionBar?.setDisplayHomeAsUpEnabled(isDisplayHomeAsUpEnabled())
            kotlin.runCatching {
                initViews()
            }
                .onFailure {
                    throw it
                }
        } catch (e: Exception) {
            Log.e("Error",e.localizedMessage,e)
            Toast.makeText(this, "onCreate err:${e.localizedMessage}", Toast.LENGTH_LONG)
                .show()
        }
    }

    open fun onBeforeSetContentLayout() {

    }

    abstract fun initViewBinding(layoutInflater: LayoutInflater): VB?
    open fun isDisplayHomeAsUpEnabled(): Boolean = true
    abstract fun initViews()
    abstract fun onViewClick(v: View)

    //   abstract fun handleUiState(UIState:UiState)

    private fun initViewBinding(): VB {
        val viewBinding = initViewBinding(layoutInflater)
        if (viewBinding!=null){
            return viewBinding
        }
        val type = javaClass.genericSuperclass as ParameterizedType
        try {
            val clazz = type.actualTypeArguments[0] as Class<VB>
            val method = clazz.getMethod("inflate", LayoutInflater::class.java)
            return method.invoke(null, layoutInflater) as VB
        } catch (e: Exception) {

        }
        return initViewBinding(layoutInflater)!!
    }

    fun setOnClickListener(vararg views: View) {
        views.forEach {
            it.setOnClickListener(mOnClickListener)
        }
    }

    final override fun onClick(v: View?) {
        if (v != null && ViewClickUtils.isOnClickSafe()) {
            onViewClick(v)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}