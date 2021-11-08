package nl.isaac.android

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.viewpager2.widget.ViewPager2
import nl.isaac.android.stepindicator.R
import kotlin.math.abs

class StepIndicator(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    var stepRadius: Float = DEFAULT_STEP_RADIUS
        set(value) {
            field = value
            updateActiveStepPaint()
            updatePreviousStepPaint()
            updateNextStepPaint()
            invalidate()
        }

    var activeStepRadius: Float = DEFAULT_ACTIVE_STEP_RADIUS
        set(value) {
            field = value
            updateActiveStepPaint()
            updatePreviousStepPaint()
            updateNextStepPaint()
            invalidate()
        }

    var stepWidth: Float = DEFAULT_STEP_WIDTH
        set(value) {
            field = value
            updateActiveStepPaint()
            updatePreviousStepPaint()
            updateNextStepPaint()
            invalidate()
        }

    var stepDistance: Float = DEFAULT_STEP_DISTANCE
        set(value) {
            field = value
            updateActiveStepPaint()
            updatePreviousStepPaint()
            updateNextStepPaint()
            invalidate()
        }

    var numberOfSteps: Int = DEFAULT_NUMBER_OF_STEPS
        set(value) {
            field = value
            invalidate()
        }

    var activeStepPosition: Int = DEFAULT_ACTIVE_STEP_POSITION
        set(value) {
            if (value in 0 until numberOfSteps) {
                field = value
                invalidate()
            }
        }

    var activeStepColor: Int = primaryColor
        set(value) {
            field = value
            updateActiveStepPaint()
            invalidate()
        }

    var previousStepColor: Int = primaryColor
        set(value) {
            field = value
            updatePreviousStepPaint()
            invalidate()
        }

    var nextStepColor: Int = DEFAULT_NEXT_STEP_COLOR
        set(value) {
            field = value
            updateNextStepPaint()
            invalidate()
        }

    var activeStepIndicatorTypeColor: Int = primaryColor
        set(value) {
            field = value
            updateActiveStepPaint()
            invalidate()
        }

    var previousStepIndicatorTypeColor: Int = primaryColor
        set(value) {
            field = value
            updatePreviousStepPaint()
            invalidate()
        }

    var nextStepIndicatorTypeColor: Int = DEFAULT_NEXT_STEP_COLOR
        set(value) {
            field = value
            updateNextStepPaint()
            invalidate()
        }

    var activeStepLabelColor: Int = primaryColor
        set(value) {
            field = value
            updateActiveStepPaint()
            invalidate()
        }

    var previousStepLabelColor: Int = primaryColor
        set(value) {
            field = value
            updatePreviousStepPaint()
            invalidate()
        }

    var nextStepLabelColor: Int = DEFAULT_NEXT_STEP_COLOR
        set(value) {
            field = value
            updateNextStepPaint()
            invalidate()
        }

    var fillActiveStep: Boolean = DEFAULT_FILL_ACTIVE_STEP
        set(value) {
            field = value
            updateActiveStepPaint()
            invalidate()
        }

    var fillPreviousStep: Boolean = DEFAULT_FILL_PREVIOUS_STEP
        set(value) {
            field = value
            updatePreviousStepPaint()
            invalidate()
        }

    var fillNextStep: Boolean = DEFAULT_FILL_NEXT_STEP
        set(value) {
            field = value
            updateNextStepPaint()
            invalidate()
        }

    var showLabels: Boolean = DEFAULT_SHOW_LABELS
        set(value) {
            field = value
            invalidate()
        }

    var showLinesBetweenSteps: Boolean = DEFAULT_SHOW_LINES_BETWEEN_STEPS
        set(value) {
            field = value
            invalidate()
        }

    var labels = listOf<String>()

    var stepType: StepType = DEFAULT_STEP_TYPE
        set(value) {
            field = value
            invalidate()
        }

    var onClickListener: OnClickListener? = null

    private var startX: Float = 0f
    private var centerY: Float = 0f
    private var endX: Float = 0F
    private var endY: Float = 0F

    private var numberOfLinesBetweenSteps: Int = 0
    private var stepIndicatorSize: Float = 0f
    private var differenceBetweenIndicators: Float = 0f

    private val textBounds: Rect = Rect()

    private val primaryColor: Int
        get() {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            return typedValue.data
        }

    private val viewPager2OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) { activeStepPosition = position }
        override fun onPageScrollStateChanged(state: Int) {}
    }

    private val activeStepPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val previousStepPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val nextStepPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val activeStepIndicatorTypePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val previousStepIndicatorTypePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val nextStepIndicatorTypePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val activeStepLabelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val previousStepLabelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val nextStepLabelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    init {
        // Set attributes parameters from file
        context.theme.obtainStyledAttributes(attributeSet, R.styleable.StepIndicator, 0, 0).apply {
            try {
                stepRadius = getFloat(R.styleable.StepIndicator_stepRadius, DEFAULT_STEP_RADIUS)
                activeStepRadius = getFloat(R.styleable.StepIndicator_activeStepRadius, DEFAULT_ACTIVE_STEP_RADIUS)
                stepWidth = getFloat(R.styleable.StepIndicator_stepWidth, DEFAULT_STEP_WIDTH)
                stepDistance = getFloat(R.styleable.StepIndicator_stepDistance, DEFAULT_STEP_DISTANCE)
                numberOfSteps = getInt(R.styleable.StepIndicator_numberOfSteps, DEFAULT_NUMBER_OF_STEPS)
                activeStepPosition = getInt(R.styleable.StepIndicator_activeStepPosition, DEFAULT_ACTIVE_STEP_POSITION)
                activeStepColor = getInt(R.styleable.StepIndicator_activeStepColor, primaryColor)
                previousStepColor = getInt(R.styleable.StepIndicator_previousStepColor, primaryColor)
                nextStepColor = getInt(R.styleable.StepIndicator_nextStepColor, DEFAULT_NEXT_STEP_COLOR)
                activeStepIndicatorTypeColor = getInt(R.styleable.StepIndicator_activeStepIndicatorTypeColor, primaryColor)
                previousStepIndicatorTypeColor = getInt(R.styleable.StepIndicator_previousStepIndicatorTypeColor, primaryColor)
                nextStepIndicatorTypeColor = getInt(R.styleable.StepIndicator_nextStepIndicatorTypeColor, DEFAULT_NEXT_STEP_COLOR)
                activeStepLabelColor = getInt(R.styleable.StepIndicator_activeStepLabelColor, primaryColor)
                previousStepLabelColor = getInt(R.styleable.StepIndicator_previousStepLabelColor, primaryColor)
                nextStepLabelColor = getInt(R.styleable.StepIndicator_nextStepLabelColor, DEFAULT_NEXT_STEP_COLOR)
                fillActiveStep = getBoolean(R.styleable.StepIndicator_fillActiveStep, DEFAULT_FILL_ACTIVE_STEP)
                fillPreviousStep = getBoolean(R.styleable.StepIndicator_fillPreviousStep, DEFAULT_FILL_PREVIOUS_STEP)
                fillNextStep = getBoolean(R.styleable.StepIndicator_fillNextStep, DEFAULT_FILL_NEXT_STEP)
                showLabels = getBoolean(R.styleable.StepIndicator_showLabels, DEFAULT_SHOW_LABELS)
                showLinesBetweenSteps = getBoolean(R.styleable.StepIndicator_showLinesBetweenSteps, DEFAULT_SHOW_LINES_BETWEEN_STEPS)
            } catch (exception: Exception) {
                Log.e(javaClass.name, exception.toString())
            } finally {
                recycle()
            }
        }

        updateActiveStepPaint()
        updatePreviousStepPaint()
        updateNextStepPaint()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var pointX = startX
        for (position in 0 until numberOfSteps) {
            drawCircle(canvas, position, pointX)

            if (position < numberOfLinesBetweenSteps && showLinesBetweenSteps) {
                drawLine(canvas, position, pointX)
            }

            drawLabel(canvas, position, pointX)
            drawValueInsideStepIndicator(canvas, position, pointX)

            pointX += stepDistance + stepIndicatorSize
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        updateCanvas()
        val requestedWidth = MeasureSpec.getSize(widthMeasureSpec)
        val requestedWidthMode = MeasureSpec.getMode(widthMeasureSpec)

        val requestedHeight = MeasureSpec.getSize(heightMeasureSpec)
        val requestedHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        val desiredWidth: Int = endX.toInt()
        val desiredHeight: Int = endY.toInt()

        if (desiredWidth > requestedWidth) {
            val maxStepDistance = (desiredWidth - requestedWidth) / numberOfLinesBetweenSteps
            if (maxStepDistance > stepDistance) {
                stepRadius -= maxStepDistance - stepDistance
            } else {
                stepDistance -= maxStepDistance
            }
        }

        val width = when (requestedWidthMode) {
            MeasureSpec.EXACTLY -> requestedWidth
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> requestedWidth.coerceAtMost(desiredWidth)
        }

        val height = when (requestedHeightMode) {
            MeasureSpec.EXACTLY -> requestedHeight
            MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> requestedHeight.coerceAtMost(desiredHeight)
        }

        setMeasuredDimension(width, height)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var pointX = startX
        for (i in 0 until numberOfSteps) {
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                if (abs(event.x - pointX) < stepRadius + 5 && abs(event.y - centerY) < stepRadius + 5) {
                    onClickListener?.onClick(i)
                }
            }
            pointX += stepDistance + stepIndicatorSize
        }
        return super.onTouchEvent(event)
    }

    fun setupWithViewPager(viewPager: ViewPager2) {
        if (viewPager.adapter == null) {
            throw IllegalArgumentException("ViewPager does not have a PagerAdapter set")
        }

        numberOfSteps = viewPager.adapter!!.itemCount

        viewPager.registerOnPageChangeCallback(viewPager2OnPageChangeCallback)
    }

    fun unRegisterViewPager(viewPager: ViewPager2){
        viewPager.unregisterOnPageChangeCallback(viewPager2OnPageChangeCallback)
    }

    fun setNextStepActive() {
        activeStepPosition += 1
    }

    fun setPreviousStepActive() {
        activeStepPosition -= 1
    }

    private fun updateCanvas() {
        differenceBetweenIndicators = activeStepRadius - stepRadius
        stepIndicatorSize = (stepRadius + stepWidth) * 2
        numberOfLinesBetweenSteps = numberOfSteps - 1

        val radius = if (activeStepRadius > stepRadius) activeStepRadius else stepRadius

        centerY = radius + stepWidth
        startX = radius + stepWidth
        endX = ((stepRadius + stepWidth) * 2) * numberOfSteps + stepDistance * numberOfLinesBetweenSteps + differenceBetweenIndicators * 2
        endY = (radius + stepWidth) * 2

        if (showLabels) {
            startX += stepIndicatorSize / 2
            endX += stepIndicatorSize
            endY += stepIndicatorSize + stepRadius
        }
    }

    private fun drawCircle(canvas: Canvas, position: Int, pointX: Float) {
        val paint = when {
            activeStepPosition > position -> previousStepPaint
            activeStepPosition == position -> activeStepPaint
            activeStepPosition < position -> nextStepPaint
            else -> nextStepPaint
        }

        if (activeStepPosition == position) {
            canvas.drawCircle(pointX, centerY, activeStepRadius, paint)
        } else {
            canvas.drawCircle(pointX, centerY, stepRadius, paint)
        }
    }

    private fun drawLine(canvas: Canvas, position: Int, pointX: Float) {
        val paint = when {
            activeStepPosition > position -> previousStepPaint
            activeStepPosition == position -> nextStepPaint
            activeStepPosition < position -> nextStepPaint
            else -> nextStepPaint
        }

        var startLine = pointX + stepRadius + stepWidth / 2
        var endLine = startLine + stepDistance + stepWidth
        if (activeStepPosition == position + 1) {
            endLine -= differenceBetweenIndicators
        } else if (activeStepPosition == position) {
            startLine += differenceBetweenIndicators
            endLine = startLine + stepDistance + stepWidth - differenceBetweenIndicators
        }

        canvas.drawLine(startLine, centerY, endLine, centerY, paint)
    }

    private fun drawLabel(canvas: Canvas, position: Int, pointX: Float) {
        val paint = when {
            activeStepPosition > position -> previousStepLabelPaint
            activeStepPosition == position -> activeStepLabelPaint
            activeStepPosition < position -> nextStepLabelPaint
            else -> nextStepLabelPaint
        }

        if (showLabels && labels.isNotEmpty()) {
            val text = labels.getOrNull(position) ?: ""

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                canvas.drawMultilineText(
                    text,
                    paint,
                    (stepIndicatorSize + (stepDistance / 2)).toInt(),
                    pointX,
                    stepIndicatorSize + differenceBetweenIndicators + LABEL_MARGIN_PREFIX
                )
            } else {
                canvas.drawText(text, pointX, stepIndicatorSize, paint)
            }
        }
    }

    private fun drawValueInsideStepIndicator(canvas: Canvas, position: Int, pointX: Float) {
        when (stepType) {
            is StepType.Icon -> {
                val icon = (stepType as StepType.Icon).icons.getOrNull(position)
                icon?.let { drawIconInsideStepIndicator(canvas, position, pointX, it) }
            }
            is StepType.Mixed -> drawMixedValueInsideStepIndicator(canvas, position, pointX)
            is StepType.Text -> {
                val text = (stepType as StepType.Text).text.getOrNull(position)
                text?.let { drawTextInsideStepIndicator(canvas, position, pointX, it) }
            }
            is StepType.Numbers -> drawTextInsideStepIndicator(canvas, position, pointX, (position + 1).toString())
            StepType.None -> {
                // In this case a no value must be drawn
            }
        }
    }

    private fun drawIconInsideStepIndicator(canvas: Canvas, position: Int, pointX: Float, icon: Int) {
        val color = when {
            activeStepPosition > position -> previousStepIndicatorTypeColor
            activeStepPosition == position -> activeStepIndicatorTypeColor
            activeStepPosition < position -> nextStepIndicatorTypeColor
            else -> nextStepIndicatorTypeColor
        }

        val bitmapIcon = AppCompatResources.getDrawable(context, icon)?.apply {
            colorFilter = LightingColorFilter(color, 0)
        }?.toBitmap()
        bitmapIcon?.let {
            val rectF = RectF(
                pointX - getStepRadiusWithPadding(),
                centerY - getStepRadiusWithPadding(),
                pointX + getStepRadiusWithPadding(),
                centerY + getStepRadiusWithPadding()
            )
            canvas.drawBitmap(it, null, rectF, Paint())
        }
    }

    private fun drawTextInsideStepIndicator(canvas: Canvas, position: Int, pointX: Float, text: String) {
        val paint = when {
            activeStepPosition > position -> previousStepIndicatorTypePaint
            activeStepPosition == position -> activeStepIndicatorTypePaint
            activeStepPosition < position -> nextStepIndicatorTypePaint
            else -> nextStepIndicatorTypePaint
        }

        paint.getTextBounds(text, 0, text.length, textBounds)
        canvas.drawText(text, pointX, centerY - textBounds.exactCenterY(), paint)
    }

    private fun drawMixedValueInsideStepIndicator(canvas: Canvas, position: Int, pointX: Float) {
        val mixedValue = (stepType as StepType.Mixed).values.getOrNull(position)
        if (mixedValue?.first == StepType.StepIndicatorTypeMixedOption.TEXT) {
            drawTextInsideStepIndicator(canvas, position, pointX, mixedValue.second as String)
        } else if (mixedValue?.first == StepType.StepIndicatorTypeMixedOption.ICON) {
            drawIconInsideStepIndicator(canvas, position, pointX, mixedValue.second as Int)
        }
    }

    private fun updateActiveStepPaint() {
        activeStepPaint.apply {
            style = if (fillActiveStep) Paint.Style.FILL_AND_STROKE else Paint.Style.STROKE
            color = activeStepColor
            strokeWidth = stepWidth
        }

        activeStepIndicatorTypePaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            color = activeStepIndicatorTypeColor
            strokeWidth = 2f
            textSize = getStepRadiusWithPadding()
            textAlign = Paint.Align.CENTER
        }

        activeStepLabelPaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            color = activeStepLabelColor
            strokeWidth = 2f
            textSize = getStepRadiusWithPadding()
            textAlign = Paint.Align.CENTER
        }
    }

    private fun updatePreviousStepPaint() {
        previousStepPaint.apply {
            style = if (fillPreviousStep) Paint.Style.FILL_AND_STROKE else Paint.Style.STROKE
            color = previousStepColor
            strokeWidth = stepWidth
        }

        previousStepIndicatorTypePaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            color = previousStepIndicatorTypeColor
            strokeWidth = 2f
            textSize = getStepRadiusWithPadding()
            textAlign = Paint.Align.CENTER
        }

        previousStepLabelPaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            color = previousStepLabelColor
            strokeWidth = 2f
            textSize = getStepRadiusWithPadding()
            textAlign = Paint.Align.CENTER
        }
    }

    private fun updateNextStepPaint() {
        nextStepPaint.apply {
            style = if (fillNextStep) Paint.Style.FILL_AND_STROKE else Paint.Style.STROKE
            color = nextStepColor
            strokeWidth = stepWidth
        }

        nextStepIndicatorTypePaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            color = nextStepIndicatorTypeColor
            strokeWidth = 2f
            textSize = getStepRadiusWithPadding()
            textAlign = Paint.Align.CENTER
        }

        nextStepLabelPaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            color = nextStepLabelColor
            strokeWidth = 2f
            textSize = getStepRadiusWithPadding()
            textAlign = Paint.Align.CENTER
        }
    }

    private fun getStepRadiusWithPadding(): Float {
        return if (activeStepRadius >= stepRadius) stepRadius + -10f else activeStepRadius
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }

    sealed class StepType {
        object None : StepType()
        object Numbers : StepType()
        data class Text(val text: List<String>) : StepType()
        data class Icon(val icons: List<Int>) : StepType()
        data class Mixed(val values: List<Pair<StepIndicatorTypeMixedOption, Any>>) : StepType()

        enum class StepIndicatorTypeMixedOption {
            TEXT, ICON, NONE
        }
    }

    private companion object {

        const val DEFAULT_STEP_RADIUS = 42f
        const val DEFAULT_ACTIVE_STEP_RADIUS = (DEFAULT_STEP_RADIUS * 1.25).toFloat()
        const val DEFAULT_STEP_WIDTH = 8f
        const val DEFAULT_STEP_DISTANCE = 175f
        const val DEFAULT_NUMBER_OF_STEPS = 4
        const val DEFAULT_ACTIVE_STEP_POSITION = 0
        const val DEFAULT_NEXT_STEP_COLOR = Color.LTGRAY
        const val DEFAULT_FILL_ACTIVE_STEP = false
        const val DEFAULT_FILL_PREVIOUS_STEP = false
        const val DEFAULT_FILL_NEXT_STEP = true
        const val DEFAULT_SHOW_LABELS = false
        const val DEFAULT_SHOW_LINES_BETWEEN_STEPS = true
        const val LABEL_MARGIN_PREFIX = 8f

        val DEFAULT_STEP_TYPE = StepType.Numbers
    }
}
