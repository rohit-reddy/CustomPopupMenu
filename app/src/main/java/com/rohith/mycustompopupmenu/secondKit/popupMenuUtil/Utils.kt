package com.rohith.mycustompopupmenu.secondKit.popupMenuUtil

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.rohith.mycustompopupmenu.CustomItem
import com.rohith.mycustompopupmenu.R

fun getCustomSamplesN(context: Context, numberOfItems : Int): List<CustomItem> {
    val samples = ArrayList<CustomItem>()
    for (i in 1..numberOfItems){
        samples.add(CustomItem(drawable(context, R.drawable.icon_rg_24_system_bounding), "Label"))
    }

    return samples
}

fun getCustomSamplesWithoutIconsN(context: Context, numberOfItems: Int): List<CustomItem> {
    val samples = ArrayList<CustomItem>()
    for (i in 1..numberOfItems){
        samples.add(CustomItem(null, "Label"))
    }
    return samples
}

private fun drawable(context: Context, @DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(context, id)
}