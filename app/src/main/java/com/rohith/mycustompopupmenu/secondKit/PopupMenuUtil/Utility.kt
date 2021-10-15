

package com.rohith.mycustompopupmenu.secondKit.PopupMenuUtil

import com.rohith.mycustompopupmenu.secondKit.PaytmOverflowMenu

/** ArrowOrientation determines the orientation of the arrow. */
enum class ArrowOrientation {
  BOTTOM,
  TOP,
  LEFT,
  RIGHT
}


/**
 * ArrowOrientationRules determines the orientation of the arrow depending on the aligning rules.
 *
 * [ArrowOrientationRules.ALIGN_ANCHOR]: Align depending on the position of an anchor.
 * [ArrowOrientationRules.ALIGN_FIXED]: Align to fixed [ArrowOrientation].
 */
enum class ArrowOrientationRules {
  /**
   * Align depending on the position of an anchor.
   *
   * For example, [PaytmOverflowMenu.Builder.arrowAlignment] is [ArrowOrientation.TOP] and we want to show up
   * the balloon under an anchor using the [PaytmOverflowMenu.showAlignBottom].
   * However, if there is not enough free space to place the tooltip at the bottom of the anchor,
   * tooltips will be placed on top of the anchor and the orientation of the arrow will be [ArrowOrientation.BOTTOM].
   */
  ALIGN_ANCHOR
}



/**
 * ArrowPositionRules determines the position of the arrow depending on the aligning rules.
 *
 * [ArrowPositionRules.ALIGN_POPUPMENU]: Align the arrow position depending on the balloon popup body.
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


/**
 * A runnable class that implements [Runnable] for dismissing a [PaytmOverflowMenu]
 * if the [PaytmOverflowMenu.Builder.autoDismissDuration] has a value.
 */
internal class AutoDismissRunnable(val paytmOverflowMenu: PaytmOverflowMenu) : Runnable {

  /** dismiss a balloon. */
  override fun run() {
    paytmOverflowMenu.dismiss()
  }
}


/** BalloonCenterAlign is an aligning rule for the [PaytmOverflowMenu.show]. */
enum class PopupMenuAlignment {
  BOTTOMRIGHT,
  TOPRIGHT,
  BOTTOMLEFT,
  TOPLEFT
}



/**
 * A specification interface for determining sizes of the Balloon materials.
 * We can wrap some materials depending on their size of content using this interface.
 */
object PopupMenuSizeSpec {

  /** Wraps the material depending on the size of the content.  */
  const val WRAP: Int = NO_INT_VALUE
}
