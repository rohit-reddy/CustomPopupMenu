package com.rohith.mycustompopupmenu

import android.content.Context
import android.widget.FrameLayout
import android.widget.PopupWindow

class CustomPopupWindow(
    val context: Context,
    root: FrameLayout,
    wrapContent: Int,
    wrapContent1: Int
) : PopupWindow(root, wrapContent, wrapContent1)