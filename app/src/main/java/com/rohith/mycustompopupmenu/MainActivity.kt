package com.rohith.mycustompopupmenu

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private var popupWindow: CustomPopupWindow? = null
    private var withIcons : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn1 -> {
                showPopup(view, (view as Button).text.toString())
            }
            R.id.btn2 -> {
                showPopup(view, (view as Button).text.toString())
            }
            R.id.btn3 -> {
                showPopup(view, (view as Button).text.toString())
            }
            else -> {
                showPopup(view, (view as Button).text.toString())
            }
        }
    }


    private fun showPopup(view: View, textView: String){


        dismissPopup()
        popupWindow = CustomPopupWindow(this)
        if (withIcons){
            popupWindow?.setListItem(getCustomSamples(this))
        }else{
            popupWindow?.setListItem(getCustomSamplesWithoutIcons(this))
        }
        popupWindow?.showAsDropDown(view)
        popupWindow?.setItemSelectionListener(object : CustomPopupAdapter.RecyclerviewCallbacks<CustomItem>{
            override fun onCustomItemClick(view: View, position: Int, item: CustomItem) {
                Toast.makeText(this@MainActivity, "data = $item", Toast.LENGTH_SHORT).show()
                dismissPopup()
            }

        })

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