package com.example.amazon_map.Views

// DraggableView.kt
import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.customview.widget.ViewDragHelper

class DraggableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var dragHelper: ViewDragHelper
    private var draggedView: View? = null

    init {
        dragHelper = ViewDragHelper.create(this, 1.0f, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return child == draggedView
            }

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                val leftBound = paddingLeft
                val rightBound = width - child.width - paddingRight
                return left.coerceIn(leftBound, rightBound)
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                val topBound = paddingTop
                val bottomBound = height - child.height - paddingBottom
                return top.coerceIn(topBound, bottomBound)
            }
        })
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            draggedView = getChildAt(0)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        draggedView?.let {
            savedState.isDragged = dragHelper.isViewUnder(it, it.left, it.top)
        }
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            draggedView?.let {
                if (state.isDragged) {
                    post { dragHelper.smoothSlideViewTo(it, it.left, it.top) }
                }
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private class SavedState : BaseSavedState {
        var isDragged: Boolean = false

        constructor(superState: Parcelable?) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            isDragged = parcel.readByte() != 0.toByte()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeByte(if (isDragged) 1 else 0)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}
