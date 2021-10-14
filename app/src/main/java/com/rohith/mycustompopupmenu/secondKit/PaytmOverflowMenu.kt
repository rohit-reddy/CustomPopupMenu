
package com.rohith.mycustompopupmenu.secondKit

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.method.MovementMethod
import android.util.LayoutDirection
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.forEach
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.rohith.mycustompopupmenu.CustomPopupWindow
import com.rohith.mycustompopupmenu.databinding.LayoutCustomPopupMenuBinding
import com.rohith.mycustompopupmenu.secondKit.annotations.Dp
import com.rohith.mycustompopupmenu.secondKit.annotations.Sp
import com.rohith.mycustompopupmenu.secondKit.util.*
import kotlin.math.max
import kotlin.math.roundToInt
import android.util.DisplayMetrics
import android.view.Gravity








/**
 * popupmenu implements a customizable tooltips popup with and arrow and animations.
 *
 * @see [PaytmOverflowMenu](https://github.com/skydoves/popupmenu)
 *
 * @param context A context for creating and accessing internal resources.
 * @param builder A [PaytmOverflowMenu.Builder] for creating an instance of the [PaytmOverflowMenu].
 */
@Suppress("MemberVisibilityCanBePrivate")
class PaytmOverflowMenu(
  private val context: Context,
  private val builder: Builder
) : LifecycleObserver {

  /** A main content view of the popup. */
  private val binding: LayoutCustomPopupMenuBinding =
    LayoutCustomPopupMenuBinding.inflate(LayoutInflater.from(context), null, false)


  /** A main content window of the popup. */
  val bodyWindow: CustomPopupWindow = CustomPopupWindow(context,
    binding.root,
    FrameLayout.LayoutParams.WRAP_CONTENT,
    FrameLayout.LayoutParams.WRAP_CONTENT
  )


  /** Denotes the popup is showing or not. */
  var isShowing = false
    private set

  /** Denotes the popup is already destroyed internally. */
  private var destroyed: Boolean = false

  /** Interface definition for a callback to be invoked when a popupmenu view is initialized. */
  @JvmField
  var onPopupMenuInitializedListener: OnPopupMenuInitializedListener? =
    builder.onPopupMenuInitializedListener

  /** A handler for running [autoDismissRunnable]. */
  private val handler: Handler by lazy(LazyThreadSafetyMode.NONE) {
    Handler(Looper.getMainLooper())
  }

  /** A runnable for dismissing the popupmenu with the [Builder.autoDismissDuration]. */
  private val autoDismissRunnable: AutoDismissRunnable by lazy(
    LazyThreadSafetyMode.NONE
  ) { AutoDismissRunnable(this) }



  init {
    createByBuilder()
  }

  private fun createByBuilder() {
    initializeBackground()
    initializepopupmenuRoot()
    initializepopupmenuWindow()
    initializepopupmenuLayout()
    initializepopupmenuContent()
    initializepopupmenuListeners()

    adjustFitsSystemWindows(binding.root)

    if (builder.lifecycleOwner == null && context is LifecycleOwner) {
      builder.setLifecycleOwner(context)
      context.lifecycle.addObserver(this@PaytmOverflowMenu)
    } else {
      builder.lifecycleOwner?.lifecycle?.addObserver(this@PaytmOverflowMenu)
    }
  }

  private fun adjustFitsSystemWindows(parent: ViewGroup) {
    parent.fitsSystemWindows = false
    (0 until parent.childCount).map { parent.getChildAt(it) }.forEach { child ->
      child.fitsSystemWindows = false
      if (child is ViewGroup) {
        adjustFitsSystemWindows(child)
      }
    }
  }

  private fun getMinArrowPosition(): Float {
    return (builder.arrowSize.toFloat() * builder.arrowAlignAnchorPaddingRatio) +
      builder.arrowAlignAnchorPadding
  }


  private fun getMaxArrowPosition(): Float{
    return getMeasuredWidth().toFloat() - builder.arrowSize - SIZE_ARROW_BOUNDARY - getMinArrowPosition()
  }

  private fun getDoubleArrowSize(): Int {
    return builder.arrowSize * 2
  }

//  private fun initializeArrow(anchor: View) {
//    with(binding.popupmenuArrow) {
//      layoutParams = FrameLayout.LayoutParams(builder.arrowSize, builder.arrowSize)
//      alpha = builder.alpha
//      builder.arrowDrawable?.let { setImageDrawable(it) }
//      setPadding(
//        builder.arrowLeftPadding,
//        builder.arrowTopPadding,
//        builder.arrowRightPadding,
//        builder.arrowBottomPadding
//      )
//      if (builder.arrowColor != NO_INT_VALUE) {
//        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(builder.arrowColor))
//      } else {
//        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(builder.backgroundColor))
//      }
//      runOnAfterSDK21 {
//        outlineProvider = ViewOutlineProvider.BOUNDS
//      }
//      binding.popupmenuCard.post {
//        onPopupMenuInitializedListener?.onpopupmenuInitialized(getContentView())
//
//        adjustArrowOrientationByRules(anchor)
//
//        @SuppressLint("NewApi")
//        when (builder.arrowOrientation) {
//          ArrowOrientation.BOTTOMLEFT -> {
//            rotation = 180f
//            x = getArrowConstraintPositionX(anchor)
//            y = binding.popupmenuCard.y + binding.popupmenuCard.height - SIZE_ARROW_BOUNDARY
//            ViewCompat.setElevation(this, builder.arrowElevation)
//            if (builder.arrowColorMatchpopupmenu) {
//              foreground = BitmapDrawable(
//                resources,
//                adjustArrowColorByMatchingCardBackground(
//                  this, x,
//                  binding.popupmenuCard.height.toFloat()
//                )
//              )
//            }
//          }
//          ArrowOrientation.TOPLEFT -> {
//            rotation = 0f
//            x = getArrowConstraintPositionX(anchor)
//            y = binding.popupmenuCard.y - builder.arrowSize + SIZE_ARROW_BOUNDARY
//            if (builder.arrowColorMatchpopupmenu) {
//              foreground =
//                BitmapDrawable(resources, adjustArrowColorByMatchingCardBackground(this, x, 0f))
//            }
//          }
//          ArrowOrientation.BOTTOMRIGHT -> {
//            rotation = -90f
//            x = binding.popupmenuCard.x - builder.arrowSize + SIZE_ARROW_BOUNDARY
//            y = getArrowConstraintPositionY(anchor)
//            if (builder.arrowColorMatchpopupmenu) {
//              foreground =
//                BitmapDrawable(resources, adjustArrowColorByMatchingCardBackground(this, 0f, y))
//            }
//          }
//          ArrowOrientation.TOPRIGHT -> {
//            rotation = 0f
//            x = getArrowConstraintPositionX(anchor)
//            y = binding.popupmenuCard.y - builder.arrowSize + SIZE_ARROW_BOUNDARY
//            if (builder.arrowColorMatchpopupmenu) {
//              foreground = BitmapDrawable(
//                resources,
//                adjustArrowColorByMatchingCardBackground(
//                  this, binding.popupmenuCard.width.toFloat(),
//                  y
//                )
//              )
//            }
//          }
//        }
//        visible(builder.isVisibleArrow)
//      }
//    }
//  }


  private fun initializeArrow(anchor: View) {
    with(binding.popupmenuArrow) {
      layoutParams = FrameLayout.LayoutParams(builder.arrowSize, builder.arrowSize)
      alpha = builder.alpha
      builder.arrowDrawable?.let { setImageDrawable(it) }
      setPadding(
        builder.arrowLeftPadding,
        builder.arrowTopPadding,
        builder.arrowRightPadding,
        builder.arrowBottomPadding
      )
      if (builder.arrowColor != NO_INT_VALUE) {
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(builder.arrowColor))
      } else {
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(builder.backgroundColor))
      }
      runOnAfterSDK21 {
        outlineProvider = ViewOutlineProvider.BOUNDS
      }
      binding.popupmenuCard.post {
        onPopupMenuInitializedListener?.onpopupmenuInitialized(getContentView())

        adjustArrowOrientationByRules(anchor)

        @SuppressLint("NewApi")
        when (builder.arrowOrientation) {
          ArrowOrientation.BOTTOM -> {
            rotation = 180f
            x = getArrowConstraintPositionX(anchor)
            y = binding.popupmenuCard.y + binding.popupmenuCard.height - SIZE_ARROW_BOUNDARY
            ViewCompat.setElevation(this, builder.arrowElevation)
            if (builder.arrowColorMatchpopupmenu) {
              foreground = BitmapDrawable(
                resources,
                adjustArrowColorByMatchingCardBackground(
                  this, x,
                  binding.popupmenuCard.height.toFloat()
                )
              )
            }
          }
          ArrowOrientation.TOP -> {
            rotation = 0f
            x = getArrowConstraintPositionX(anchor)
            y = binding.popupmenuCard.y - builder.arrowSize + SIZE_ARROW_BOUNDARY
            if (builder.arrowColorMatchpopupmenu) {
              foreground =
                BitmapDrawable(resources, adjustArrowColorByMatchingCardBackground(this, x, 0f))
            }
          }
          ArrowOrientation.LEFT -> {
            rotation = -90f
            x = binding.popupmenuCard.x - builder.arrowSize + SIZE_ARROW_BOUNDARY
            y = getArrowConstraintPositionY(anchor)
            if (builder.arrowColorMatchpopupmenu) {
              foreground =
                BitmapDrawable(resources, adjustArrowColorByMatchingCardBackground(this, 0f, y))
            }
          }
          ArrowOrientation.RIGHT -> {
            rotation = 90f
            x = binding.popupmenuCard.x + binding.popupmenuCard.width - SIZE_ARROW_BOUNDARY
            y = getArrowConstraintPositionY(anchor)
            if (builder.arrowColorMatchpopupmenu) {
              foreground = BitmapDrawable(
                resources,
                adjustArrowColorByMatchingCardBackground(
                  this, binding.popupmenuCard.width.toFloat(),
                  y
                )
              )
            }
          }
        }
        visible(builder.isVisibleArrow)
      }
    }
  }

  /**
   * Calculate the color at arrow position from popupmenuCard. The color is then set as a foreground to the arrow.
   *
   * @param imageView the arrow imageview containing the drawable.
   * @param x x position of the point where the middle of the arrow is connected to the popupmenu
   * @param y y position of the point where the middle of the arrow is connected to the popupmenu
   *
   * @throws IllegalArgumentException Throws an exception when the arrow is attached outside the popupmenu.
   *
   */
  private fun adjustArrowColorByMatchingCardBackground(
    imageView: AppCompatImageView,
    x: Float,
    y: Float
  ): Bitmap {
    imageView.setColorFilter(builder.backgroundColor, PorterDuff.Mode.SRC_IN)
    val oldBitmap = drawableToBitmap(
      imageView.drawable, imageView.drawable.intrinsicWidth,
      imageView.drawable.intrinsicHeight
    )
    val colors: Pair<Int, Int>
    try {
      colors = getColorsFrompopupmenuCard(x, y)
    } catch (e: IllegalArgumentException) {
      throw IllegalArgumentException(
        "Arrow attached outside popupmenu. Could not get a matching color."
      )
    }
    val startColor = colors.first
    val endColor = colors.second

    val updatedBitmap =
      Bitmap.createBitmap(oldBitmap.width, oldBitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(updatedBitmap)
    canvas.drawBitmap(oldBitmap, 0f, 0f, null)
    val paint = Paint()
    val shader: LinearGradient = when (builder.arrowOrientation) {
      ArrowOrientation.BOTTOM, ArrowOrientation.LEFT -> {
        LinearGradient(
          oldBitmap.width.toFloat() / 2 - builder.arrowHalfSize, 0f,
          oldBitmap.width.toFloat(), 0f, startColor, endColor, Shader.TileMode.CLAMP
        )
      }
      ArrowOrientation.RIGHT, ArrowOrientation.TOP -> {
        LinearGradient(
          oldBitmap.width.toFloat() / 2 + builder.arrowHalfSize, 0f, 0f, 0f,
          startColor, endColor, Shader.TileMode.CLAMP
        )
      }
    }
    paint.shader = shader
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawRect(0f, 0f, oldBitmap.width.toFloat(), oldBitmap.height.toFloat(), paint)
    imageView.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN)
    return updatedBitmap
  }

  private fun getColorsFrompopupmenuCard(x: Float, y: Float): Pair<Int, Int> {
    val bitmap = drawableToBitmap(
      binding.popupmenuCard.background, binding.popupmenuCard.width + 1,
      binding.popupmenuCard.height + 1
    )
    val startColor: Int
    val endColor: Int
    when (builder.arrowOrientation) {
      ArrowOrientation.BOTTOM, ArrowOrientation.TOP -> {
        startColor = bitmap.getPixel((x + builder.arrowHalfSize).toInt(), y.toInt())
        endColor = bitmap.getPixel((x - builder.arrowHalfSize).toInt(), y.toInt())
      }
      ArrowOrientation.LEFT, ArrowOrientation.RIGHT -> {
        startColor = bitmap.getPixel(x.toInt(), (y + builder.arrowHalfSize).toInt())
        endColor = bitmap.getPixel(x.toInt(), (y - builder.arrowHalfSize).toInt())
      }
    }
    return Pair(startColor, endColor)
  }

  private fun drawableToBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
  }

  /**
   * Adjust the orientation of the arrow depending on the [ArrowOrientationRules].
   *
   * @param anchor A target anchor to be shown under the popupmenu.
   */
  private fun adjustArrowOrientationByRules(anchor: View) {
    if (builder.arrowOrientationRules == ArrowOrientationRules.ALIGN_FIXED) return

    val anchorRect = Rect()
    anchor.getGlobalVisibleRect(anchorRect)

    val windowRect = Rect()
    bodyWindow.contentView.getGlobalVisibleRect(windowRect)

    val location: IntArray = intArrayOf(0, 0)
    bodyWindow.contentView.getLocationOnScreen(location)

    if (builder.arrowOrientation == ArrowOrientation.TOP &&
      location[1] < anchorRect.bottom
    ) {
      builder.setArrowOrientation(ArrowOrientation.BOTTOM)
    } else if (builder.arrowOrientation == ArrowOrientation.BOTTOM &&
      location[1] > anchorRect.top
    ) {
      builder.setArrowOrientation(ArrowOrientation.TOP)
    }

    initializepopupmenuContent()
  }

  private fun getArrowConstraintPositionX(anchor: View): Float {
    val popupmenuX: Int = binding.popupmenuContent.getViewPointOnScreen().x
    val anchorX: Int = anchor.getViewPointOnScreen().x
    val minPosition = getMinArrowPosition()
    val maxPosition1 = binding.popupmenuCard.measuredWidth - minPosition
    val maxPosition = getMeasuredWidth().toFloat() - builder.arrowSize - SIZE_ARROW_BOUNDARY - minPosition

    return when(builder.arrowOrientation){
      ArrowOrientation.TOP -> maxPosition
      ArrowOrientation.RIGHT -> maxPosition
      ArrowOrientation.BOTTOM -> maxPosition
      else ->{
        maxPosition
      }
    }
//    return when (builder.arrowPositionRules) {
//      ArrowPositionRules.ALIGN_POPUPMENU -> binding.popupmenuWrapper.width * builder.arrowPosition - builder.arrowHalfSize
//      ArrowPositionRules.ALIGN_ANCHOR -> {
//        when {
//          anchorX + anchor.width < popupmenuX -> minPosition
//          popupmenuX + getMeasuredWidth() < anchorX -> maxPosition
//          else -> {
//            val position =
//              (anchor.width) * builder.arrowPosition + anchorX - popupmenuX - builder.arrowHalfSize
//            when {
//              position <= getDoubleArrowSize() -> minPosition
//              position > getMeasuredWidth() - getDoubleArrowSize() -> maxPosition
//              else -> position
//            }
//          }
//        }
//      }
//    }
  }

