package com.example.healthvault

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.healthvault.ui.home.HomeFragment
import com.example.healthvault.ui.insights.InsightsFragment
import com.example.healthvault.ui.settings.SettingsFragment
import com.example.healthvault.ui.vault.VaultFragment
import com.example.healthvault.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences

    companion object {
        private const val PREFS = "health_vault_security"
        private const val PIN_KEY = "vault_pin"
        private const val DEFAULT_PIN = "1234"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        if (!preferences.contains(PIN_KEY)) {
            preferences.edit().putString(PIN_KEY, DEFAULT_PIN).apply()
        }

        setupNavigation()

        if (savedInstanceState == null) {
            showFragment(HomeFragment())
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }

        showPinLock()
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> showFragment(HomeFragment())
                R.id.nav_vault -> showFragment(VaultFragment())
                R.id.nav_insights -> showFragment(InsightsFragment())
                R.id.nav_settings -> showFragment(SettingsFragment())
            }
            true
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun showPinLock() {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 12, 48, 8)
        }

        val pinInput = EditText(this).apply {
            hint = "4-digit PIN"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
            maxLines = 1
        }

        container.addView(pinInput)

        val dialog = AlertDialog.Builder(this)
            .setTitle("🔐 Unlock Health Vault")
            .setMessage("Enter your Vault PIN to continue.")
            .setView(container)
            .setCancelable(false)
            .setPositiveButton("Unlock", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val entered = pinInput.text.toString()
                val saved = preferences.getString(PIN_KEY, DEFAULT_PIN)

                if (entered == saved) {
                    dialog.dismiss()
                } else {
                    pinInput.text.clear()
                    Toast.makeText(
                        this,
                        "Incorrect PIN. Try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialog.show()
    }

    fun changeVaultPin() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 12, 48, 8)
        }

        val oldPin = EditText(this).apply {
            hint = "Current PIN"
            inputType = 2
        }

        val newPin = EditText(this).apply {
            hint = "New 4-digit PIN"
            inputType = 2
        }

        layout.addView(oldPin)
        layout.addView(newPin)

        AlertDialog.Builder(this)
            .setTitle("Change Vault PIN")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val saved = preferences.getString(PIN_KEY, DEFAULT_PIN)
                val old = oldPin.text.toString()
                val new = newPin.text.toString()

                if (old == saved && new.length == 4) {
                    preferences.edit().putString(PIN_KEY, new).apply()
                    Toast.makeText(
                        this,
                        "Vault PIN updated successfully.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Enter the correct current PIN and a 4-digit new PIN.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}