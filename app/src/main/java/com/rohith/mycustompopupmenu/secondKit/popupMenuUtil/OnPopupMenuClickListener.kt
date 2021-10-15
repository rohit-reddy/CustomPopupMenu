

package com.rohith.mycustompopupmenu.secondKit.popupMenuUtil

import android.view.MotionEvent
import android.view.View

/** Interface definition for a callback to be invoked when a popupmenu view is clicked. */
fun interface OnPopupMenuClickListener {

  /** invoked when the [PaytmOverflowMenu] is clicked. */
  fun onpopupmenuClick(view: View)
}


/** Interface definition for a callback to be invoked when a popupmenu view is dismissed. */
fun interface OnPopupMenuDismissListener {

  /** invoked when the [PaytmOverflowMenu] is dismissed. */
  fun onpopupmenuDismiss()
}


/** Interface definition for a callback to be invoked when touched on outside of the popupmenu popup. */
fun interface OnPopupMenuOutsideTouchListener {

  /** invoked when the outside of the [PaytmOverflowMenu] is touched. */
  fun onpopupmenuOutsideTouch(view: View, event: MotionEvent)
}