//  private fun getArrowConstraintPositionY(anchor: View): Float {
//    val statusBarHeight = anchor.getStatusBarHeight(builder.isStatusBarVisible)
//    val popupmenuY: Int = binding.popupmenuContent.getViewPointOnScreen().y - statusBarHeight
//    val anchorY: Int = anchor.getViewPointOnScreen().y - statusBarHeight
//    val minPosition = getMinArrowPosition()
//    val maxPosition = getMeasuredHeight() - minPosition - builder.marginTop - builder.marginBottom
//    val arrowHalfSize = builder.arrowSize / 2
//    return maxPosition
////    return when (builder.arrowPositionRules) {
////      ArrowPositionRules.ALIGN_POPUPMENU -> binding.popupmenuWrapper.height * builder.arrowPosition - arrowHalfSize
////      ArrowPositionRules.ALIGN_ANCHOR -> {
////        when {
////          anchorY + anchor.height < popupmenuY -> minPosition
////          popupmenuY + getMeasuredHeight() < anchorY -> maxPosition
////          else -> {
////            val position =
////              (anchor.height) * builder.arrowPosition + anchorY - popupmenuY - arrowHalfSize
////            when {
////              position <= getDoubleArrowSize() -> minPosition
////              position > getMeasuredHeight() - getDoubleArrowSize() -> maxPosition
////              else -> position
////            }
////          }
////        }
////      }
////    }
//  }


  private fun getArrowConstraintPositionY(anchor: View): Float {
    val statusBarHeight = anchor.getStatusBarHeight(builder.isStatusBarVisible)
    val balloonY: Int = binding.popupmenuContent.getViewPointOnScreen().y - statusBarHeight
    val anchorY: Int = anchor.getViewPointOnScreen().y - statusBarHeight
    val minPosition = getMinArrowPosition()
    val maxPosition = getMeasuredHeight() - minPosition - builder.marginTop - builder.marginBottom
    val arrowHalfSize = builder.arrowSize / 2
    return when (builder.arrowPositionRules) {
      ArrowPositionRules.ALIGN_POPUPMENU -> binding.popupmenuWrapper.height * builder.arrowPosition - arrowHalfSize
      ArrowPositionRules.ALIGN_ANCHOR -> {
        when {
          anchorY + anchor.height < balloonY -> minPosition
          balloonY + getMeasuredHeight() < anchorY -> maxPosition
          else -> {
            val position =
              (anchor.height) * builder.arrowPosition + anchorY - balloonY - arrowHalfSize
            when {
              position <= getDoubleArrowSize() -> minPosition
              position > getMeasuredHeight() - getDoubleArrowSize() -> maxPosition
              else -> position
            }
          }
        }
      }
    }
  }

  private fun initializeBackground() {
    with(binding.popupmenuCard) {
      alpha = builder.alpha
      radius = builder.cornerRadius
      ViewCompat.setElevation(this, builder.elevation)
      background = builder.backgroundDrawable ?: GradientDrawable().apply {
        setColor(builder.backgroundColor)
        cornerRadius = builder.cornerRadius
      }
      setPadding(
        builder.paddingLeft,
        builder.paddingTop,
        builder.paddingRight,
        builder.paddingBottom
      )
    }
  }

  private fun initializepopupmenuWindow() {
    with(this.bodyWindow) {
      isOutsideTouchable = true
      isFocusable = builder.isFocusable
      setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      runOnAfterSDK21 {
        elevation = builder.elevation
      }
      setIsAttachedInDecor(builder.isAttachedInDecor)
    }
  }

  private fun initializepopupmenuListeners() {
    setOnPopupMenuClickListener(builder.onPopupMenuClickListener)
    setOnPopupMenuDismissListener(builder.onPopupMenuDismissListener)
    setOnPopupMenuOutsideTouchListener(builder.onPopupMenuOutsideTouchListener)
  }

  private fun initializepopupmenuRoot() {
    with(binding.popupmenuWrapper) {
      (layoutParams as ViewGroup.MarginLayoutParams).setMargins(
        builder.marginLeft,
        builder.marginTop,
        builder.marginRight,
        builder.marginBottom
      )
    }
  }

  private fun initializepopupmenuContent() {
    val paddingSize = builder.arrowSize - SIZE_ARROW_BOUNDARY
    val elevation = builder.elevation.toInt()
    with(binding.popupmenuContent) {
      when (builder.arrowOrientation) {
        ArrowOrientation.LEFT -> setPadding(paddingSize, elevation, paddingSize, elevation)
        ArrowOrientation.RIGHT -> setPadding(paddingSize, elevation, paddingSize, elevation)
        ArrowOrientation.TOP ->
          setPadding(elevation, paddingSize, elevation, paddingSize.coerceAtLeast(elevation))
        ArrowOrientation.BOTTOM ->
          setPadding(elevation, paddingSize, elevation, paddingSize.coerceAtLeast(elevation))

      }
    }
  }


  private fun initializepopupmenuLayout() {
    if (hasCustomLayout()) {
      initializeCustomLayout()
    }
  }

  /** Check the [PaytmOverflowMenu.Builder] has a custom layout [PaytmOverflowMenu.Builder.layoutRes] or [PaytmOverflowMenu.Builder.layout]. */
  private fun hasCustomLayout(): Boolean {
    return builder.layoutRes != null || builder.layout != null
  }

  /** Initializes the popupmenu content using the custom layout. */
  private fun initializeCustomLayout() {
    val layout = builder.layoutRes?.let {
      LayoutInflater.from(context).inflate(it, binding.popupmenuCard, false)
    } ?: builder.layout ?: throw IllegalArgumentException("The custom layout is null.")
    binding.popupmenuCard.removeAllViews()
    binding.popupmenuCard.addView(layout)
    traverseAndMeasureTextWidth(binding.popupmenuCard)
  }



  /**
   * Shows [PaytmOverflowMenu] tooltips on the [anchor] with some initializations related to arrow, content, and overlay.
   * The popupmenu will be shown with the [overlayWindow] if the anchorView's parent window is in a valid state.
   * The size of the content will be measured internally, and it will affect calculating the popup size.
   *
   * @param block A lambda block for showing the [bodyWindow].
   */
  @MainThread
  private inline fun show(anchor: View, crossinline block: () -> Unit) {
    if (!isShowing &&
      // If the popupmenu is already destroyed depending on the lifecycle,
      // We should not allow showing the popupWindow, it's related to `relay()` method. (#46)
      !destroyed &&
      // We should check the current Activity is running.
      // If the Activity is finishing, we can't attach the popupWindow to the Activity's window. (#92)
      !context.isFinishing &&
      // We should check the contentView is already attached to the decorView or backgroundView in the popupWindow.
      // Sometimes there is a concurrency issue between show and dismiss the popupWindow. (#149)
      bodyWindow.contentView.parent == null &&
      // we should check the anchor view is attached to the parent's window.
      ViewCompat.isAttachedToWindow(anchor)
    ) {
      anchor.post {
        this.isShowing = true

        val dismissDelay = this.builder.autoDismissDuration
        if (dismissDelay != NO_LONG_VALUE) {
          dismissWithDelay(dismissDelay)
        }

        if (hasCustomLayout()) {
          traverseAndMeasureTextWidth(binding.popupmenuCard)
        }
        this.binding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        this.bodyWindow.width = getMeasuredWidth()
        this.bodyWindow.height = getMeasuredHeight()

        initializeArrow(anchor)
        initializepopupmenuContent()

        block()
      }
    } else if (builder.dismissWhenShowAgain) {
      dismiss()
    }
  }
  

  /**
   * Shows the popupmenu over the anchor view (overlap) as the center aligns.
   * Even if you use with the [ArrowOrientationRules.ALIGN_ANCHOR], the alignment will not be guaranteed.
   * So if you use the function, use with [ArrowOrientationRules.ALIGN_FIXED] and fixed [ArrowOrientation].
   *
   * @param anchor A target view which popup will be shown with overlap.
   * @param xOff A horizontal offset from the anchor in pixels.
   * @param yOff A vertical offset from the anchor in pixels.
   * @param centerAlign A rule for deciding the alignment of the popupmenu.
   */

  @JvmOverloads
  fun showAtCenter(
    anchor: View,
    xOff: Int = 0,
    yOff: Int = 0,
    centerAlign: PopupMenuCenterAlign = PopupMenuCenterAlign.TOP
  ) {
    val halfAnchorWidth = (anchor.measuredWidth * 0.5f).roundToInt()
    val halfAnchorHeight = (anchor.measuredHeight * 0.5f).roundToInt()
    val halfBalloonWidth = (getMeasuredWidth() * 0.5f).roundToInt()
    val halfBalloonHeight = (getMeasuredHeight() * 0.5f).roundToInt()
    show(anchor) {
      when (centerAlign) {
        PopupMenuCenterAlign.TOP ->{
          val popupmenuX: Int = binding.popupmenuContent.getViewPointOnScreen().x
          val anchorX: Int = anchor.getViewPointOnScreen().x
          bodyWindow.showAsDropDown(
            anchor,
            ((((anchor.right + anchorX) / 2) - anchorX - getMinArrowPosition().toInt() - builder.arrowHalfSize).toInt()),
            -getMeasuredHeight() - anchor.measuredHeight + yOff,
            Gravity.START or Gravity.LEFT
          )
        }

        PopupMenuCenterAlign.BOTTOM ->{
          val popupmenuX: Int = binding.popupmenuContent.getViewPointOnScreen().x
          val anchorX: Int = anchor.getViewPointOnScreen().x
          bodyWindow.showAsDropDown(
            anchor,
            ((((anchor.right + anchorX) / 2) - anchorX - getMinArrowPosition().toInt() - builder.arrowHalfSize).toInt()),
            0,
            Gravity.START or Gravity.LEFT
          )
        }

        PopupMenuCenterAlign.START ->{
          val anchorX: Int = anchor.getViewPointOnScreen().x
          //Get the height of 2/3rd of the height of the screen

          //Get the height of 2/3rd of the height of the screen
          val displayMetrics = context.resources.displayMetrics
          val height = displayMetrics.heightPixels
          val width = displayMetrics.widthPixels
          println("Height:$height")
          println("Width:$width")

//          bodyWindow.showAsDropDown(anchor,
//            (-(anchor.right - ((anchorX + anchor.right) / 2)) + getMinArrowPosition().toInt() + builder.arrowSize - SIZE_ARROW_BOUNDARY - builder.arrowHalfSize).toInt(),
//            (-getMeasuredHeight() + halfAnchorHeight) + yOff,
//            Gravity.RIGHT or Gravity.END)
          bodyWindow.showAsDropDown(anchor,
            (-(anchor.right - ((anchorX + anchor.right) / 2)) + (getMinArrowPosition() + builder.arrowSize - SIZE_ARROW_BOUNDARY) - builder.arrowHalfSize).toInt(),
            -getMeasuredHeight() - anchor.measuredHeight + yOff,
          Gravity.RIGHT or Gravity.END)

        }

        PopupMenuCenterAlign.END ->{
          val anchorX: Int = anchor.getViewPointOnScreen().x
          //Get the height of 2/3rd of the height of the screen

          //Get the height of 2/3rd of the height of the screen
          val displayMetrics = context.resources.displayMetrics
          val height = displayMetrics.heightPixels
          val width = displayMetrics.widthPixels
          println("Height:$height")
          println("Width:$width")

//          if ((width - anchorX) < getMeasuredWidth()) {
            bodyWindow.showAsDropDown(anchor,
              (-(anchor.right - ((anchorX + anchor.right) / 2)) + getMinArrowPosition().toInt() + builder.arrowSize - SIZE_ARROW_BOUNDARY - builder.arrowHalfSize).toInt(), 0, Gravity.RIGHT or Gravity.END)
//          } else {
//            bodyWindow.showAsDropDown(
//              anchor,
//              0,
//              0,
//              Gravity.RIGHT or Gravity.END
           // )
         // }

        }

      }
    }
  }


  /**
   * Shows the popupmenu on an anchor view as drop down with x-off and y-off.
   *
   * @param anchor A target view which popup will be shown to.
   * @param xOff A horizontal offset from the anchor in pixels.
   * @param yOff A vertical offset from the anchor in pixels.
   */
  
  fun showAsDropDown(anchor: View, xOff: Int = 0, yOff: Int = 0) {
    show(anchor) { bodyWindow.showAsDropDown(anchor, xOff, yOff) }
  }


