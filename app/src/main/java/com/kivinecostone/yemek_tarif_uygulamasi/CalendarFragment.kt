package com.kivinecostone.yemek_tarif_uygulamasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.*
import android.content.Context
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import java.util.zip.Inflater


class CalendarFragment : Fragment() {


    private var calorieCount = 0
    private val PREF_NAME = "KaloriPreferences"
    private val KEY_CALORIE = "calorie_count"


    override fun onCreateView(

    inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Toast.makeText(requireContext(), "CalendarFragment yüklendi", Toast.LENGTH_SHORT).show()

        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val calorieText = view.findViewById<TextView>(R.id.calorieText)
        val inputAmount = view.findViewById<EditText>(R.id.inputAmount)
        val addButton = view.findViewById<Button>(R.id.addButton)
        val removeButton = view.findViewById<Button>(R.id.removeButton)

        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


        calorieCount = prefs.getInt(KEY_CALORIE, 0)
        calorieText.text = calorieCount.toString()


        addButton.setOnClickListener {
            Log.d("BUTON", "Ekle butonuna tıklandı")

            val input = inputAmount.text.toString()
            if (input.isNotEmpty()) {
                val amount = input.toInt()
                calorieCount += amount
                calorieText.text = calorieCount.toString()
                prefs.edit().putInt(KEY_CALORIE, calorieCount).apply()
            } else {
                Toast.makeText(requireContext(), "Lütfen bir sayı gir", Toast.LENGTH_SHORT).show()
            }
        }


        saveButton.setOnClickListener {
            prefs.edit().putInt(KEY_CALORIE, calorieCount).apply()

            Toast.makeText(requireContext(), "Toplam kalori kaydedildi: $calorieCount", Toast.LENGTH_SHORT).show()
        }

        removeButton.setOnClickListener {
            val input = inputAmount.text.toString()
            if (input.isNotEmpty()) {
                val amount = input.toInt()
                calorieCount -= amount
                calorieText.text = calorieCount.toString()
                prefs.edit().putInt(KEY_CALORIE, calorieCount).apply()
            } else {
                Toast.makeText(requireContext(), "Lütfen bir sayı gir", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
class CalendarFragment1 : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }
}

