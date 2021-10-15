package com.rohith.mycustompopupmenu.secondKit.popupMenuUtil.radiusLayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.Px

/**
 * RadiusLayout clips four directions of inner layouts depending on the radius size.
 */
class RadiusLayout @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attr, defStyle) {

    /** path for smoothing the container's corner. */
    private val path = Path()

    /** corner radius for the clipping corners. */
    @Px
    private var _radius: Float = 0f
    var radius: Float
        @Px get() = _radius
        set(@Px value) {
            _radius = value
            invalidate()
        }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) {
        super.onSizeChanged(w, h, oldw, oldh)
        path.apply {
            addRoundRect(
                RectF(0f, 0f, w.toFloat(), h.toFloat()),
                radius, radius,
                Path.Direction.CW
            )
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
    }
}