//  /**
//   * Shows the popupmenu on an anchor view as the top alignment with x-off and y-off.
//   *
//   * @param anchor A target view which popup will be shown to.
//   * @param xOff A horizontal offset from the anchor in pixels.
//   * @param yOff A vertical offset from the anchor in pixels.
//   */
//
//  fun showAlignTop(anchor: View, xOff: Int = 0, yOff: Int = 0) {
//    show(anchor) {
//      bodyWindow.showAsDropDown(
//        anchor,
//        builder.supportRtlLayoutFactor * ((anchor.measuredWidth / 2) - 10.dp - getMinArrowPosition().toInt()),
//        -getMeasuredHeight() - anchor.measuredHeight + yOff
//      )
//    }
//  }


  /**
   * Shows the balloon on an anchor view as the top alignment with x-off and y-off.
   *
   * @param anchor A target view which popup will be shown to.
   * @param xOff A horizontal offset from the anchor in pixels.
   * @param yOff A vertical offset from the anchor in pixels.
   */
  @JvmOverloads
  fun showAlignTop(anchor: View, xOff: Int = 0, yOff: Int = 0) {
    show(anchor) {
      bodyWindow.showAsDropDown(
        anchor,
        builder.supportRtlLayoutFactor * ((anchor.measuredWidth / 2) - (getMeasuredWidth() / 2) + xOff),
        -getMeasuredHeight() - anchor.measuredHeight + yOff
      )
    }
  }


  /**
   * Shows the balloon on an anchor view as the bottom alignment with x-off and y-off.
   *
   * @param anchor A target view which popup will be shown to.
   * @param xOff A horizontal offset from the anchor in pixels.
   * @param yOff A vertical offset from the anchor in pixels.
   */
  @JvmOverloads
  fun showAlignBottom(anchor: View, xOff: Int = 0, yOff: Int = 0) {
    show(anchor) {
      bodyWindow.showAsDropDown(
        anchor,
        builder.supportRtlLayoutFactor * ((anchor.measuredWidth / 2) - (getMeasuredWidth() / 2) + xOff),
        yOff
      )
    }
  }


