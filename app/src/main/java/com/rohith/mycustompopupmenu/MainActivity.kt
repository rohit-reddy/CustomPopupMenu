package com.rohith.mycustompopupmenu

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.rohith.mycustompopupmenu.secondKit.OverflowMenuFactory
import com.rohith.mycustompopupmenu.secondKit.PaytmOverflowMenu
import com.rohith.mycustompopupmenu.secondKit.util.ArrowOrientation
import com.rohith.mycustompopupmenu.secondKit.util.PopupMenuCenterAlign


class MainActivity : AppCompatActivity() {
    private var popupWindow: CustomPopupWindow? = null
    private var customListPaytmOverflowMenu : PaytmOverflowMenu? = null

    private var recyclerView: RecyclerView? = null
    private lateinit var customPopupAdapter: CustomPopupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.top_left -> {
                customListPaytmOverflowMenu = createPopupMenu(ArrowOrientation.TOP)
                customListPaytmOverflowMenu?.showAlignBottom(view, 30, 36) // arrow will be pointing to top, container will be at the bottom
            }
            R.id.top_right -> {
                customListPaytmOverflowMenu = createPopupMenu(ArrowOrientation.TOP)
                customListPaytmOverflowMenu?.showAlignBottom(view, 0, 36)
            }
            R.id.bottom_left -> {
                customListPaytmOverflowMenu = createPopupMenu(ArrowOrientation.BOTTOM)
                customListPaytmOverflowMenu?.showAlignTop(view, 0, -30)
            }
            R.id.bottom_right -> {
                customListPaytmOverflowMenu = createPopupMenu(ArrowOrientation.BOTTOM)
                customListPaytmOverflowMenu?.showAlignTop(view, 0, 0)
            }
            R.id.center ->{
//                customListPaytmOverflowMenu = createPopupMenu(ArrowOrientation.BOTTOM)   // BOTTOMLEFT
//                customListPaytmOverflowMenu?.showAtCenter(view, 0, 0, PopupMenuCenterAlign.TOP)

                customListPaytmOverflowMenu = createPopupMenu(ArrowOrientation.TOP) // TOPLEFT
                customListPaytmOverflowMenu?.showAtCenter(view, 0, 0, PopupMenuCenterAlign.BOTTOM)
            }

            R.id.center2 ->{
//                customListPaytmOverflowMenu = createPopupMenu(ArrowOrientation.TOP) // TOPRIGHT
//                customListPaytmOverflowMenu?.showAtCenter(view, 0, 0, PopupMenuCenterAlign.END)

                customListPaytmOverflowMenu = createPopupMenu(ArrowOrientation.BOTTOM) // BOTTOMRIGHT
                customListPaytmOverflowMenu?.showAtCenter(view, 0, 0, PopupMenuCenterAlign.START)
            }
        }
    }



    fun createPopupMenu(value : ArrowOrientation) : PaytmOverflowMenu{
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