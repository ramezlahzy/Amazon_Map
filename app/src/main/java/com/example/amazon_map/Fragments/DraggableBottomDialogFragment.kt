package com.example.amazon_map.Fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.customview.widget.ViewDragHelper
import com.example.amazon_map.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DraggableBottomDialogFragment : BottomSheetDialogFragment() {
    private lateinit var dragHelper: ViewDragHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_draggable_bottom_dialog, container, false)
    }
}