//  /**
//   * Shows the popupmenu on an anchor view as the bottom alignment with x-off and y-off.
//   *
//   * @param anchor A target view which popup will be shown to.
//   * @param xOff A horizontal offset from the anchor in pixels.
//   * @param yOff A vertical offset from the anchor in pixels.
//   */
//
//  fun showAlignBottom(anchor: View, xOff: Int = 0, yOff: Int = 0) {
//    show(anchor) {
//      when(builder.arrowOrientation){
//        ArrowOrientation.LEFT ->{
//          bodyWindow.showAsDropDown(
//            anchor,
//            builder.supportRtlLayoutFactor * ((anchor.measuredWidth / 2) - 10.dp - getMinArrowPosition().toInt()),
//            yOff
//          )
//        }
//        ArrowOrientation.RIGHT ->{
//          bodyWindow.showAsDropDown(
//            anchor,
//            anchor.measuredWidth / 2,
//            yOff
//          )
//        }
//        else ->{
//          Log.d("TAG", "showAlignBottom: Please check the position")
//        }
//      }
//    }
//  }


  /**
   * Shows the popupmenu on an anchor view as the right alignment with x-off and y-off.
   *
   * @param anchor A target view which popup will be shown to.
   * @param xOff A horizontal offset from the anchor in pixels.
   * @param yOff A vertical offset from the anchor in pixels.
   */
  
  fun showAlignRight(anchor: View, xOff: Int = 0, yOff: Int = 0) {
    show(anchor) {
      bodyWindow.showAsDropDown(
        anchor,
        anchor.measuredWidth + xOff,
        -(getMeasuredHeight() / 2) - (anchor.measuredHeight / 2) + yOff
      )
    }
  }


  /**
   * Shows the popupmenu on an anchor view as the left alignment with x-off and y-off.
   *
   * @param anchor A target view which popup will be shown to.
   * @param xOff A horizontal offset from the anchor in pixels.
   * @param yOff A vertical offset from the anchor in pixels.
   */
  
  fun showAlignLeft(anchor: View, xOff: Int = 0, yOff: Int = 0) {
    show(anchor) {
      bodyWindow.showAsDropDown(
        anchor,
        -(getMeasuredWidth()) + xOff,
        -(getMeasuredHeight() / 2) - (anchor.measuredHeight / 2) + yOff
      )
    }
  }


  /** updates popup and arrow position of the popup based on a new target anchor view. */
  @MainThread
  private inline fun update(anchor: View, crossinline block: () -> Unit) {
    if (isShowing) {
      initializeArrow(anchor)
      block()
    }
  }

  /** dismiss the popup menu. */
  fun dismiss() {
    if (this.isShowing) {
      val dismissWindow: () -> Unit = {
        this.isShowing = false
        this.bodyWindow.dismiss()
        this.handler.removeCallbacks(autoDismissRunnable)
      }
//      if (this.builder.popupmenuAnimation == popupmenuAnimation.CIRCULAR) {
//        this.bodyWindow.contentView.circularUnRevealed(builder.circularDuration) {
//          dismissWindow()
//        }
//      } else {
//
//      }
      dismissWindow()
    }
  }

  /** dismiss the popup menu with milliseconds delay. */
  fun dismissWithDelay(delay: Long) =
    handler.postDelayed(autoDismissRunnable, delay)

  /** sets a [OnPopupMenuClickListener] to the popup. */
  fun setOnPopupMenuClickListener(onPopupMenuClickListener: OnPopupMenuClickListener?) {
    this.binding.popupmenuWrapper.setOnClickListener {
      onPopupMenuClickListener?.onpopupmenuClick(it)
      if (builder.dismissWhenClicked) dismiss()
    }
  }


  /** sets a [OnPopupMenuClickListener] to the popup using lambda. */
  fun setOnPopupMenuClickListener(block: (View) -> Unit) {
    setOnPopupMenuClickListener(OnPopupMenuClickListener(block))
  }

  /**
   * sets a [OnPopupMenuInitializedListener] to the popup.
   * The [OnPopupMenuInitializedListener.onpopupmenuInitialized] will be invoked when inflating the
   * body content of the popupmenu is finished.
   */
  fun setOnpopupmenuInitializedListener(onPopupMenuInitializedListener: OnPopupMenuInitializedListener?) {
    this.onPopupMenuInitializedListener = onPopupMenuInitializedListener
  }

  /**
   * sets a [OnPopupMenuInitializedListener] to the popup using a lambda.
   * The [OnPopupMenuInitializedListener.onpopupmenuInitialized] will be invoked when inflating the
   * body content of the popupmenu is finished.
   */
  fun setOnpopupmenuInitializedListener(block: (View) -> Unit) {
    setOnpopupmenuInitializedListener(OnPopupMenuInitializedListener(block))
  }

  /** sets a [OnPopupMenuDismissListener] to the popup. */
  fun setOnPopupMenuDismissListener(onPopupMenuDismissListener: OnPopupMenuDismissListener?) {
    this.bodyWindow.setOnDismissListener {
      this@PaytmOverflowMenu.dismiss()
      onPopupMenuDismissListener?.onpopupmenuDismiss()
    }
  }

  /** sets a [OnPopupMenuDismissListener] to the popup using lambda. */
  fun setOnPopupMenuDismissListener(block: () -> Unit) {
    setOnPopupMenuDismissListener(OnPopupMenuDismissListener(block))
  }

  /** sets a [OnPopupMenuOutsideTouchListener] to the popup. */
  fun setOnPopupMenuOutsideTouchListener(onPopupMenuOutsideTouchListener: OnPopupMenuOutsideTouchListener?) {
    this.bodyWindow.setTouchInterceptor(
      object : View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, event: MotionEvent): Boolean {
          if (event.action == MotionEvent.ACTION_OUTSIDE) {
            if (builder.dismissWhenTouchOutside) {
              this@PaytmOverflowMenu.dismiss()
            }
            onPopupMenuOutsideTouchListener?.onpopupmenuOutsideTouch(view, event)
            return true
          }
          return false
        }
      }
    )
  }

  /** sets a [OnPopupMenuOutsideTouchListener] to the popup using lambda. */
  fun setOnPopupMenuOutsideTouchListener(block: (View, MotionEvent) -> Unit) {
    setOnPopupMenuOutsideTouchListener(
      OnPopupMenuOutsideTouchListener(block)
    )
  }

  /** sets a [View.OnTouchListener] to the popup. */
  fun setOnpopupmenuTouchListener(onTouchListener: View.OnTouchListener?) {
    if (onTouchListener != null) {
      this.bodyWindow.setTouchInterceptor(onTouchListener)
    }
  }



  /**
   * sets whether the popup window will be attached in the decor frame of its parent window.
   * If you want to show up popupmenu on your DialogFragment, it's recommended to use with true. (#131)
   */
  fun setIsAttachedInDecor(value: Boolean) = apply {
    runOnAfterSDK22 {
      this.bodyWindow.isAttachedInDecor = value
    }
  }

  /** gets measured width size of the popupmenu popup. */
  fun getMeasuredWidth(): Int {
    val displayWidth = displaySize.x
    return when {
      builder.widthRatio != NO_Float_VALUE ->
        (displayWidth * builder.widthRatio).toInt()
      builder.minWidthRatio != NO_Float_VALUE || builder.maxWidthRatio != NO_Float_VALUE -> {
        val maxWidthRatio =
          if (builder.maxWidthRatio != NO_Float_VALUE) builder.maxWidthRatio else 1f
        binding.root.measuredWidth.coerceIn(
          (displayWidth * builder.minWidthRatio).toInt(),
          (displayWidth * maxWidthRatio).toInt()
        )
      }
      builder.width != PopupMenuSizeSpec.WRAP -> builder.width.coerceAtMost(displayWidth)
      else -> binding.root.measuredWidth.coerceIn(builder.minWidth, builder.maxWidth)
    }
  }

  /**
   * Measures the width of a [AppCompatTextView] and set the measured with.
   * If the width of the parent XML layout is the `WRAP_CONTENT`, and the width of [AppCompatTextView]
   * in the parent layout is `WRAP_CONTENT`, this method will measure the size of the width exactly.
   *
   * @param textView a target textView for measuring text width.
   */
  private fun measureTextWidth(textView: AppCompatTextView, rootView: View) {
    with(textView) {
      var measuredTextWidth = textView.paint.measureText(textView.text.toString()).toInt()
      if (compoundDrawablesRelative.isExistHorizontalDrawable()) {
        minHeight = compoundDrawablesRelative.getIntrinsicHeight()
        measuredTextWidth += compoundDrawablesRelative.getSumOfIntrinsicWidth() + sumOfCompoundPadding
      } else if (compoundDrawables.isExistHorizontalDrawable()) {
        minHeight = compoundDrawables.getIntrinsicHeight()
        measuredTextWidth += compoundDrawables.getSumOfIntrinsicWidth() + sumOfCompoundPadding
      }
      maxWidth = getMeasuredTextWidth(measuredTextWidth, rootView)
    }
  }

  /**
   * Traverse a [ViewGroup]'s view hierarchy and measure each [AppCompatTextView] for measuring
   * the specific height of the [AppCompatTextView] and calculating the proper height size of the popupmenu.
   *
   * @param parent a parent view for traversing and measuring.
   */
  private fun traverseAndMeasureTextWidth(parent: ViewGroup) {
    parent.forEach { child ->
      if (child is AppCompatTextView) {
        measureTextWidth(child, parent)
      } else if (child is ViewGroup) {
        traverseAndMeasureTextWidth(child)
      }
    }
  }

  /** gets measured width size of the popupmenu popup text label. */
  private fun getMeasuredTextWidth(measuredWidth: Int, rootView: View): Int {
    val displayWidth = displaySize.x
    val spaces = rootView.paddingLeft + rootView.paddingRight + if (builder.iconDrawable != null) {
      builder.iconWidth + builder.iconSpace
    } else 0 + builder.marginRight + builder.marginLeft + (builder.arrowSize * 2)
    val maxTextWidth = builder.maxWidth - spaces

    return when {
      builder.widthRatio != NO_Float_VALUE ->
        (displayWidth * builder.widthRatio).toInt() - spaces
      builder.minWidthRatio != NO_Float_VALUE || builder.maxWidthRatio != NO_Float_VALUE -> {
        val maxWidthRatio =
          if (builder.maxWidthRatio != NO_Float_VALUE) builder.maxWidthRatio else 1f
        measuredWidth.coerceAtMost((displayWidth * maxWidthRatio).toInt() - spaces)
      }
      builder.width != PopupMenuSizeSpec.WRAP && builder.width <= displayWidth ->
        builder.width - spaces
      else -> measuredWidth.coerceAtMost(maxTextWidth)
    }
  }

  /** gets measured height size of the popupmenu popup. */
  fun getMeasuredHeight(): Int {
    if (builder.height != PopupMenuSizeSpec.WRAP) {
      return builder.height
    }
    return this.binding.root.measuredHeight
  }

  /** gets a content view of the popupmenu popup window. */
  fun getContentView(): ViewGroup {
    return binding.popupmenuCard
  }

  /** gets a arrow view of the popupmenu popup window. */
  fun getpopupmenuArrowView(): View {
    return binding.popupmenuArrow
  }

  /** dismiss when the [LifecycleOwner] be on paused. */
  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  fun onPause() {
    if (builder.dismissWhenLifecycleOnPause) {
      dismiss()
    }
  }

  /** dismiss automatically when lifecycle owner is destroyed. */
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  fun onDestroy() {
    this.destroyed = true
    this.bodyWindow.dismiss()
  }

  /** Builder class for creating [PaytmOverflowMenu]. */
  class Builder(private val context: Context) {
    @JvmField
    @Px
    var width: Int = PopupMenuSizeSpec.WRAP

    @JvmField
    @Px
    var minWidth: Int = 0

    @JvmField
    @Px
    var maxWidth: Int = displaySize.x

    @JvmField
    @FloatRange(from = 0.0, to = 1.0)
    var widthRatio: Float = NO_Float_VALUE

    @JvmField
    @FloatRange(from = 0.0, to = 1.0)
    var minWidthRatio: Float = NO_Float_VALUE

    @JvmField
    @FloatRange(from = 0.0, to = 1.0)
    var maxWidthRatio: Float = NO_Float_VALUE

    @JvmField
    @Px
    var height: Int = PopupMenuSizeSpec.WRAP

    @JvmField
    @Px
    var paddingLeft: Int = 0

    @JvmField
    @Px
    var paddingTop: Int = 0

    @JvmField
    @Px
    var paddingRight: Int = 0

    @JvmField
    @Px
    var paddingBottom: Int = 0

    @JvmField
    @Px
    var marginRight: Int = 0

    @JvmField
    @Px
    var marginLeft: Int = 0

    @JvmField
    @Px
    var marginTop: Int = 0

    @JvmField
    @Px
    var marginBottom: Int = 0

    @JvmField
    var isVisibleArrow: Boolean = true

    @JvmField
    @ColorInt
    var arrowColor: Int = NO_INT_VALUE

    @JvmField
    var arrowColorMatchpopupmenu: Boolean = false

    @JvmField
    @Px
    var arrowSize: Int = 12.dp

    val arrowHalfSize: Float
    @Px
    inline get() = arrowSize * 0.5f

    @JvmField
    @FloatRange(from = 0.0, to = 1.0)
    var arrowPosition: Float = 0.5f

    @JvmField
    var arrowPositionRules: ArrowPositionRules = ArrowPositionRules.ALIGN_POPUPMENU

    @JvmField
    var arrowOrientationRules: ArrowOrientationRules =
      ArrowOrientationRules.ALIGN_ANCHOR

    @JvmField
    var arrowOrientation: ArrowOrientation = ArrowOrientation.BOTTOM

    @JvmField
    var arrowDrawable: Drawable? = null

    @JvmField
    var arrowLeftPadding: Int = 0

    @JvmField
    var arrowRightPadding: Int = 0

    @JvmField
    var arrowTopPadding: Int = 0

    @JvmField
    var arrowBottomPadding: Int = 0

    @JvmField
    var arrowAlignAnchorPadding: Int = 0

    @JvmField
    var arrowAlignAnchorPaddingRatio: Float = 1.5f

    @JvmField
    var arrowElevation: Float = 0f

    @JvmField
    @ColorInt
    var backgroundColor: Int = Color.BLACK

    @JvmField
    var backgroundDrawable: Drawable? = null

    @JvmField
    @Px
    var cornerRadius: Float = 5f.dp

    @JvmField
    var text: CharSequence = ""

    @JvmField
    @ColorInt
    var textColor: Int = Color.WHITE

    @JvmField
    var textIsHtml: Boolean = false

    @JvmField
    var movementMethod: MovementMethod? = null

    @JvmField
    @Sp
    var textSize: Float = 12f

    @JvmField
    var textTypeface: Int = Typeface.NORMAL

    @JvmField
    var textTypefaceObject: Typeface? = null

    @JvmField
    var textGravity: Int = Gravity.CENTER


    @JvmField
    var iconDrawable: Drawable? = null


    @JvmField
    @Px
    var iconWidth: Int = 28.dp

    @JvmField
    @Px
    var iconHeight: Int = 28.dp

    @JvmField
    @Px
    var iconSpace: Int = 8.dp

    @JvmField
    @ColorInt
    var iconColor: Int = NO_INT_VALUE


    @JvmField
    @FloatRange(from = 0.0, to = 1.0)
    var alpha: Float = 1f

    @JvmField
    var elevation: Float = 2f.dp

    @JvmField
    var layout: View? = null

    @JvmField
    @LayoutRes
    var layoutRes: Int? = null


    @JvmField
    var onPopupMenuClickListener: OnPopupMenuClickListener? = null

    @JvmField
    var onPopupMenuDismissListener: OnPopupMenuDismissListener? = null

    @JvmField
    var onPopupMenuInitializedListener: OnPopupMenuInitializedListener? = null

    @JvmField
    var onPopupMenuOutsideTouchListener: OnPopupMenuOutsideTouchListener? = null


    @JvmField
    var onpopupmenuOverlayTouchListener: View.OnTouchListener? = null


    @JvmField
    var dismissWhenTouchOutside: Boolean = true

    @JvmField
    var dismissWhenShowAgain: Boolean = false

    @JvmField
    var dismissWhenClicked: Boolean = false

    @JvmField
    var dismissWhenOverlayClicked: Boolean = true

    @JvmField
    var dismissWhenLifecycleOnPause: Boolean = true

    @JvmField
    var autoDismissDuration: Long = NO_LONG_VALUE

    @JvmField
    var lifecycleOwner: LifecycleOwner? = null


    @JvmField
    var preferenceName: String? = null

    @JvmField
    var showTimes: Int = 1

    @JvmField
    var runIfReachedShowCounts: (() -> Unit)? = null

    @JvmField
    var isRtlLayout: Boolean =
      context.resources.configuration.layoutDirection == LayoutDirection.RTL

    @JvmField
    var supportRtlLayoutFactor: Int = LTR.unaryMinus(isRtlLayout)

    @JvmField
    var isFocusable: Boolean = true

    @JvmField
    var isStatusBarVisible: Boolean = true

    @JvmField
    var isAttachedInDecor: Boolean = true

    /** sets the width size. */
    fun setWidth(@Dp value: Int): Builder = apply {
      require(
        value > 0 || value == PopupMenuSizeSpec.WRAP
      ) { "The width of the popupmenu must bigger than zero." }
      this.width = value.dp
    }

    /** sets the width size using a dimension resource. */
    fun setWidthResource(@DimenRes value: Int): Builder = apply {
      this.width = context.dimenPixel(value)
    }

    /**
     * sets the minimum size of the width.
     * this functionality works only with the [popupmenuSizeSpec.WRAP].
     */
    fun setMinWidth(@Dp value: Int): Builder = apply {
      this.minWidth = value.dp
    }

    /**
     * sets the minimum size of the width using a dimension resource.
     * this functionality works only with the [popupmenuSizeSpec.WRAP].
     */
    fun setMinWidthResource(@DimenRes value: Int): Builder = apply {
      this.minWidth = context.dimenPixel(value)
    }

    /**
     * sets the maximum size of the width.
     * this functionality works only with the [popupmenuSizeSpec.WRAP].
     */
    fun setMaxWidth(@Dp value: Int): Builder = apply {
      this.maxWidth = value.dp
    }

    /**
     * sets the maximum size of the width using a dimension resource.
     * this functionality works only with the [popupmenuSizeSpec.WRAP].
     */
    fun setMaxWidthResource(@DimenRes value: Int): Builder = apply {
      this.maxWidth = context.dimenPixel(value)
    }

    /** sets the width size by the display screen size ratio. */
    fun setWidthRatio(
      @FloatRange(from = 0.0, to = 1.0) value: Float
    ): Builder = apply { this.widthRatio = value }

    /** sets the minimum width size by the display screen size ratio. */
    fun setMinWidthRatio(
      @FloatRange(from = 0.0, to = 1.0) value: Float
    ): Builder = apply { this.minWidthRatio = value }

    /** sets the maximum width size by the display screen size ratio. */
    fun setMaxWidthRatio(
      @FloatRange(from = 0.0, to = 1.0) value: Float
    ): Builder = apply { this.maxWidthRatio = value }

    /** sets the height size. */
    fun setHeight(@Dp value: Int): Builder = apply {
      require(
        value > 0 || value == PopupMenuSizeSpec.WRAP
      ) { "The height of the popupmenu must bigger than zero." }
      this.height = value.dp
    }

    /** sets the height size using a dimension resource. */
    fun setHeightResource(@DimenRes value: Int): Builder = apply {
      this.height = context.dimenPixel(value)
    }

    /** sets the width and height sizes of the popupmenu. */
    fun setSize(@Dp width: Int, @Dp height: Int): Builder = apply {
      setWidth(width)
      setHeight(height)
    }

    /** sets the width and height sizes of the popupmenu using a dimension resource. */
    fun setSizeResource(@DimenRes width: Int, @DimenRes height: Int): Builder = apply {
      setWidthResource(width)
      setHeightResource(height)
    }

    /** sets the padding on the popupmenu content all directions. */
    fun setPadding(@Dp value: Int): Builder = apply {
      setPaddingLeft(value)
      setPaddingTop(value)
      setPaddingRight(value)
      setPaddingBottom(value)
    }

    /** sets the padding on the popupmenu content all directions using dimension resource. */
    fun setPaddingResource(@DimenRes value: Int): Builder = apply {
      val padding = context.dimenPixel(value)
      this.paddingLeft = padding
      this.paddingTop = padding
      this.paddingRight = padding
      this.paddingBottom = padding
    }

    /** sets the horizontal (right and left) padding on the popupmenu content. */
    fun setPaddingHorizontal(@Dp value: Int): Builder = apply {
      setPaddingLeft(value)
      setPaddingRight(value)
    }

    /** sets the horizontal (right and left) padding on the popupmenu content using dimension resource. */
    fun setPaddingHorizontalResource(@DimenRes value: Int): Builder = apply {
      setPaddingLeftResource(value)
      setPaddingRightResource(value)
    }

    /** sets the vertical (top and bottom) padding on the popupmenu content. */
    fun setPaddingVertical(@Dp value: Int): Builder = apply {
      setPaddingTop(value)
      setPaddingBottom(value)
    }

    /** sets the vertical (top and bottom) padding on the popupmenu content using dimension resource. */
    fun setPaddingVerticalResource(@DimenRes value: Int): Builder = apply {
      setPaddingTopResource(value)
      setPaddingBottomResource(value)
    }

    /** sets the left padding on the popupmenu content. */
    fun setPaddingLeft(@Dp value: Int): Builder = apply { this.paddingLeft = value.dp }

    /** sets the left padding on the popupmenu content using dimension resource. */
    fun setPaddingLeftResource(@DimenRes value: Int): Builder = apply {
      this.paddingLeft = context.dimenPixel(value)
    }

    /** sets the top padding on the popupmenu content. */
    fun setPaddingTop(@Dp value: Int): Builder = apply { this.paddingTop = value.dp }

    /** sets the top padding on the popupmenu content using dimension resource. */
    fun setPaddingTopResource(@DimenRes value: Int): Builder = apply {
      this.paddingTop = context.dimenPixel(value)
    }

    /** sets the right padding on the popupmenu content. */
    fun setPaddingRight(@Dp value: Int): Builder = apply {
      this.paddingRight = value.dp
    }

    /** sets the right padding on the popupmenu content using dimension resource. */
    fun setPaddingRightResource(@DimenRes value: Int): Builder = apply {
      this.paddingRight = context.dimenPixel(value)
    }

    /** sets the bottom padding on the popupmenu content. */
    fun setPaddingBottom(@Dp value: Int): Builder = apply {
      this.paddingBottom = value.dp
    }

    /** sets the bottom padding on the popupmenu content using dimension resource. */
    fun setPaddingBottomResource(@DimenRes value: Int): Builder = apply {
      this.paddingBottom = context.dimenPixel(value)
    }

    /** sets the margin on the popupmenu all directions. */
    fun setMargin(@Dp value: Int): Builder = apply {
      setMarginLeft(value)
      setMarginTop(value)
      setMarginRight(value)
      setMarginBottom(value)
    }

    /** sets the margin on the popupmenu all directions using a dimension resource. */
    fun setMarginResource(@DimenRes value: Int): Builder = apply {
      val margin = context.dimenPixel(value)
      this.marginLeft = margin
      this.marginTop = margin
      this.marginRight = margin
      this.marginBottom = margin
    }

    /** sets the horizontal (left and right) margins on the popupmenu. */
    fun setMarginHorizontal(@Dp value: Int): Builder = apply {
      setMarginLeft(value)
      setMarginRight(value)
    }

    /** sets the horizontal (left and right) margins on the popupmenu using a dimension resource. */
    fun setMarginHorizontalResource(@DimenRes value: Int): Builder = apply {
      setMarginLeftResource(value)
      setMarginRightResource(value)
    }

    /** sets the vertical (top and bottom) margins on the popupmenu. */
    fun setMarginVertical(@Dp value: Int): Builder = apply {
      setMarginTop(value)
      setMarginBottom(value)
    }

    /** sets the vertical (top and bottom) margins on the popupmenu using a dimension resource. */
    fun setMarginVerticalResource(@DimenRes value: Int): Builder = apply {
      setMarginTopResource(value)
      setMarginBottomResource(value)
    }

    /** sets the left margin on the popupmenu. */
    fun setMarginLeft(@Dp value: Int): Builder = apply {
      this.marginLeft = value.dp
    }

    /** sets the left margin on the popupmenu using dimension resource. */
    fun setMarginLeftResource(@DimenRes value: Int): Builder = apply {
      this.marginLeft = context.dimenPixel(value)
    }

    /** sets the top margin on the popupmenu. */
    fun setMarginTop(@Dp value: Int): Builder = apply {
      this.marginTop = value.dp
    }

    /** sets the top margin on the popupmenu using dimension resource. */
    fun setMarginTopResource(@DimenRes value: Int): Builder = apply {
      this.marginTop = context.dimenPixel(value)
    }

    /** sets the right margin on the popupmenu. */
    fun setMarginRight(@Dp value: Int): Builder = apply {
      this.marginRight = value.dp
    }

    /** sets the right margin on the popupmenu using dimension resource. */
    fun setMarginRightResource(@DimenRes value: Int): Builder = apply {
      this.marginRight = context.dimenPixel(value)
    }

    /** sets the bottom margin on the popupmenu. */
    fun setMarginBottom(@Dp value: Int): Builder = apply {
      this.marginBottom = value.dp
    }

    /** sets the bottom margin on the popupmenu using dimension resource. */
    fun setMarginBottomResource(@DimenRes value: Int): Builder = apply {
      this.marginBottom = context.dimenPixel(value)
    }

    /** sets the visibility of the arrow. */
    fun setIsVisibleArrow(value: Boolean): Builder = apply { this.isVisibleArrow = value }

    /** sets a color of the arrow. */
    fun setArrowColor(@ColorInt value: Int): Builder = apply { this.arrowColor = value }

    /**
     * sets if arrow color should match the color of the popupmenu card.
     * Overrides [arrowColor]. Does not work with custom arrows.
     */
    fun setArrowColorMatchpopupmenu(value: Boolean): Builder = apply {
      this.arrowColorMatchpopupmenu = value
    }

    /** sets a color of the arrow using a resource. */
    fun setArrowColorResource(@ColorRes value: Int): Builder = apply {
      this.arrowColor = context.contextColor(value)
    }

    /** sets the size of the arrow. */
    fun setArrowSize(@Dp value: Int): Builder = apply {
      this.arrowSize =
        if (value == PopupMenuSizeSpec.WRAP) {
          PopupMenuSizeSpec.WRAP
        } else {
          value.dp
        }
    }

    /** sets the size of the arrow using dimension resource. */
    fun setArrowSizeResource(@DimenRes value: Int): Builder = apply {
      this.arrowSize = context.dimenPixel(value)
    }

    /** sets the arrow position by popup size ration. The popup size depends on [arrowOrientation]. */
    fun setArrowPosition(
      @FloatRange(from = 0.0, to = 1.0) value: Float
    ): Builder = apply { this.arrowPosition = value }

    /**
     * ArrowPositionRules determines the position of the arrow depending on the aligning rules.
     *
     * [ArrowPositionRules.ALIGN_popupmenu]: Align the arrow position depending on the popupmenu popup body.
     * [ArrowPositionRules.ALIGN_ANCHOR]: Align the arrow position depending on an anchor.
     */
    fun setArrowPositionRules(value: ArrowPositionRules) = apply { this.arrowPositionRules = value }

    /** sets the arrow orientation using [ArrowOrientation]. */
    fun setArrowOrientation(value: ArrowOrientation): Builder = apply {
      this.arrowOrientation = value
    }

    /**
     * ArrowOrientationRules determines the orientation of the arrow depending on the aligning rules.
     *
     * [ArrowOrientationRules.ALIGN_ANCHOR]: Align depending on the position of an anchor.
     * [ArrowOrientationRules.ALIGN_FIXED]: Align to fixed [ArrowOrientation].
     */
    fun setArrowOrientationRules(value: ArrowOrientationRules) = apply {
      this.arrowOrientationRules = value
    }

    /** sets a custom drawable of the arrow. */
    fun setArrowDrawable(value: Drawable?): Builder = apply {
      this.arrowDrawable = value?.mutate()
      if (value != null && arrowSize == PopupMenuSizeSpec.WRAP) {
        arrowSize = max(value.intrinsicWidth, value.intrinsicHeight)
      }
    }

    /** sets a custom drawable of the arrow using the resource. */
    fun setArrowDrawableResource(@DrawableRes value: Int): Builder = apply {
      setArrowDrawable(context.contextDrawable(value))
    }

    /** sets the left padding of the arrow. */
    fun setArrowLeftPadding(@Dp value: Int): Builder = apply {
      this.arrowLeftPadding = value.dp
    }

    /** sets the left padding of the arrow using the resource. */
    fun setArrowLeftPaddingResource(@DimenRes value: Int): Builder = apply {
      this.arrowLeftPadding = context.dimenPixel(value)
    }

    /** sets the right padding of the arrow. */
    fun setArrowRightPadding(@Dp value: Int): Builder = apply {
      this.arrowRightPadding = value.dp
    }

    /** sets the right padding of the arrow using the resource. */
    fun setArrowRightPaddingResource(@DimenRes value: Int): Builder = apply {
      this.arrowRightPadding = context.dimenPixel(value)
    }

    /** sets the top padding of the arrow. */
    fun setArrowTopPadding(@Dp value: Int): Builder = apply {
      this.arrowTopPadding = value.dp
    }

    /** sets the top padding of the arrow using the resource. */
    fun setArrowTopPaddingResource(@DimenRes value: Int): Builder = apply {
      this.arrowTopPadding = context.dimenPixel(value)
    }

    /** sets the bottom padding of the arrow. */
    fun setArrowBottomPadding(@Dp value: Int): Builder = apply {
      this.arrowBottomPadding = value.dp
    }

    /** sets the bottom padding of the arrow using the resource. */
    fun setArrowBottomPaddingResource(@DimenRes value: Int): Builder = apply {
      this.arrowBottomPadding = context.dimenPixel(value)
    }

    /** sets the padding of the arrow when aligning anchor using with [ArrowPositionRules.ALIGN_ANCHOR]. */
    fun setArrowAlignAnchorPadding(@Dp value: Int): Builder = apply {
      this.arrowAlignAnchorPadding = value.dp
    }

    /** sets the padding of the arrow the resource when aligning anchor using with [ArrowPositionRules.ALIGN_ANCHOR]. */
    fun setArrowAlignAnchorPaddingResource(@DimenRes value: Int): Builder = apply {
      this.arrowAlignAnchorPadding = context.dimenPixel(value)
    }

    /** sets the padding ratio of the arrow when aligning anchor using with [ArrowPositionRules.ALIGN_ANCHOR]. */
    fun setArrowAlignAnchorPaddingRatio(value: Float): Builder = apply {
      this.arrowAlignAnchorPaddingRatio = value
    }

    /** sets the elevation of the arrow. */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun setArrowElevation(@Dp value: Int): Builder = apply {
      this.arrowElevation = value.dp.toFloat()
    }

    /** sets the elevation of the arrow using dimension resource. */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun setArrowElevationResource(@DimenRes value: Int): Builder = apply {
      this.arrowElevation = context.dimen(value)
    }

    /** sets the background color of the arrow and popup. */
    fun setBackgroundColor(@ColorInt value: Int): Builder = apply { this.backgroundColor = value }

    /** sets the background color of the arrow and popup using the resource color. */
    fun setBackgroundColorResource(@ColorRes value: Int): Builder = apply {
      this.backgroundColor = context.contextColor(value)
    }

    /** sets the background drawable of the popup. */
    fun setBackgroundDrawable(value: Drawable?): Builder = apply {
      this.backgroundDrawable = value?.mutate()
    }

    /** sets the background drawable of the popup by the resource. */
    fun setBackgroundDrawableResource(@DrawableRes value: Int): Builder = apply {
      this.backgroundDrawable = context.contextDrawable(value)?.mutate()
    }

    /** sets the corner radius of the popup. */
    fun setCornerRadius(@Dp value: Float): Builder = apply {
      this.cornerRadius = value.dp
    }

    /** sets the corner radius of the popup using dimension resource. */
    fun setCornerRadiusResource(@DimenRes value: Int): Builder = apply {
      this.cornerRadius = context.dimen(value)
    }

    /** sets the main text content of the popup. */
    fun setText(value: CharSequence): Builder = apply { this.text = value }

    /** sets the main text content of the popup using resource. */
    fun setTextResource(@StringRes value: Int): Builder = apply {
      this.text = context.getString(value)
    }

    /** sets the color of the main text content. */
    fun setTextColor(@ColorInt value: Int): Builder = apply { this.textColor = value }

    /** sets the color of the main text content using the resource color. */
    fun setTextColorResource(@ColorRes value: Int): Builder = apply {
      this.textColor = context.contextColor(value)
    }

    /** sets whether the text will be parsed as HTML (using Html.fromHtml(..)) */
    fun setTextIsHtml(value: Boolean): Builder = apply { this.textIsHtml = value }

    /** sets the movement method for TextView. */
    fun setMovementMethod(value: MovementMethod): Builder = apply { this.movementMethod = value }

    /** sets the size of the main text content. */
    fun setTextSize(@Sp value: Float): Builder = apply { this.textSize = value }

    /** sets the size of the main text content using dimension resource. */
    fun setTextSizeResource(@DimenRes value: Int) = apply {
      this.textSize = context.px2Sp(context.dimen(value))
    }

    /** sets the typeface of the main text content. */
    fun setTextTypeface(value: Int): Builder = apply { this.textTypeface = value }

    /** sets the typeface of the main text content. */
    fun setTextTypeface(value: Typeface): Builder = apply { this.textTypefaceObject = value }

    /**
     * sets gravity of the text.
     * this only works when the width or setWidthRatio set explicitly.
     */
    fun setTextGravity(value: Int): Builder = apply {
      this.textGravity = value
    }


    /** sets the icon drawable of the popup. */
    fun setIconDrawable(value: Drawable?): Builder = apply { this.iconDrawable = value?.mutate() }

    /** sets the icon drawable of the popup using the resource. */
    fun setIconDrawableResource(@DrawableRes value: Int) = apply {
      this.iconDrawable = context.contextDrawable(value)?.mutate()
    }

    /** sets the width size of the icon drawable. */
    fun setIconWidth(@Dp value: Int): Builder = apply {
      this.iconWidth = value.dp
    }

    /** sets the width size of the icon drawable using the dimension resource. */
    fun setIconWidthResource(@DimenRes value: Int): Builder = apply {
      this.iconWidth = context.dimenPixel(value)
    }

    /** sets the height size of the icon drawable. */
    fun setIconHeight(@Dp value: Int): Builder = apply {
      this.iconHeight = value.dp
    }

    /** sets the height size of the icon drawable using the dimension resource. */
    fun setIconHeightResource(@DimenRes value: Int): Builder = apply {
      this.iconHeight = context.dimenPixel(value)
    }

    /** sets the size of the icon drawable. */
    fun setIconSize(@Dp value: Int): Builder = apply {
      setIconWidth(value)
      setIconHeight(value)
    }

    /** sets the size of the icon drawable using the dimension resource. */
    fun setIconSizeResource(@DimenRes value: Int): Builder = apply {
      setIconWidthResource(value)
      setIconHeightResource(value)
    }

    /** sets the color of the icon drawable. */
    fun setIconColor(@ColorInt value: Int): Builder = apply { this.iconColor = value }

    /** sets the color of the icon drawable using the resource color. */
    fun setIconColorResource(@ColorRes value: Int): Builder = apply {
      this.iconColor = context.contextColor(value)
    }

    /** sets the space between the icon and the main text content. */
    fun setIconSpace(@Dp value: Int): Builder = apply { this.iconSpace = value.dp }

    /** sets the space between the icon and the main text content using dimension resource. */
    fun setIconSpaceResource(@DimenRes value: Int): Builder = apply {
      this.iconSpace = context.dimenPixel(value)
    }


    /** sets the alpha value to the popup. */
    fun setAlpha(@FloatRange(from = 0.0, to = 1.0) value: Float): Builder = apply {
      this.alpha = value
    }

    /** sets the elevation to the popup. */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun setElevation(@Dp value: Int): Builder = apply {
      this.elevation = value.dp.toFloat()
    }

    /** sets the elevation to the popup using dimension resource. */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun setElevationResource(@DimenRes value: Int): Builder = apply {
      this.elevation = context.dimen(value)
    }

    /** sets the custom layout resource to the popup content. */
    fun setLayout(@LayoutRes layoutRes: Int): Builder = apply { this.layoutRes = layoutRes }

    /** sets the custom layout view to the popup content. */
    fun setLayout(layout: View): Builder = apply { this.layout = layout }

    /** sets is status bar is visible or not in your screen. */
    fun setIsStatusBarVisible(value: Boolean) = apply {
      this.isStatusBarVisible = value
    }

    /**
     * sets whether the popup window will be attached in the decor frame of its parent window.
     * If you want to show up popupmenu on your DialogFragment, it's recommended to use with true. (#131)
     */
    fun setIsAttachedInDecor(value: Boolean) = apply {
      this.isAttachedInDecor = value
    }

    /**
     * sets the [LifecycleOwner] for dismissing automatically when the [LifecycleOwner] is destroyed.
     * It will prevents memory leak : [Avoid Memory Leak](https://github.com/skydoves/popupmenu#avoid-memory-leak)
     */
    fun setLifecycleOwner(value: LifecycleOwner?): Builder = apply { this.lifecycleOwner = value }



//    /** sets a [OnPopupMenuClickListener] to the popup. */
//    fun setOnpopupmenuClickListener(value: OnPopupMenuClickListener): Builder = apply {
//      this.onPopupMenuClickListener = value
//    }
//
//    /** sets a [OnPopupMenuDismissListener] to the popup. */
//    fun setOnpopupmenuDismissListener(value: OnPopupMenuDismissListener): Builder = apply {
//      this.onPopupMenuDismissListener = value
//    }
//
//    /** sets a [OnPopupMenuInitializedListener] to the popup. */
//    fun setOnpopupmenuInitializedListener(value: OnPopupMenuInitializedListener): Builder = apply {
//      this.onPopupMenuInitializedListener = value
//    }
//
//    /** sets a [OnPopupMenuOutsideTouchListener] to the popup. */
//    fun setOnpopupmenuOutsideTouchListener(value: OnPopupMenuOutsideTouchListener): Builder = apply {
//      this.onPopupMenuOutsideTouchListener = value
//    }
//
//
//    /** sets a [OnPopupMenuClickListener] to the popup using lambda. */
//
//    fun setOnpopupmenuClickListener(block: (View) -> Unit): Builder = apply {
//      this.onPopupMenuClickListener = OnPopupMenuClickListener(block)
//    }
//
//    /** sets a [OnPopupMenuDismissListener] to the popup using lambda. */
//
//    fun setOnpopupmenuDismissListener(block: () -> Unit): Builder = apply {
//      this.onPopupMenuDismissListener = OnPopupMenuDismissListener(block)
//    }
//
//    /** sets a [OnPopupMenuInitializedListener] to the popup using lambda. */
//
//    fun setOnpopupmenuInitializedListener(block: (View) -> Unit): Builder = apply {
//      this.onPopupMenuInitializedListener = OnPopupMenuInitializedListener(block)
//    }
//
//    /** sets a [OnPopupMenuOutsideTouchListener] to the popup using lambda. */
//
    fun setOnpopupmenuOutsideTouchListener(block: (View, MotionEvent) -> Unit): Builder = apply {
      this.onPopupMenuOutsideTouchListener = OnPopupMenuOutsideTouchListener(block)
      setDismissWhenTouchOutside(false)
    }


    /** dismisses when touch outside. */
    fun setDismissWhenTouchOutside(value: Boolean): Builder = apply {
      this.dismissWhenTouchOutside = value
      if (!value) {
        setFocusable(value)
      }
    }

    /** dismisses when invoked show function again. */
    fun setDismissWhenShowAgain(value: Boolean): Builder = apply {
      this.dismissWhenShowAgain = value
    }

    /** dismisses when the popup clicked. */
    fun setDismissWhenClicked(value: Boolean): Builder = apply { this.dismissWhenClicked = value }

    /** dismisses when the [LifecycleOwner] be on paused. */
    fun setDismissWhenLifecycleOnPause(value: Boolean): Builder = apply {
      this.dismissWhenLifecycleOnPause = value
    }
//
//    /** dismisses automatically some milliseconds later when the popup is shown. */
//    fun setAutoDismissDuration(value: Long): Builder = apply { this.autoDismissDuration = value }

//    /**
//     * sets the preference name for persisting showing counts.
//     * This method should be used with the [setShowCounts].
//     *
//     * @see (https://github.com/skydoves/popupmenu#persistence)
//     */
//    fun setPreferenceName(value: String): Builder = apply { this.preferenceName = value }
//
//    /**
//     * sets showing counts which how many times the popupmenu popup will be shown up.
//     * This method should be used with the [setPreferenceName].
//     *
//     * @see (https://github.com/skydoves/popupmenu#persistence)
//     */
//    fun setShowCounts(value: Int): Builder = apply { this.showTimes = value }
//
//    /**
//     * sets a lambda for invoking after the preference showing counts is reached the goal.
//     * This method should be used ith the [setPreferenceName] and [setShowCounts].
//     *
//     * @see (https://github.com/skydoves/popupmenu#persistence)
//     *
//     * @param block A lambda for invoking after the preference showing counts is reached the goal.
//     */
//    fun runIfReachedShowCounts(block: () -> Unit): Builder = apply {
//      runIfReachedShowCounts = block
//    }
//
//    /**
//     * sets a [Runnable] for invoking after the preference showing counts is reached the goal.
//     * This method should be used ith the [setPreferenceName] and [setShowCounts].
//     *
//     * @see (https://github.com/skydoves/popupmenu#persistence)
//     *
//     * @param runnable A [Runnable] for invoking after the preference showing counts is reached the goal.
//     */
//    fun runIfReachedShowCounts(runnable: Runnable): Builder = apply {
//      runIfReachedShowCounts { runnable.run() }
//    }

    /**
     * sets isFocusable option to the body window.
     * if true when the popupmenu is showing, can not touch other views and
     * onBackPressed will be fired to the popupmenu.
     * */
    fun setFocusable(value: Boolean): Builder = apply { this.isFocusable = value }

    /**
     * Create a new instance of the [PaytmOverflowMenu] which includes customized attributes.
     *
     * @return A new created instance of the [PaytmOverflowMenu].
     */
    fun build(): PaytmOverflowMenu = PaytmOverflowMenu(
      context = context,
      builder = this@Builder
    )
  }

  /**
   * An abstract factory class for creating [PaytmOverflowMenu] instance.
   * A factory implementation class must have a default (non-argument) constructor.
   * This class is used to initialize an instance of the [PaytmOverflowMenu] lazily in Activities and Fragments.
   *
   * @see [Lazy Initialization](https://github.com/skydoves/popupmenu#lazy-initialization)
   */
  abstract class Factory {

    /**
     * Creates a new instance of [PaytmOverflowMenu].
     *
     * @return A new created instance of the [PaytmOverflowMenu].
     */
    abstract fun create(context: Context, lifecycle: LifecycleOwner?): PaytmOverflowMenu
  }
}
