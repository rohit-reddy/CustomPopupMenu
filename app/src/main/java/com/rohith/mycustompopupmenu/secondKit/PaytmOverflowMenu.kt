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
import android.util.Pair
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
import com.rohith.mycustompopupmenu.secondKit.PopupMenuUtil.*
import com.rohith.mycustompopupmenu.secondKit.annotations.Dp
import android.util.DisplayMetrics
import android.util.TypedValue


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
    val bodyWindow: CustomPopupWindow = CustomPopupWindow(
        context,
        binding.root,
        FrameLayout.LayoutParams.WRAP_CONTENT,
        FrameLayout.LayoutParams.WRAP_CONTENT
    )


    /** Denotes the popup is showing or not. */
    var isShowing = false
        private set

    /** Denotes the popup is already destroyed internally. */
    private var destroyed: Boolean = false


    init {
        createByBuilder()
    }

    private fun createByBuilder() {
        initializeBackground()
        initializepopupmenuWindow()
        initializepopupmenuLayout()
        initializepopupmenuContent()

        adjustFitsSystemWindows(binding.root)

        if (builder.getLifecycleOwner() == null && context is LifecycleOwner) {
            builder.setLifecycleOwner(context)
            context.lifecycle.addObserver(this@PaytmOverflowMenu)
        } else {
            builder.getLifecycleOwner()?.lifecycle?.addObserver(this@PaytmOverflowMenu)
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
        return (builder.getArrowSize().toFloat() * builder.getArrowAlignAnchorPaddingRatio())
    }


    private fun getMaxArrowPosition(): Float {
        return getMeasuredWidth().toFloat() - builder.getArrowSize() - SIZE_ARROW_BOUNDARY - getMinArrowPosition()
    }

    private fun getDoubleArrowSize(): Int {
        return builder.getArrowSize() * 2
    }


    private fun initializeArrow(anchor: View) {
        with(binding.popupmenuArrow) {
            layoutParams = FrameLayout.LayoutParams(builder.getArrowSize(), builder.getArrowSize())
            alpha = builder.getAlpha()
            ImageViewCompat.setImageTintList(
                this,
                ColorStateList.valueOf(builder.getBackgroundColor())
            )
            runOnAfterSDK21 {
                outlineProvider = ViewOutlineProvider.BOUNDS
            }
            binding.popupmenuCard.post {
                adjustArrowOrientationByRules(anchor)

                @SuppressLint("NewApi")
                when (builder.getArrowOrientation()) {
                    PopupMenuAlignment.BOTTOMLEFT, PopupMenuAlignment.BOTTOMRIGHT -> {
                        rotation = 180f
                        x = getArrowConstraintPositionX(anchor)
                        y =
                            binding.popupmenuCard.y + binding.popupmenuCard.height - SIZE_ARROW_BOUNDARY
                        foreground = BitmapDrawable(
                            resources,
                            adjustArrowColorByMatchingCardBackground(
                                this, x,
                                binding.popupmenuCard.height.toFloat()
                            )
                        )
                    }
                    PopupMenuAlignment.TOPLEFT, PopupMenuAlignment.TOPRIGHT -> {
                        rotation = 0f
                        x = getArrowConstraintPositionX(anchor)
                        y = binding.popupmenuCard.y - builder.getArrowSize() + SIZE_ARROW_BOUNDARY
                        foreground =
                            BitmapDrawable(
                                resources,
                                adjustArrowColorByMatchingCardBackground(this, x, 0f)
                            )
                    }
                }
                visible(true)
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
        imageView.setColorFilter(builder.getBackgroundColor(), PorterDuff.Mode.SRC_IN)
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
        val shader: LinearGradient = when (builder.getArrowOrientation()) {
            PopupMenuAlignment.BOTTOMLEFT, PopupMenuAlignment.TOPLEFT -> {
                LinearGradient(
                    oldBitmap.width.toFloat() / 2 - builder.getHalfArrowSize(), 0f,
                    oldBitmap.width.toFloat(), 0f, startColor, endColor, Shader.TileMode.CLAMP
                )
            }
            PopupMenuAlignment.BOTTOMRIGHT, PopupMenuAlignment.TOPRIGHT -> {
                LinearGradient(
                    oldBitmap.width.toFloat() / 2 + builder.getHalfArrowSize(), 0f, 0f, 0f,
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

        val startColor: Int = bitmap.getPixel((x + builder.getHalfArrowSize()).toInt(), y.toInt())
        val endColor: Int = bitmap.getPixel((x - builder.getHalfArrowSize()).toInt(), y.toInt())

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
        val anchorRect = Rect()
        anchor.getGlobalVisibleRect(anchorRect)

        val windowRect = Rect()
        bodyWindow.contentView.getGlobalVisibleRect(windowRect)

        val location: IntArray = intArrayOf(0, 0)
        bodyWindow.contentView.getLocationOnScreen(location)

        if (builder.getArrowOrientation() == (PopupMenuAlignment.TOPLEFT) &&
            location[1] < anchorRect.bottom
        ) {
            builder.setArrowOrientation(PopupMenuAlignment.BOTTOMLEFT)
        } else if (builder.getArrowOrientation() == PopupMenuAlignment.TOPRIGHT &&
            location[1] < anchorRect.bottom
        ) {
            builder.setArrowOrientation(PopupMenuAlignment.BOTTOMRIGHT)
        } else if (builder.getArrowOrientation() == PopupMenuAlignment.BOTTOMRIGHT &&
            location[1] > anchorRect.top
        ) {
            builder.setArrowOrientation(PopupMenuAlignment.TOPRIGHT)
        } else if (builder.getArrowOrientation() == PopupMenuAlignment.BOTTOMLEFT &&
            location[1] > anchorRect.top
        ) {
            builder.setArrowOrientation(PopupMenuAlignment.TOPLEFT)
        }

        initializepopupmenuContent()
    }

    private fun getArrowConstraintPositionX(anchor: View): Float {
        val popupmenuX: Int = binding.popupmenuContent.getViewPointOnScreen().x
        val anchorX: Int = anchor.getViewPointOnScreen().x
        val minPosition = getMinArrowPosition()
        val maxPosition1 = binding.popupmenuCard.measuredWidth - minPosition
        val maxPosition =
            getMeasuredWidth().toFloat() - builder.getArrowSize() - SIZE_ARROW_BOUNDARY - minPosition

        return when (builder.getArrowOrientation()) {
            PopupMenuAlignment.TOPLEFT -> minPosition
            PopupMenuAlignment.TOPRIGHT -> maxPosition
            PopupMenuAlignment.BOTTOMLEFT -> minPosition
            PopupMenuAlignment.BOTTOMRIGHT -> maxPosition
        }
    }


    private fun initializeBackground() {
        with(binding.popupmenuCard) {
            alpha = builder.getAlpha()
            radius = builder.getCornerRadius()
            ViewCompat.setElevation(this, builder.getElevation())
            background = GradientDrawable().apply {
                setColor(builder.getBackgroundColor())
                cornerRadius = builder.getCornerRadius()
            }
        }
    }

    private fun initializepopupmenuWindow() {
        with(this.bodyWindow) {
            isOutsideTouchable = true
            isFocusable = builder.getIsFocusable()
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            runOnAfterSDK21 {
                elevation = builder.getElevation()
            }
            setIsAttachedInDecor(builder.getIsAttachedInDecor())
        }
    }


    private fun initializepopupmenuContent() {
        val paddingSize = builder.getArrowSize() - SIZE_ARROW_BOUNDARY
        val elevation = builder.getElevation().toInt()
        with(binding.popupmenuContent) {
            when (builder.getArrowOrientation()) {
                PopupMenuAlignment.TOPRIGHT, PopupMenuAlignment.TOPLEFT ->
                    setPadding(
                        elevation,
                        paddingSize,
                        elevation,
                        paddingSize.coerceAtLeast(elevation)
                    )
                PopupMenuAlignment.BOTTOMRIGHT, PopupMenuAlignment.BOTTOMLEFT ->
                    setPadding(
                        elevation,
                        paddingSize,
                        elevation,
                        paddingSize.coerceAtLeast(elevation)
                    )

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
        return builder.getLayoutRes() != null || builder.getLayout() != null
    }

    /** Initializes the popupmenu content using the custom layout. */
    private fun initializeCustomLayout() {
        val layout = builder.getLayoutRes()?.let {
            LayoutInflater.from(context).inflate(it, binding.popupmenuCard, false)
        } ?: builder.getLayout() ?: throw IllegalArgumentException("The custom layout is null.")
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

                if (hasCustomLayout()) {
                    traverseAndMeasureTextWidth(binding.popupmenuCard)
                }
                this.binding.root.measure(
                    View.MeasureSpec.UNSPECIFIED,
                    View.MeasureSpec.UNSPECIFIED
                )
                this.bodyWindow.width = getMeasuredWidth()
                val maxAvailableHeight = this.bodyWindow.getMaxAvailableHeight(anchor)
                this.bodyWindow.height = getMeasuredHeight(maxAvailableHeight)

                initializeArrow(anchor)
                initializepopupmenuContent()

                block()
            }
        } else if (builder.getDismissWhenShowAgain()) {
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
     * @param alignment A rule for deciding the alignment of the popupmenu.
     */

@JvmOverloads
fun show(
    anchor: View,
    xOff: Int = 0,
    yOff: Int = 0,
    alignment: PopupMenuAlignment = PopupMenuAlignment.BOTTOMLEFT
) {
    show(anchor) {
        when (alignment) {
            PopupMenuAlignment.BOTTOMLEFT -> {
                val popupmenuX: Int = binding.popupmenuContent.getViewPointOnScreen().x
                val anchorX: Int = anchor.getViewPointOnScreen().x
                val anchorRect = Rect()
                anchor.getGlobalVisibleRect(anchorRect)
                bodyWindow.showAsDropDown(
                    anchor,
                    ((((anchorRect.right + anchorRect.left) / 2) - anchorRect.left - getMinArrowPosition().toInt() - builder.getHalfArrowSize()).toInt()) + xOff,
                    -getMeasuredHeight(this.bodyWindow.getMaxAvailableHeight(anchor)) - anchor.measuredHeight + yOff,
                    Gravity.START or Gravity.LEFT
                )
            }

            PopupMenuAlignment.TOPLEFT -> {
                val popupmenuX: Int = binding.popupmenuContent.getViewPointOnScreen().x
                val anchorX: Int = anchor.getViewPointOnScreen().x
                val anchorRect = Rect()
                anchor.getGlobalVisibleRect(anchorRect)
                bodyWindow.showAsDropDown(
                    anchor,
                    (((anchorRect.right + anchorRect.left) / 2) - anchorRect.left - getMinArrowPosition().toInt() - builder.getHalfArrowSize()).toInt() + xOff,
                    yOff,
                    Gravity.START or Gravity.LEFT
                )
            }

            PopupMenuAlignment.BOTTOMRIGHT -> {
                val anchorX: Int = anchor.getViewPointOnScreen().x

                val displayMetrics = context.resources.displayMetrics
                val height = displayMetrics.heightPixels
                val width = displayMetrics.widthPixels
                println("Height:$height")
                println("Width:$width")
                val anchorRect = Rect()
                anchor.getGlobalVisibleRect(anchorRect)

                bodyWindow.showAsDropDown(
                    anchor,
                    (-(anchorRect.right - ((anchorRect.left + anchorRect.right) / 2)) + (getMinArrowPosition() + builder.getArrowSize() - SIZE_ARROW_BOUNDARY) - builder.getHalfArrowSize()).toInt() + xOff,
                    -getMeasuredHeight(this.bodyWindow.getMaxAvailableHeight(anchor)) - anchor.measuredHeight + yOff,
                    Gravity.RIGHT or Gravity.END
                )

                }

            PopupMenuAlignment.TOPRIGHT -> {
                val anchorX: Int = anchor.getViewPointOnScreen().x
                val displayMetrics = context.resources.displayMetrics
                val height = displayMetrics.heightPixels
                val width = displayMetrics.widthPixels
                println("Height:$height")
                println("Width:$width")
                val anchorRect = Rect()
                anchor.getGlobalVisibleRect(anchorRect)

                bodyWindow.showAsDropDown(
                    anchor,
                    (-(anchorRect.right - ((anchorRect.left + anchorRect.right) / 2)) + getMinArrowPosition().toInt() + builder.getArrowSize() - SIZE_ARROW_BOUNDARY - builder.getHalfArrowSize()).toInt() + xOff,
                    yOff, Gravity.RIGHT or Gravity.END
                )

                }

            }
        }
    }

    /** dismiss the popup menu. */
    fun dismiss() {
        if (this.isShowing) {
            val dismissWindow: () -> Unit = {
                this.isShowing = false
                this.bodyWindow.dismiss()
            }
            dismissWindow()
        }
    }

    /** sets a [OnPopupMenuClickListener] to the popup. */
    fun setOnPopupMenuClickListener(onPopupMenuClickListener: OnPopupMenuClickListener?) {
        this.binding.popupmenuWrapper.setOnClickListener {
            onPopupMenuClickListener?.onpopupmenuClick(it)
            if (builder.getDismissWhenShowAgain()) dismiss()
        }
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
                        if (builder.getDismissWhenShowAgain()) {
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
        return binding.root.measuredWidth.coerceIn(builder.getMinWidth(), builder.getMaxWidth())
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
        val spaces = rootView.paddingLeft + rootView.paddingRight
        val maxTextWidth = builder.getMaxWidth() - spaces
        return measuredWidth.coerceAtMost(maxTextWidth)
    }

    /** gets measured height size of the popupmenu popup. */
    fun getMeasuredHeight(maxHeight : Int): Int {
        val tv = TypedValue()
        var actionBarHeight = 0
        if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)){
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.resources.displayMetrics)
        }
        val displayHeight = displaySize.y
        println("Height:$displayHeight")
        return this.binding.root.measuredHeight.coerceAtMost(maxHeight - actionBarHeight)
        //return this.binding.root.measuredHeight
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
        if (builder.getDismissWhenLifecycleOnPause()) {
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

        @Px
        private var minWidth: Int = 184.dp

        @Px
        private var maxWidth: Int = 280.dp

        @Px
        private var arrowSize: Int = 12.dp

        private val arrowHalfSize: Float
            @Px
            inline get() = arrowSize * 0.5f

        @FloatRange(from = 0.0, to = 1.0)
        private var arrowPosition: Float = 0.5f

        private var arrowPositionRules: ArrowPositionRules = ArrowPositionRules.ALIGN_ANCHOR

        private var arrowAlignment: PopupMenuAlignment = PopupMenuAlignment.BOTTOMLEFT

        private var arrowAlignAnchorPaddingRatio: Float = 1.5f

        @ColorInt
        private var backgroundColor: Int = Color.WHITE

        @Px
        private var cornerRadius: Float = 8f.dp

        @FloatRange(from = 0.0, to = 1.0)
        private var alpha: Float = 1f

        private var elevation: Float = 6f.dp

        private var layout: View? = null

        @LayoutRes
        private var layoutRes: Int? = null

        private var dismissWhenTouchOutside: Boolean = true

        private var dismissWhenShowAgain: Boolean = true

        private var dismissWhenClicked: Boolean = false

        private var dismissWhenLifecycleOnPause: Boolean = true

        private var lifecycleOwner: LifecycleOwner? = null

        private var isFocusable: Boolean = true

        private var isAttachedInDecor: Boolean = true


    fun getHalfArrowSize() = this.arrowHalfSize

        fun getArrowSize() = this.arrowSize

        fun getMaxWidth() = this.maxWidth

        fun getArrowAlignAnchorPaddingRatio() = this.arrowAlignAnchorPaddingRatio

        /** sets the arrow orientation using [ArrowOrientation]. */
        fun setArrowOrientation(value: PopupMenuAlignment): Builder = apply {
            this.arrowAlignment = value
        }

        fun getMinWidth() = this.minWidth

        fun getArrowOrientation() = this.arrowAlignment

        fun getCornerRadius() = this.cornerRadius

        fun getIsFocusable() = this.isFocusable

        /** sets the background color of the arrow and popup. */
        fun setBackgroundColor(@ColorInt value: Int): Builder =
            apply { this.backgroundColor = value }

        fun getBackgroundColor() = this.backgroundColor

        /** sets the background color of the arrow and popup using the resource color. */
        fun setBackgroundColorResource(@ColorRes value: Int): Builder = apply {
            this.backgroundColor = context.contextColor(value)
        }

        /** sets the alpha value to the popup. */
        fun setAlpha(@FloatRange(from = 0.0, to = 1.0) value: Float): Builder = apply {
            this.alpha = value
        }

        fun getAlpha() = this.alpha

        /** sets the elevation to the popup. */
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun setElevation(@Dp value: Int): Builder = apply {
            this.elevation = value.dp.toFloat()
        }

        fun getElevation() = this.elevation

        /** sets the elevation to the popup using dimension resource. */
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun setElevationResource(@DimenRes value: Int): Builder = apply {
            this.elevation = context.dimen(value)
        }

        /** sets the custom layout resource to the popup content. */
        fun setLayout(@LayoutRes layoutRes: Int): Builder = apply { this.layoutRes = layoutRes }

        fun getLayoutRes() = this.layoutRes

        /** sets the custom layout view to the popup content. */
        fun setLayout(layout: View): Builder = apply { this.layout = layout }

        fun getLayout() = this.layout

        /**
         * sets whether the popup window will be attached in the decor frame of its parent window.
         * If you want to show up popupmenu on your DialogFragment, it's recommended to use with true. (#131)
         */
        fun setIsAttachedInDecor(value: Boolean) = apply {
            this.isAttachedInDecor = value
        }

        fun getIsAttachedInDecor() = this.isAttachedInDecor

        /**
         * sets the [LifecycleOwner] for dismissing automatically when the [LifecycleOwner] is destroyed.
         * It will prevents memory leak : [Avoid Memory Leak](https://github.com/skydoves/popupmenu#avoid-memory-leak)
         */
        fun setLifecycleOwner(value: LifecycleOwner?): Builder =
            apply { this.lifecycleOwner = value }

        fun getLifecycleOwner() = this.lifecycleOwner

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

        fun getDismissWhenShowAgain() = this.dismissWhenShowAgain

        /** dismisses when the popup clicked. */
        fun setDismissWhenClicked(value: Boolean): Builder =
            apply { this.dismissWhenClicked = value }

        /** dismisses when the [LifecycleOwner] be on paused. */
        fun setDismissWhenLifecycleOnPause(value: Boolean): Builder = apply {
            this.dismissWhenLifecycleOnPause = value
        }

        fun getDismissWhenLifecycleOnPause() = this.dismissWhenLifecycleOnPause

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
        abstract fun create(context: Context, lifecycle: LifecycleOwner?,  value: PopupMenuAlignment): PaytmOverflowMenu
    }
}
