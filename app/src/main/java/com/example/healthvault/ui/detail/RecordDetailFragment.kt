package com.example.healthvault.ui.detail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.healthvault.data.local.HealthRecordEntity
import com.example.healthvault.data.local.HealthVaultDatabase
import com.example.healthvault.data.repository.HealthRecordRepository
import com.example.healthvault.databinding.FragmentRecordDetailBinding
import com.example.healthvault.ui.HealthRecordViewModelFactory
import com.example.healthvault.ui.vault.RecordListViewModel

class RecordDetailFragment : Fragment() {

    private var _binding: FragmentRecordDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RecordListViewModel

    private var recordId: Int = 0
    private var currentRecord: HealthRecordEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recordId = arguments?.getInt(ARG_ID) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecordDetailBinding.inflate(
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

            val record = records.firstOrNull {
                it.id == recordId
            }

            if (record == null) {
                parentFragmentManager.popBackStack()
                return@observe
            }

            currentRecord = record

            binding.tvTitle.text = record.title
            binding.tvCategory.text = record.category
            binding.tvDoctor.text = record.doctorName
            binding.tvDate.text = record.date
            binding.tvNotes.text =
                record.notes.ifBlank { "No personal notes added." }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnDelete.setOnClickListener {
            currentRecord?.let { record ->

                AlertDialog.Builder(requireContext())
                    .setTitle("Delete record?")
                    .setMessage(
                        "This record will be removed from your Vault."
                    )
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteRecord(record)

                        Toast.makeText(
                            requireContext(),
                            "Record deleted.",
                            Toast.LENGTH_SHORT
                        ).show()

                        parentFragmentManager.popBackStack()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }

        binding.btnShare.setOnClickListener {

            currentRecord?.let { record ->

                val text = buildString {
                    appendLine("Health Vault — Health Record")
                    appendLine()
                    appendLine("Title: ${record.title}")
                    appendLine("Category: ${record.category}")
                    appendLine("Doctor: ${record.doctorName}")
                    appendLine("Date: ${record.date}")

                    if (record.notes.isNotBlank()) {
                        appendLine("Notes: ${record.notes}")
                    }

                    appendLine()
                    appendLine("🔒 Shared from Health Vault")
                }

                val intent =
                    android.content.Intent(
                        android.content.Intent.ACTION_SEND
                    ).apply {
                        type = "text/plain"
                        putExtra(
                            android.content.Intent.EXTRA_SUBJECT,
                            "Health Record: ${record.title}"
                        )
                        putExtra(
                            android.content.Intent.EXTRA_TEXT,
                            text
                        )
                    }

                startActivity(
                    android.content.Intent.createChooser(
                        intent,
                        "Share record via"
                    )
                )
            }
        }
    }

    companion object {

        private const val ARG_ID = "record_id"

        fun newInstance(id: Int): RecordDetailFragment {

            return RecordDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID, id)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}