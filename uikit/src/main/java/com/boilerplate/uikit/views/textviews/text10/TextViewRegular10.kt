package com.boilerplate.uikit.views.textviews.text10

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.boilerplate.uikit.R

class TextViewRegular10 : AppCompatTextView {

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context) : super(context)

    init {
        setTextAppearance(R.style.textview_regular_10)
    }
}