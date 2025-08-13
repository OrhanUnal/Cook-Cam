package com.kivinecostone.yemek_tarif_uygulamasi

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*
import com.prolificinteractive.materialcalendarview.*

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
        val calendarView = root.findViewById<MaterialCalendarView>(R.id.calendarView)

        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = sdf.format(Date())
        var selectedDate = todayDate

        fun setEditingEnabled(enable: Boolean) {
            addButton.isEnabled = enable
            removeButton.isEnabled = enable
            saveButton.isEnabled = enable
            inputAmount.isEnabled = enable
            inputAmount.isFocusable = enable
            inputAmount.isFocusableInTouchMode = enable
            val a = if (enable) 1f else 0.5f
            addButton.alpha = a; removeButton.alpha = a; saveButton.alpha = a; inputAmount.alpha = a
        }

        fun loadCountFor(dateKey: String): Int {
            return if (dateKey == todayDate) {
                prefs.getInt(KEY_CALORIE, prefs.getInt(todayDate, 0))
            } else {
                prefs.getInt(dateKey, 0)
            }
        }

        fun refreshUIFor(dateKey: String) {
            calorieCount = loadCountFor(dateKey)
            calorieText.text = calorieCount.toString()
            val dayCalories = prefs.getInt(dateKey, 0)
            selectedDayCaloriesText.text = "Seçilen Gün Kalorisi: $dayCalories"
            val todaySaved = prefs.getInt(todayDate, 0)
            todayCaloriesText.text = "Bugünkü Kalorin: $todaySaved"
        }

        fun allEntryDays(): Set<CalendarDay> {
            val out = HashSet<CalendarDay>()
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val re = Regex("\\d{4}-\\d{2}-\\d{2}")
            for ((k, v) in prefs.all) {
                if (v is Int && re.matches(k)) {
                    val parsed = fmt.parse(k)
                    if (parsed != null) {
                        out.add(CalendarDay.from(parsed))
                    }
                }
            }
            return out
        }


        val todaySavedInit = prefs.getInt(todayDate, 0)
        todayCaloriesText.text = "Bugünkü Kalorin: $todaySavedInit"
        selectedDayCaloriesText.text = "Seçilen Gün Kalorisi: $todaySavedInit"
        calorieCount = loadCountFor(todayDate)
        calorieText.text = calorieCount.toString()
        setEditingEnabled(true)

        calendarView.clearSelection()
        calendarView.setSelectedDate(CalendarDay.today())
        calendarView.addDecorator(EntryDaysDecorator(allEntryDays()))

        addButton.setOnClickListener {
            val input = inputAmount.text.toString()
            if (input.isNotEmpty()) {
                val amount = input.toInt()
                calorieCount += amount
                if (calorieCount < 0) calorieCount = 0
                calorieText.text = calorieCount.toString()
            } else {
                Toast.makeText(requireContext(), "Lütfen bir sayı gir", Toast.LENGTH_SHORT).show()
            }
        }

        removeButton.setOnClickListener {
            val input = inputAmount.text.toString()
            if (input.isNotEmpty()) {
                val amount = input.toInt()
                val newVal = calorieCount - amount
                calorieCount = if (newVal < 0) 0 else newVal
                calorieText.text = calorieCount.toString()
            } else {
                Toast.makeText(requireContext(), "Lütfen bir sayı gir", Toast.LENGTH_SHORT).show()
            }
        }

        saveButton.setOnClickListener {
            prefs.edit().apply {
                putInt(selectedDate, calorieCount)
                if (selectedDate == todayDate) {
                    putInt(KEY_CALORIE, calorieCount)
                    putInt(todayDate, calorieCount)
                }
            }.apply()
            if (selectedDate == todayDate) {
                todayCaloriesText.text = "Bugünkü Kalorin: $calorieCount"
            }
            selectedDayCaloriesText.text = "Seçilen Gün Kalorisi: $calorieCount"
            calendarView.removeDecorators()
            calendarView.addDecorator(EntryDaysDecorator(allEntryDays()))
            Toast.makeText(requireContext(), "Kaydedildi", Toast.LENGTH_SHORT).show()
        }

        calendarView.setOnDateChangedListener(OnDateSelectedListener { _, date, _ ->
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateKey = fmt.format(date.date)

            selectedDate = dateKey
            val canEdit = dateKey <= todayDate
            setEditingEnabled(canEdit)
            refreshUIFor(dateKey)
        })

        return root
    }
}
