package com.rohith.mycustompopupmenu

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView

class CustomPopupWindow(private val context: Context) : PopupWindow(context){
    private lateinit var recyclerView: RecyclerView
    private lateinit var customPopupAdapter: CustomPopupAdapter
    private var itemList : List<CustomItem> = mutableListOf()

    init {
        //setUpView()
    }


    fun setUpView() : View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_custom_list, null)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        customPopupAdapter = CustomPopupAdapter(context)
        recyclerView.adapter = customPopupAdapter
//        height = ViewGroup.LayoutParams.WRAP_CONTENT
//        width = ViewGroup.LayoutParams.WRAP_CONTENT
//        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        elevation = context.resources.getDimension(R.dimen.elevation)
//        isFocusable = true
//        isOutsideTouchable = true
        //contentView = view
        return view

    }

    fun setItemSelectionListener(delegate: CustomPopupAdapter.RecyclerviewCallbacks<CustomItem>) {
        customPopupAdapter.setOnClick(delegate)
    }

    fun setListItem(items: List<CustomItem>){
        this.itemList = items.toMutableList()
        customPopupAdapter.addCustomItem(itemList)
    }

}