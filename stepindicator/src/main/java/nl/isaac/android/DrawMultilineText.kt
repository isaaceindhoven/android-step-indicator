package nl.isaac.android

import android.graphics.Canvas
import android.os.Build
import android.text.*
import androidx.annotation.RequiresApi
import androidx.core.graphics.withTranslation
import androidx.core.util.lruCache

@RequiresApi(Build.VERSION_CODES.M)
fun Canvas.drawMultilineText(
    text: CharSequence,
    textPaint: TextPaint,
    width: Int,
    x: Float,
    y: Float,
    start: Int = 0,
    end: Int = text.length,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
    textDir: TextDirectionHeuristic = TextDirectionHeuristics.LTR,
    spacingMult: Float = 1f,
    spacingAdd: Float = 0f
) {

    val cacheKey = "$text-$start-$end-$textPaint-$width-$alignment-$textDir-$spacingMult-$spacingAdd"

    val staticLayout = StaticLayoutCache[cacheKey] ?: StaticLayout.Builder.obtain(text, start, end, textPaint, width)
        .setAlignment(alignment)
        .setTextDirection(textDir)
        .setLineSpacing(spacingAdd, spacingMult)
        .build().apply { StaticLayoutCache[cacheKey] = this }

    staticLayout.draw(this, x, y)
}

private fun StaticLayout.draw(canvas: Canvas, x: Float, y: Float) {
    canvas.withTranslation(x, y) {
        draw(this)
    }
}

private object StaticLayoutCache {

    private const val MAX_SIZE = 50
    private val cache = lruCache<String, StaticLayout>(MAX_SIZE)

    operator fun set(key: String, staticLayout: StaticLayout) {
        cache.put(key, staticLayout)
    }

    operator fun get(key: String): StaticLayout? {
        return cache[key]
    }
}
