package com.kivinecostone.yemek_tarif_uygulamasi

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.spans.DotSpan
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
        val calendarView = root.findViewById<MaterialCalendarView>(R.id.calendarView)

        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val sdfIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = sdfIso.format(Date())
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

        fun buildRadiusListFromPrefs(): List<Pair<CalendarDay, Float>> {
            val items = mutableListOf<Pair<CalendarDay, Int>>()
            val re = Regex("\\d{4}-\\d{2}-\\d{2}")
            for ((k, v) in prefs.all) {
                if (v is Int && re.matches(k)) {
                    val parsed = sdfIso.parse(k)
                    if (parsed != null) items.add(CalendarDay.from(parsed) to v)
                }
            }
            if (items.isEmpty()) return emptyList()
            val minVal = items.minOf { it.second }
            val maxVal = items.maxOf { it.second }
            val minR = 3f
            val maxR = 12f
            return items.map { (day, value) ->
                val radius = if (maxVal == minVal) {
                    (minR + maxR) / 2f
                } else {
                    val t = (value - minVal).toFloat() / (maxVal - minVal).toFloat()
                    minR + t * (maxR - minR)
                }
                day to radius
            }
        }

        fun applyPerDayRadiusDecorators() {
            calendarView.removeDecorators()
            val radiusList = buildRadiusListFromPrefs()
            for ((day, radius) in radiusList) {
                calendarView.addDecorator(PerDayRadiusDecorator(day, radius, Color.parseColor("#FF5722")))
            }
        }

        val todaySavedInit = prefs.getInt(todayDate, 0)
        todayCaloriesText.text = "Bugünkü Kalorin: $todaySavedInit"
        selectedDayCaloriesText.text = "Seçilen Gün Kalorisi: $todaySavedInit"
        calorieCount = loadCountFor(todayDate)
        calorieText.text = calorieCount.toString()
        setEditingEnabled(true)

        calendarView.clearSelection()
        calendarView.setSelectedDate(CalendarDay.today())
        applyPerDayRadiusDecorators()

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
            applyPerDayRadiusDecorators()
            Toast.makeText(requireContext(), "Kaydedildi", Toast.LENGTH_SHORT).show()
        }

        calendarView.setOnDateChangedListener(OnDateSelectedListener { _, date, _ ->
            val dateKey = sdfIso.format(date.date)
            selectedDate = dateKey
            val canEdit = dateKey <= todayDate
            setEditingEnabled(canEdit)
            refreshUIFor(dateKey)
        })

        return root
    }

    inner class PerDayRadiusDecorator(
        private val day: CalendarDay,
        private val radius: Float,
        private val color: Int
    ) : DayViewDecorator {
        override fun shouldDecorate(d: CalendarDay): Boolean = d == day
        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(radius, color))
        }
    }
}
