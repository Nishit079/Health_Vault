package com.example.healthvault.ui.emergency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.healthvault.data.local.HealthVaultDatabase
import com.example.healthvault.databinding.FragmentEmergencyProfileBinding

class EmergencyProfileFragment : Fragment() {

    private var _binding: FragmentEmergencyProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: EmergencyProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmergencyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = HealthVaultDatabase.getDatabase(requireContext()).emergencyProfileDao()
        val factory = EmergencyProfileViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[EmergencyProfileViewModel::class.java]

        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                binding.etName.setText(it.name)
                binding.etBloodType.setText(it.bloodType)
                binding.etAllergies.setText(it.allergies)
                binding.etMedications.setText(it.medications)
                binding.etEmergencyContact.setText(it.emergencyContact)
            }
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val blood = binding.etBloodType.text.toString().trim()
            val allergies = binding.etAllergies.text.toString().trim()
            val meds = binding.etMedications.text.toString().trim()
            val contact = binding.etEmergencyContact.text.toString().trim()

            viewModel.saveProfile(name, blood, allergies, meds, contact)
            Toast.makeText(requireContext(), "Profile Saved", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
