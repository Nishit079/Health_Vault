package com.example.healthvault.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.healthvault.MainActivity
import com.example.healthvault.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        binding.cardChangePin.setOnClickListener {
            (requireActivity() as MainActivity).changeVaultPin()
        }

        binding.cardEmergencyProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(com.example.healthvault.R.id.fragmentContainer, com.example.healthvault.ui.emergency.EmergencyProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.tvVersion.text = "Health Vault v2.0"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}