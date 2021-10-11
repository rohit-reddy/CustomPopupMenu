package com.rohith.mycustompopupmenu

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun getCustomSamples(context: Context): List<CustomItem> {
    val samples = ArrayList<CustomItem>()
    samples.add(CustomItem(drawable(context, R.drawable.ic_push_pin), "Pin"))
    samples.add(CustomItem(drawable(context, R.drawable.ic_home), "Add to Home"))
    samples.add(CustomItem(drawable(context, R.drawable.ic_delete), "Delete"))
    return samples
}

fun getCustomSamplesWithoutIcons(context: Context): List<CustomItem> {
    val samples = ArrayList<CustomItem>()
    samples.add(CustomItem(null, "Pin"))
    samples.add(CustomItem(null, "Add to Home"))
    samples.add(CustomItem(null, "Delete"))
    return samples
}

private fun drawable(context: Context, @DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(context, id)
}