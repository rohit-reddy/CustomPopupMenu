

package com.rohith.mycustompopupmenu.secondKit.popupMenuUtil

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt


/** gets a dimension size from dimension resource. */

internal fun Context.dimen(@DimenRes dimenRes: Int): Float {
    return resources.getDimension(dimenRes)
}

/** gets a color from the resource. */

internal fun Context.contextColor(resource: Int): Int {
    return ContextCompat.getColor(this, resource)
}


/** returns if an Activity is finishing or not. */
internal val Context.isFinishing: Boolean
    inline get() = this is Activity && this.isFinishing


/** returns true if there is a start/left or end/right drawable in the array. */

internal fun Array<Drawable?>.isExistHorizontalDrawable(): Boolean {
    return this[0] != null || this[2] != null
}

/** returns intrinsic height size of a drawable array. */

internal fun Array<Drawable?>.getIntrinsicHeight(): Int {
    return this[0].getHeight().coerceAtLeast(this[2].getHeight())
}

/** returns intrinsic height size of a drawable array. */

internal fun Array<Drawable?>.getSumOfIntrinsicWidth(): Int {
    return this[0].getWidth() + this[2].getWidth()
}

/** returns intrinsic height size of a drawable. */

internal fun Drawable?.getHeight(): Int {
    return this?.intrinsicHeight ?: 0
}

/** returns intrinsic height size of a drawable. */

internal fun Drawable?.getWidth(): Int {
    return this?.intrinsicWidth ?: 0
}

/**
 * Runs a [block] lambda when the device's SDK level is 21 or higher.
 *
 * @param block A lambda that should be run when the device's SDK level is 21 or higher.
 */

internal inline fun runOnAfterSDK21(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        block()
    }
}

/**
 * Runs a [block] lambda when the device's SDK level is 22 or higher.
 *
 * @param block A lambda that should be run when the device's SDK level is 22 or higher.
 */

internal inline fun runOnAfterSDK22(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        block()
    }
}

/** returns sum of the compound padding (start and end). */
internal val TextView.sumOfCompoundPadding: Int
    inline get() = compoundPaddingStart + compoundPaddingEnd


/** sets visibility of the view based on the given parameter. */

internal fun View.visible(shouldVisible: Boolean) {
    visibility = if (shouldVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

/** computes and returns the coordinates of this view on the screen. */

internal fun View.getViewPointOnScreen(): Point {
    val location: IntArray = intArrayOf(0, 0)
    getLocationOnScreen(location)
    return Point(location[0], location[1])
}


/** returns integer dimensional value from the integer px value. */
internal val Int.dp: Int
    inline get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(),
        Resources.getSystem().displayMetrics
    ).roundToInt()

/** returns float dimensional value from the float px value. */
internal val Float.dp: Float
    inline get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this,
        Resources.getSystem().displayMetrics
    )

/** gets display size as a point. */
internal val displaySize: Point
    inline get() = Point(
        Resources.getSystem().displayMetrics.widthPixels,
        Resources.getSystem().displayMetrics.heightPixels
    )
