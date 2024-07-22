package com.snapcat.ui.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CustomTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        val metrics = context.resources.displayMetrics
        val lineHeightPixels = 26 * metrics.density
        val fontHeightPixels = textSize
        val extraSpacing = lineHeightPixels - fontHeightPixels
        setLineSpacing(extraSpacing, 1.0f)
    }
}
