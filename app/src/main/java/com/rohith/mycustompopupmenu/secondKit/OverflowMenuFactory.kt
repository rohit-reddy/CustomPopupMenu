package com.rohith.mycustompopupmenu.secondKit

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.rohith.mycustompopupmenu.R
import com.rohith.mycustompopupmenu.secondKit.PopupMenuUtil.ArrowPositionRules
import com.rohith.mycustompopupmenu.secondKit.PopupMenuUtil.PopupMenuAlignment
import com.rohith.mycustompopupmenu.secondKit.PopupMenuUtil.PopupMenuSizeSpec

//class OverflowMenuFactory : PaytmOverflowMenu.Factory() {
class OverflowMenuFactory {

    companion object {
        private var instance: PaytmOverflowMenu? = null

        fun getInstance(
            context: Context,
            lifecycle: LifecycleOwner?,
            value: PopupMenuAlignment
        ): PaytmOverflowMenu {
//      if (instance == null) {
            instance = OverflowMenuFactory().create(context, lifecycle, value)
            //}

            return instance as PaytmOverflowMenu
        }
    }

    fun create(
        context: Context,
        lifecycle: LifecycleOwner?,
        value: PopupMenuAlignment
    ): PaytmOverflowMenu {
        return PaytmOverflowMenu.Builder(context)
            .setLayout(R.layout.layout_custom_list)
            .setArrowOrientation(value)
            .setElevation(6)
            .setBackgroundColorResource(R.color.white)
            .setDismissWhenShowAgain(true)
            .setDismissWhenTouchOutside(true)
            .setDismissWhenClicked(true)
            .setLifecycleOwner(lifecycle)
            .build()
    }
}
