package com.example.amazon_map.Dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.amazon_map.Fragments.HomeFragment
import com.example.amazon_map.R

class DateDialogFragment(private val fragment: Fragment) {
    fun showDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(fragment.requireContext())
        builder.setTitle("Please choose your date of birth.")
            .setPositiveButton("Choose") { _, _ ->
                val dialog = DatePicker()
                dialog.show(fragment.childFragmentManager, "DatePicker")
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}

class DatePicker : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireContext(), this, year, month, day)
    }
    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val fragment = parentFragment as HomeFragment?
        fragment!!.onDateSet(year, month, day)
    }
}