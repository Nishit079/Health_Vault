package com.example.healthvault.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.healthvault.R
import com.example.healthvault.data.local.HealthVaultDatabase
import com.example.healthvault.data.repository.HealthRecordRepository
import com.example.healthvault.databinding.FragmentHomeBinding
import com.example.healthvault.ui.HealthRecordViewModelFactory
import com.example.healthvault.ui.vault.RecordListViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RecordListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(
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

        val dao = HealthVaultDatabase
            .getDatabase(requireContext())
            .healthRecordDao()

        val repository = HealthRecordRepository(dao)

        val factory = HealthRecordViewModelFactory(repository)

        viewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[RecordListViewModel::class.java]

        viewModel.allRecords.observe(viewLifecycleOwner) { records ->

            binding.tvTotalRecords.text = records.size.toString()

            val doctors = records
                .map { it.doctorName }
                .filter { it.isNotBlank() && it != "N/A" }
                .distinct()
                .size

            binding.tvDoctorCount.text = doctors.toString()

            val categories = records
                .map { it.category }
                .distinct()
                .size

            binding.tvCategoryCount.text = categories.toString()

            if (records.isEmpty()) {
                binding.tvRecent.text =
                    "No records yet. Add your first health record from the Vault."
            } else {
                binding.tvRecent.text =
                    "Latest: ${records.first().title}"
            }
        }

        binding.cardOpenVault.setOnClickListener {
            requireActivity()
                .findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    R.id.bottomNavigation
                )
                .selectedItemId = R.id.nav_vault
        }

        binding.cardInsights.setOnClickListener {
            requireActivity()
                .findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    R.id.bottomNavigation
                )
                .selectedItemId = R.id.nav_insights
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}