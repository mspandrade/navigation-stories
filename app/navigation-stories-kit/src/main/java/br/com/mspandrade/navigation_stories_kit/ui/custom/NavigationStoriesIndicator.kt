package br.com.mspandrade.navigation_stories_kit.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import br.com.mspandrade.navigation_stories_kit.R
import br.com.mspandrade.navigation_stories_kit.data.StoryIndicatorTheme
import java.lang.ref.WeakReference

internal class NavigationStoriesIndicator(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {

    interface OnNavigationStoriesEventListener {
        fun onChange(segment: Int)
        fun onFinish()
    }

    private var mProgress = 0.0f

    private var mBarColor = context.getColor(android.R.color.holo_green_light)
    private var mBackgroundBarColor = context.getColor(android.R.color.holo_green_dark)
    private var mCornerRadius = 10f
    private var mGapSize = 10f

    private var mSegments = 8
    private var mSegmentsDuration = 1000L
    private var mCurrentSegment = 0

    private var wkListener = WeakReference<OnNavigationStoriesEventListener>(null)

    private var progress set(value) {
        mProgress = when {
            value > 1f -> 1f
            value < 0f -> 0f
            else -> value
        }
        postInvalidate()
    } get() = mProgress

    var segmentCount set(value) {
        mSegments = value
        postInvalidate()
    } get() = mSegments

    private var animation: ValueAnimator? = null

    val currentSegment get() = mCurrentSegment

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet,
            R.styleable.NavigationStoriesIndicator
        )
        try {
            typedArray.apply {
                mBarColor = getColor(R.styleable.NavigationStoriesIndicator_barColor, mBarColor)
                mBackgroundBarColor =
                    getColor(R.styleable.NavigationStoriesIndicator_backgroundBarColor, mBackgroundBarColor)
                mSegments = getInt(R.styleable.NavigationStoriesIndicator_segments, mSegments)
                mGapSize = getDimension(R.styleable.NavigationStoriesIndicator_gapSize, mGapSize)
                mCornerRadius = getDimension(R.styleable.NavigationStoriesIndicator_cornerRadius, mCornerRadius)
                mSegmentsDuration = getInt(R.styleable.NavigationStoriesIndicator_segmentDuration, mSegmentsDuration.toInt()).toLong()
            }

        } finally {
            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawSegments(1f, mBackgroundBarColor, height.toFloat(), mSegments, mGapSize, mCornerRadius)
        canvas.drawSegments(mProgress, mBarColor, height.toFloat(), mSegments, mGapSize, mCornerRadius)
    }

    fun setTheme(theme: StoryIndicatorTheme) {
        mBarColor = context.getColor(theme.indicatorEnabledColor)
        mBackgroundBarColor = context.getColor(theme.indicatorDisabledColor)
        mCornerRadius = theme.indicatorCorner
        mGapSize = theme.gapSize
        layoutParams.apply {
            this.height = theme.height
        }
    }

    fun setOnSegmentChanged(listener: OnNavigationStoriesEventListener) {
        wkListener = WeakReference(listener)
    }

    private fun createValueAnimator(initialValue: Float) = ValueAnimator.ofFloat(initialValue, 1f).apply {
        interpolator = LinearInterpolator()

        val totalTime = mSegmentsDuration * mSegments
        val realTime = totalTime - (initialValue * totalTime)
        duration = realTime.toLong()

        addUpdateListener {
            progress = it.animatedValue as Float
            val currentSegment = (segmentCount * progress).toInt()

            if (progress == 1f) {
                wkListener.get()?.onFinish()
                return@addUpdateListener
            }

            if (currentSegment != mCurrentSegment) {
                mCurrentSegment = currentSegment
                wkListener.get()?.onChange(currentSegment)
            }
        }
    }

    fun start(segment: Int = 0) {

        val safeSegment = when {
            segment < 0 -> 0
            segment > mSegments -> mSegments
            else -> segment
        }

        this.animation?.cancel()
        this.animation?.removeAllListeners()
        progress = if (safeSegment == 0) 0f else (safeSegment.toFloat() / mSegments)
        animation = createValueAnimator(progress)
        animation?.start()
    }

    fun next() {
        start(currentSegment + 1)
    }

    fun previous() {
        start(currentSegment - 1)
    }

    fun pause() {
        animation?.pause()
    }

    fun resume() {
        animation?.resume()
    }
}

private fun Canvas.drawSegments(
    progress: Float,
    color: Int,
    strokeWidth: Float,
    segments: Int,
    segmentGap: Float,
    roundedValue: Float
) {
    val paint = Paint().apply {
        this.color = color
    }
    val start = 0f
    val gaps = (segments - 1) * segmentGap
    val segmentWidth = (width - gaps) / segments
    val barsWidth = segmentWidth * segments
    val end = barsWidth * progress + (progress * segments).toInt() * segmentGap

    repeat(segments) { index ->
        val offset = index * (segmentWidth + segmentGap)
        if (offset < end) {
            val barEnd = (offset + segmentWidth).coerceAtMost(end)
            val rect = RectF(
                start + offset,
                0f,
                barEnd,
                strokeWidth,
            )
            drawRoundRect(rect, roundedValue, roundedValue, paint)
        }
    }
}