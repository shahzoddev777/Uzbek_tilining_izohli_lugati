package shahzod.projects.ozbektiliningizohliugati.widget

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.PathInterpolator
import android.widget.CompoundButton
import androidx.core.graphics.ColorUtils

class LiquidGlassSwitch @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CompoundButton(context, attrs, defStyleAttr) {

    private var position = 0f
    private var touchScale = 1.0f
    private var thumbStretch = 0f

    private var positionAnimator: ValueAnimator? = null
    private var scaleAnimator: ValueAnimator? = null

    private val iosInterpolator = PathInterpolator(0.25f, 0.1f, 0.25f, 1.0f)

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val trackBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(1f)
    }
    private val trackHighlightPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val thumbShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(0.75f)
    }

    private val trackRect = RectF()
    private val thumbRect = RectF()

    private val defaultWidthPx = dpToPx(52f)
    private val defaultHeightPx = dpToPx(31f)
    private val paddingPx = dpToPx(2.5f)

    init {
        buttonDrawable = null
        background = null
        isClickable = true
        isFocusable = true
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        position = if (isChecked) 1f else 0f
    }

    override fun setChecked(checked: Boolean) {
        val changed = isChecked != checked
        super.setChecked(checked)
        if (changed) {
            if (isAttachedToWindow) {
                animatePosition(checked)
            } else {
                position = if (checked) 1f else 0f
                invalidate()
            }
        }
    }

    fun setChecked(checked: Boolean, animate: Boolean) {
        if (!animate) {
            positionAnimator?.cancel()
            super.setChecked(checked)
            position = if (checked) 1f else 0f
            invalidate()
        } else {
            setChecked(checked)
        }
    }

    private fun animatePosition(toChecked: Boolean) {
        val target = if (toChecked) 1f else 0f
        if (position == target && positionAnimator?.isRunning != true) return

        positionAnimator?.cancel()
        positionAnimator = ValueAnimator.ofFloat(position, target).apply {
            duration = 260
            interpolator = iosInterpolator
            addUpdateListener {
                position = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun animateScale(targetScale: Float) {
        scaleAnimator?.cancel()
        scaleAnimator = ValueAnimator.ofFloat(touchScale, targetScale).apply {
            duration = 180
            interpolator = iosInterpolator
            addUpdateListener {
                touchScale = it.animatedValue as Float
                thumbStretch = (1.0f - touchScale) * 30f
                invalidate()
            }
            start()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                animateScale(0.95f)
                return true
            }
            MotionEvent.ACTION_UP -> {
                animateScale(1.0f)
                toggle()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                animateScale(1.0f)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredW = (defaultWidthPx + paddingLeft + paddingRight).toInt()
        val desiredH = (defaultHeightPx + paddingTop + paddingBottom).toInt()
        val w = resolveSize(desiredW, widthMeasureSpec)
        val h = resolveSize(desiredH, heightMeasureSpec)
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat() - paddingLeft - paddingRight
        val h = height.toFloat() - paddingTop - paddingBottom

        if (w <= 0 || h <= 0) return

        canvas.save()
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())

        val cx = w / 2f
        val cy = h / 2f
        canvas.scale(touchScale, touchScale, cx, cy)

        val isNight = isNightMode()

        val offTrackColor = if (isNight) Color.parseColor("#333A4568") else Color.parseColor("#22000000")
        val onTrackColor = if (isNight) Color.parseColor("#EB2563EB") else Color.parseColor("#F02563EB")
        val currentTrackColor = ColorUtils.blendARGB(offTrackColor, onTrackColor, position)

        val offBorderColor = if (isNight) Color.parseColor("#40FFFFFF") else Color.parseColor("#20000000")
        val onBorderColor = if (isNight) Color.parseColor("#60FFFFFF") else Color.parseColor("#40FFFFFF")
        val currentBorderColor = ColorUtils.blendARGB(offBorderColor, onBorderColor, position)

        val cornerRadius = h / 2f
        trackRect.set(0f, 0f, w, h)

        trackPaint.color = currentTrackColor
        trackPaint.style = Paint.Style.FILL
        canvas.drawRoundRect(trackRect, cornerRadius, cornerRadius, trackPaint)

        val highlightGradient = LinearGradient(
            0f, 0f, 0f, h * 0.5f,
            Color.parseColor("#35FFFFFF"),
            Color.parseColor("#00FFFFFF"),
            Shader.TileMode.CLAMP
        )
        trackHighlightPaint.shader = highlightGradient
        canvas.drawRoundRect(trackRect, cornerRadius, cornerRadius, trackHighlightPaint)

        trackBorderPaint.color = currentBorderColor
        val strokeInset = trackBorderPaint.strokeWidth / 2f
        val borderRect = RectF(
            trackRect.left + strokeInset,
            trackRect.top + strokeInset,
            trackRect.right - strokeInset,
            trackRect.bottom - strokeInset
        )
        canvas.drawRoundRect(borderRect, cornerRadius - strokeInset, cornerRadius - strokeInset, trackBorderPaint)

        val thumbDiameter = h - (paddingPx * 2f)
        val thumbRadius = thumbDiameter / 2f

        val startX = paddingPx + thumbRadius
        val endX = w - paddingPx - thumbRadius
        val thumbCenterX = startX + (endX - startX) * position
        val thumbCenterY = h / 2f

        val currentThumbW = thumbDiameter + thumbStretch
        val currentThumbH = thumbDiameter - (thumbStretch * 0.25f)

        thumbRect.set(
            thumbCenterX - (currentThumbW / 2f),
            thumbCenterY - (currentThumbH / 2f),
            thumbCenterX + (currentThumbW / 2f),
            thumbCenterY + (currentThumbH / 2f)
        )

        val thumbCorner = currentThumbH / 2f

        thumbShadowPaint.color = Color.TRANSPARENT
        thumbShadowPaint.setShadowLayer(dpToPx(4f), 0f, dpToPx(2f), Color.parseColor("#38000000"))
        canvas.drawRoundRect(thumbRect, thumbCorner, thumbCorner, thumbShadowPaint)

        val thumbGradient = LinearGradient(
            thumbRect.left, thumbRect.top, thumbRect.left, thumbRect.bottom,
            Color.WHITE,
            if (isNight) Color.parseColor("#EAF0FF") else Color.parseColor("#F1F5F9"),
            Shader.TileMode.CLAMP
        )
        thumbPaint.shader = thumbGradient
        thumbPaint.style = Paint.Style.FILL
        canvas.drawRoundRect(thumbRect, thumbCorner, thumbCorner, thumbPaint)

        thumbBorderPaint.color = Color.parseColor("#A0FFFFFF")
        val thumbStrokeInset = thumbBorderPaint.strokeWidth / 2f
        val thumbBorderRect = RectF(
            thumbRect.left + thumbStrokeInset,
            thumbRect.top + thumbStrokeInset,
            thumbRect.right - thumbStrokeInset,
            thumbRect.bottom - thumbStrokeInset
        )
        canvas.drawRoundRect(thumbBorderRect, thumbCorner - thumbStrokeInset, thumbCorner - thumbStrokeInset, thumbBorderPaint)

        canvas.restore()
    }

    private fun isNightMode(): Boolean {
        val uiMode = context.resources.configuration.uiMode
        return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}