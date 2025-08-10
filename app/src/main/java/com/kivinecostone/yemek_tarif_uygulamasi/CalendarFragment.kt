package com.kivinecostone.yemek_tarif_uygulamasi

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private var calorieCount = 0
    private val PREF_NAME = "KaloriPreferences"
    private val KEY_CALORIE = "calorie_count"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_calendar, container, false)

        val titleText = root.findViewById<TextView>(R.id.titleText)
        val calorieText = root.findViewById<TextView>(R.id.calorieText)
        val todayCaloriesText = root.findViewById<TextView>(R.id.todayCaloriesText)
        val inputAmount = root.findViewById<EditText>(R.id.inputAmount)
        val removeButton = root.findViewById<Button>(R.id.removeButton)
        val addButton = root.findViewById<Button>(R.id.addButton)
        val saveButton = root.findViewById<Button>(R.id.saveButton)
        val selectedDayCaloriesText = root.findViewById<TextView>(R.id.selectedDayCaloriesText)
        val calendarView = root.findViewById<CalendarView>(R.id.calendarView)

        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = sdf.format(Date())

        calorieCount = prefs.getInt(KEY_CALORIE, 0)
        calorieText.text = calorieCount.toString()

        val todaySaved = prefs.getInt(todayDate, 0)
        todayCaloriesText.text = "Bugünkü Kalorin: $todaySaved"
        selectedDayCaloriesText.text = "Seçilen Gün Kalorisi: $todaySaved"

        val setEditingEnabled: (Boolean) -> Unit = { enable ->
            addButton.isEnabled = enable
            removeButton.isEnabled = enable
            saveButton.isEnabled = enable
            inputAmount.isEnabled = enable
            inputAmount.isFocusable = enable
            inputAmount.isFocusableInTouchMode = enable
            val alpha = if (enable) 1f else 0.5f
            addButton.alpha = alpha
            removeButton.alpha = alpha
            saveButton.alpha = alpha
            inputAmount.alpha = alpha
        }

        setEditingEnabled(true)

        addButton.setOnClickListener {
            val input = inputAmount.text.toString()
            if (input.isNotEmpty()) {
                val amount = input.toInt()
                calorieCount += amount
                calorieText.text = calorieCount.toString()
            } else {
                Toast.makeText(requireContext(), "Lütfen bir sayı gir", Toast.LENGTH_SHORT).show()
            }
        }

        removeButton.setOnClickListener {
            val input = inputAmount.text.toString()
            if (input.isNotEmpty()) {
                val amount = input.toInt()
                calorieCount -= amount
                calorieText.text = calorieCount.toString()
            } else {
                Toast.makeText(requireContext(), "Lütfen bir sayı gir", Toast.LENGTH_SHORT).show()
            }
        }

        saveButton.setOnClickListener {
            prefs.edit()
                .putInt(KEY_CALORIE, calorieCount)
                .putInt(todayDate, calorieCount)
                .apply()
            todayCaloriesText.text = "Bugünkü Kalorin: $calorieCount"
            Toast.makeText(requireContext(), "Bugünkü kalori kaydedildi", Toast.LENGTH_SHORT).show()
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val mm = String.format("%02d", month + 1)
            val dd = String.format("%02d", dayOfMonth)
            val dateKey = "$year-$mm-$dd"
            val dayCalories = prefs.getInt(dateKey, 0)
            selectedDayCaloriesText.text = "Seçilen Gün Kalorisi: $dayCalories"
            val isToday = dateKey == todayDate
            setEditingEnabled(isToday)
        }

        return root
    }
}


class CalendarFragment1 : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }
}

