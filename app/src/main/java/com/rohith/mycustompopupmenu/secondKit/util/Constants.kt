

package com.rohith.mycustompopupmenu.secondKit.util

/** definition of the non-value of Int type. */

internal const val NO_INT_VALUE: Int = Int.MIN_VALUE

/** definition of the non-value of Float type. */

internal const val NO_Float_VALUE: Float = 0f

/** definition of the non-value of Long type. */

internal const val NO_LONG_VALUE: Long = -1L

/** definition of the left-to-right value. */

internal const val LTR: Int = 1

/** definition of the boundary size between the content and the arrow. */

internal const val SIZE_ARROW_BOUNDARY: Int = 1

/** returns the negative of this value. */
internal fun Int.unaryMinus(predicate: Boolean): Int {
    return if (predicate) {
        unaryMinus()
    } else {
        this
    }
}
