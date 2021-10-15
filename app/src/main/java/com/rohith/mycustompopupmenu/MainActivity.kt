package com.rohith.mycustompopupmenu

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.rohith.mycustompopupmenu.databinding.ActivityMainBinding
import com.rohith.mycustompopupmenu.secondKit.OverflowMenuFactory
import com.rohith.mycustompopupmenu.secondKit.PaytmOverflowMenu
import com.rohith.mycustompopupmenu.secondKit.PopupMenuUtil.PopupMenuAlignment


class MainActivity : AppCompatActivity() {
    private var popupWindow: CustomPopupWindow? = null
    private var customPopupMenu : PaytmOverflowMenu? = null

    private var recyclerView: RecyclerView? = null
    private lateinit var customPopupAdapter: CustomPopupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.toolbarList.setOnClickListener {
            customPopupMenu = createPopupMenu(PopupMenuAlignment.TOPRIGHT, 5, true) // TOPRIGHT
            customPopupMenu?.show(it, 0, 0, PopupMenuAlignment.TOPRIGHT)
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.top_left -> {
                customPopupMenu = createPopupMenu(PopupMenuAlignment.TOPLEFT, 1, true) // TOPLEFT
                customPopupMenu?.show(view, 0, 0, PopupMenuAlignment.TOPLEFT)
            }
            R.id.top_right -> {
                customPopupMenu = createPopupMenu(PopupMenuAlignment.TOPRIGHT, 2, true) // TOPRIGHT
                customPopupMenu?.show(view, 0, 0, PopupMenuAlignment.TOPRIGHT)
            }
            R.id.bottom_left -> {
                customPopupMenu = createPopupMenu(PopupMenuAlignment.BOTTOMLEFT, 3, true)   // BOTTOMLEFT
                customPopupMenu?.show(view, 0, 0, PopupMenuAlignment.BOTTOMLEFT)
            }
            R.id.bottom_right -> {
                customPopupMenu = createPopupMenu(PopupMenuAlignment.BOTTOMRIGHT, 4, true) // BOTTOMRIGHT
                customPopupMenu?.show(view, 0, 0, PopupMenuAlignment.BOTTOMRIGHT)
            }
            R.id.center ->{
//                customListPaytmOverflowMenu = createPopupMenu(ArrowOrientation.BOTTOM)   // BOTTOMLEFT
//                customListPaytmOverflowMenu?.showAtCenter(view, 0, 0, PopupMenuCenterAlign.BOTTOMLEFT)

                customPopupMenu = createPopupMenu(PopupMenuAlignment.TOPLEFT, 10, true) // TOPLEFT
                customPopupMenu?.show(view, 0, 0, PopupMenuAlignment.TOPLEFT)
            }

            R.id.center2 ->{
//                customListPaytmOverflowMenu = createPopupMenu(ArrowOrientation.TOP) // TOPRIGHT
//                customListPaytmOverflowMenu?.showAtCenter(view, 0, 0, PopupMenuCenterAlign.TOPRIGHT)

                customPopupMenu = createPopupMenu(PopupMenuAlignment.BOTTOMRIGHT, 5, true) // BOTTOMRIGHT
                customPopupMenu?.show(view, 0, 0, PopupMenuAlignment.BOTTOMRIGHT)
            }


            R.id.maxHeight ->{
//                customListPaytmOverflowMenu = createPopupMenu(ArrowOrientation.TOP) // TOPRIGHT
//                customListPaytmOverflowMenu?.showAtCenter(view, 0, 0, PopupMenuCenterAlign.TOPRIGHT)

                customPopupMenu = createPopupMenu(PopupMenuAlignment.TOPLEFT, 15, true) // TOPLEFT
                customPopupMenu?.show(view, 0, 0, PopupMenuAlignment.TOPLEFT)
            }
        }
    }



    fun createPopupMenu(value : PopupMenuAlignment, itemsCount : Int, withIcons : Boolean) : PaytmOverflowMenu{
        customPopupMenu?.dismiss()
        customPopupMenu = OverflowMenuFactory.getInstance(this, this, value)

        recyclerView =
            customPopupMenu?.getContentView()?.findViewById(R.id.recyclerView)
        customPopupAdapter = CustomPopupAdapter(object : CustomPopupAdapter.RecyclerviewCallbacks<CustomItem>{
            override fun onCustomItemClick(view: View, position: Int, item: CustomItem) {
                Toast.makeText(this@MainActivity, "data = $item", Toast.LENGTH_SHORT).show()
                //dismissPopup()
                customPopupMenu?.dismiss()
            }

        })
        recyclerView?.adapter = customPopupAdapter

        if (withIcons){
            customPopupAdapter.addCustomItem(getCustomSamplesN(this, itemsCount))
        }else{
            customPopupAdapter.addCustomItem(getCustomSamplesWithoutIconsN(this, itemsCount))
        }

        return customPopupMenu as PaytmOverflowMenu
    }


    private fun showPopup(view: View, textView: String){


//        dismissPopup()
//        popupWindow = CustomPopupWindow(this)
//        if (withIcons){
//            popupWindow?.setListItem(getCustomSamples(this))
//        }else{
//            popupWindow?.setListItem(getCustomSamplesWithoutIcons(this))
//        }
//        popupWindow?.showAsDropDown(view)
//        popupWindow?.setItemSelectionListener(object : CustomPopupAdapter.RecyclerviewCallbacks<CustomItem>{
//            override fun onCustomItemClick(view: View, position: Int, item: CustomItem) {
//                Toast.makeText(this@MainActivity, "data = $item", Toast.LENGTH_SHORT).show()
//                dismissPopup()
//            }
//
//        })

//        var gravity : Int? = null
//
//        gravity = when (textView) {
//            "Right" -> {
//                Gravity.END
//            }
//            "Left" -> {
//                Gravity.START
//            }
//            "Top" -> {
//                Gravity.TOP
//            }
//            else -> {
//                Gravity.BOTTOM
//            }
//        }

//            SimpleTooltip.Builder(this)
//                .anchorView(view)
//                .gravity(gravity)
//                .maxWidth(R.dimen.maxWidth)
//                .setListItems(getCustomSamples(this))
//                .build()
//                .show()

        // gets customListBalloon's recyclerView.


        //editBalloon.showAlignTop(it, 0, -30)

    }


    override fun onStop() {
        super.onStop()
        dismissPopup()
    }

    private fun dismissPopup() {
        popupWindow?.let {
            if(it.isShowing){
                it.dismiss()
            }
            popupWindow = null
        }
    }

}