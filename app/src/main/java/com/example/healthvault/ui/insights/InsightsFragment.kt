package com.example.healthvault.ui.insights

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.healthvault.data.local.HealthVaultDatabase
import com.example.healthvault.data.repository.HealthRecordRepository
import com.example.healthvault.databinding.FragmentInsightsBinding
import com.example.healthvault.ui.HealthRecordViewModelFactory
import com.example.healthvault.ui.vault.RecordListViewModel

class InsightsFragment : Fragment() {

    private var _binding: FragmentInsightsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RecordListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInsightsBinding.inflate(
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

        viewModel = ViewModelProvider(
            requireActivity(),
            HealthRecordViewModelFactory(repository)
        )[RecordListViewModel::class.java]

        viewModel.allRecords.observe(viewLifecycleOwner) { records ->

            binding.tvTotal.text =
                records.size.toString()

            binding.tvDoctors.text =
                records
                    .map { it.doctorName }
                    .filter { it != "N/A" }
                    .distinct()
                    .size
                    .toString()

            binding.tvCategories.text =
                records
                    .map { it.category }
                    .distinct()
                    .size
                    .toString()

            val mostUsed =
                records
                    .groupingBy { it.category }
                    .eachCount()
                    .maxByOrNull { it.value }
                    ?.key

            binding.tvMostUsed.text =
                mostUsed ?: "No data"

            binding.tvInsight.text =
                if (records.isEmpty()) {
                    "Add health records to unlock your Vault insights."
                } else {
                    "Your Vault contains ${records.size} records across " +
                            "${records.map { it.category }.distinct().size} categories. " +
                            "Your most common category is ${mostUsed ?: "Other"}."
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}