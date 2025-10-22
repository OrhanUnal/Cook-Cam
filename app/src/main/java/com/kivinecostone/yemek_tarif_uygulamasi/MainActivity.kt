package com.kivinecostone.yemek_tarif_uygulamasi

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val cameraFragment = CameraFragment()
    private val calendarFragment = CalendarFragment()
    private val savedFragment = SavedFragment()
    private val aiChatFragment = AiChatFragment()

    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val sharedPreferences: SharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE)
        val privacy: Boolean = sharedPreferences.getBoolean("Privacy", false)
        if (!privacy) {
            checkPolicy(sharedPreferences, savedInstanceState)
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                sharedPreferences.edit { putBoolean("nightMode", true) }
            }
        } else {
            startMain(sharedPreferences, savedInstanceState)
        }
    }

    private fun startMain(sharedPreferences: SharedPreferences, savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)

        val switchMode: SwitchCompat = findViewById(R.id.themeSwitch)
        val storedNight = sharedPreferences.getBoolean("nightMode", false)
        if (storedNight) {
            switchMode.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            switchMode.isChecked = false
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        switchMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferences.edit { putBoolean("nightMode", true) }
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferences.edit { putBoolean("nightMode", false) }
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, aiChatFragment, "aiChat").hide(aiChatFragment)
                .add(R.id.fragment_container, savedFragment, "saved").hide(savedFragment)
                .add(R.id.fragment_container, calendarFragment, "calendar").hide(calendarFragment)
                .add(R.id.fragment_container, cameraFragment, "camera").hide(cameraFragment)
                .add(R.id.fragment_container, homeFragment, "home")
                .commit()
            activeFragment = homeFragment
        } else {
            val fm = supportFragmentManager
            val home = fm.findFragmentByTag("home") ?: homeFragment
            val cam = fm.findFragmentByTag("camera") ?: cameraFragment
            val cal = fm.findFragmentByTag("calendar") ?: calendarFragment
            val saved = fm.findFragmentByTag("saved") ?: savedFragment
            val chat = fm.findFragmentByTag("aiChat") ?: aiChatFragment
            activeFragment = listOf(home, cam, cal, saved, chat).firstOrNull { it.isVisible } ?: home
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            val target = when (item.itemId) {
                R.id.nav_ai_chat -> aiChatFragment
                R.id.nav_camera -> cameraFragment
                R.id.nav_home -> homeFragment
                R.id.nav_calendar -> calendarFragment
                R.id.nav_saved -> savedFragment
                else -> homeFragment
            }
            if (target !== activeFragment) {
                supportFragmentManager.beginTransaction()
                    .hide(activeFragment ?: homeFragment)
                    .show(target)
                    .commit()
                activeFragment = target
            }
            true
        }
        bottomNav.selectedItemId = R.id.nav_home

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun checkPolicy(sharedPreferences: SharedPreferences, saved: Bundle?) {
        setContentView(R.layout.privacy_policy)
        val confirmButton = findViewById<Button>(R.id.continueButton)
        val checkBox = findViewById<CheckBox>(R.id.checkbox)
        val textView = findViewById<TextView>(R.id.policyText)
        textView.text = Html.fromHtml(
            getString(R.string.privacy_policy).replace("\n", "<br>"),
            Html.FROM_HTML_MODE_LEGACY
        )

        confirmButton.setOnClickListener {
            if (checkBox.isChecked) {
                sharedPreferences.edit { putBoolean("Privacy", true) }
                startMain(sharedPreferences, saved)
            } else {
                Toast.makeText(this, "Please read and confirm our policy and check the box.", Toast.LENGTH_SHORT).show()
                checkPolicy(sharedPreferences, saved)
            }
        }
    }
}
