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
    private var customListPaytmOverflowMenu : PaytmOverflowMenu? = null

    private var recyclerView: RecyclerView? = null
    private lateinit var customPopupAdapter: CustomPopupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.toolbarList.setOnClickListener {
            customListPaytmOverflowMenu = createPopupMenu(PopupMenuAlignment.TOPRIGHT) // TOPRIGHT
            customListPaytmOverflowMenu?.show(it, 0, 0, PopupMenuAlignment.TOPRIGHT)
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.top_left -> {
                customListPaytmOverflowMenu = createPopupMenu(PopupMenuAlignment.TOPLEFT) // TOPLEFT
                customListPaytmOverflowMenu?.show(view, 0, 0, PopupMenuAlignment.TOPLEFT)
            }
            R.id.top_right -> {
                customListPaytmOverflowMenu = createPopupMenu(PopupMenuAlignment.TOPRIGHT) // TOPRIGHT
                customListPaytmOverflowMenu?.show(view, 0, 0, PopupMenuAlignment.TOPRIGHT)
            }
            R.id.bottom_left -> {
                customListPaytmOverflowMenu = createPopupMenu(PopupMenuAlignment.BOTTOMLEFT)   // BOTTOMLEFT
                customListPaytmOverflowMenu?.show(view, 0, 0, PopupMenuAlignment.BOTTOMLEFT)
            }
            R.id.bottom_right -> {
                customListPaytmOverflowMenu = createPopupMenu(PopupMenuAlignment.BOTTOMRIGHT) // BOTTOMRIGHT
                customListPaytmOverflowMenu?.show(view, 0, 0, PopupMenuAlignment.BOTTOMRIGHT)
            }
            R.id.center ->{
//                customListPaytmOverflowMenu = createPopupMenu(ArrowOrientation.BOTTOM)   // BOTTOMLEFT
//                customListPaytmOverflowMenu?.showAtCenter(view, 0, 0, PopupMenuCenterAlign.BOTTOMLEFT)

                customListPaytmOverflowMenu = createPopupMenu(PopupMenuAlignment.TOPLEFT) // TOPLEFT
                customListPaytmOverflowMenu?.show(view, 0, 0, PopupMenuAlignment.TOPLEFT)
            }

            R.id.center2 ->{
//                customListPaytmOverflowMenu = createPopupMenu(ArrowOrientation.TOP) // TOPRIGHT
//                customListPaytmOverflowMenu?.showAtCenter(view, 0, 0, PopupMenuCenterAlign.TOPRIGHT)

                customListPaytmOverflowMenu = createPopupMenu(PopupMenuAlignment.BOTTOMRIGHT) // BOTTOMRIGHT
                customListPaytmOverflowMenu?.show(view, 0, 0, PopupMenuAlignment.BOTTOMRIGHT)
            }
        }
    }



    fun createPopupMenu(value : PopupMenuAlignment) : PaytmOverflowMenu{
        customListPaytmOverflowMenu?.dismiss()
        customListPaytmOverflowMenu = OverflowMenuFactory.getInstance(this, this, value)

        recyclerView =
            customListPaytmOverflowMenu?.getContentView()?.findViewById(R.id.recyclerView)
        customPopupAdapter = CustomPopupAdapter(object : CustomPopupAdapter.RecyclerviewCallbacks<CustomItem>{
            override fun onCustomItemClick(view: View, position: Int, item: CustomItem) {
                Toast.makeText(this@MainActivity, "data = $item", Toast.LENGTH_SHORT).show()
                //dismissPopup()
                customListPaytmOverflowMenu?.dismiss()
            }

        })
        recyclerView?.adapter = customPopupAdapter

        customPopupAdapter.addCustomItem(getCustomSamples(this))
        return customListPaytmOverflowMenu as PaytmOverflowMenu
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