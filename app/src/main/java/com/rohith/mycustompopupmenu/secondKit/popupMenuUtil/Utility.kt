

package com.rohith.mycustompopupmenu.secondKit.popupMenuUtil

import com.rohith.mycustompopupmenu.secondKit.PaytmOverflowMenu


/**
 * ArrowPositionRules determines the position of the arrow depending on the aligning rules.
 * [ArrowPositionRules.ALIGN_ANCHOR]: Align the arrow position depending on an anchor.
 */
enum class ArrowPositionRules {
  /**
   * Align the arrow position depending on an anchor.
   *
   * If [PaytmOverflowMenu.Builder.arrowPosition] is 0.5, the arrow will be located in the middle of an anchor.
   */
  ALIGN_ANCHOR
}


/** BalloonCenterAlign is an aligning rule for the [PaytmOverflowMenu.show]. */
enum class PopupMenuAlignment {
  BOTTOMRIGHT,
  TOPRIGHT,
  BOTTOMLEFT,
  TOPLEFT
}

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
