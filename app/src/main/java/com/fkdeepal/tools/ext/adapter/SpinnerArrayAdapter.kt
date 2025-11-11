package com.fkdeepal.tools.ext.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

class SpinnerArrayAdapter<T>(val mContext: Context,
                             val data: List<T>,
                             val dataTransformFunc: (T) -> String,
                             @LayoutRes  val   resource: Int = android.R.layout.simple_spinner_dropdown_item,
                              @IdRes val textViewResourceId: Int = 0 ) : BaseAdapter() {
    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        return createViewFromResource(mInflater, position, convertView, parent, resource);
    }

    private fun createViewFromResource(inflater: LayoutInflater, position: Int,
                                       convertView: View?, parent: ViewGroup?, resource: Int): View {
        val view: View?
        val text: TextView

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false)
        } else {
            view = convertView
        }

        try {
            if (textViewResourceId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                text = view as TextView
            } else {
                //  Otherwise, find the TextView field within the layout
                text = view.findViewById<TextView?>(textViewResourceId)

                if (text == null) {
                    throw RuntimeException(("Failed to find view with ID "
                            + mContext.getResources()
                        .getResourceName(textViewResourceId)
                            + " in item layout"))
                }
            }
        } catch (e: ClassCastException) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView")
            throw IllegalStateException(
                "ArrayAdapter requires the resource ID to be a TextView", e)
        }

        val item  = getItem(position)
        if (item==null){

        }else{
            if (item is CharSequence) {
                text.setText(item as CharSequence)
            } else {
                text.setText(dataTransformFunc.invoke(item))
            }
        }


        return view
    }
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): T? {
        val item = data.getOrNull(position)

        return item
    }
}