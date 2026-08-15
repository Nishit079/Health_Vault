package com.example.healthvault.ui.vault

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthvault.data.local.HealthRecordEntity
import com.example.healthvault.data.local.HealthVaultDatabase
import com.example.healthvault.data.repository.HealthRecordRepository
import com.example.healthvault.databinding.FragmentVaultBinding
import com.example.healthvault.ui.HealthRecordViewModelFactory
import com.example.healthvault.ui.detail.RecordDetailFragment

class VaultFragment : Fragment() {

    private var _binding: FragmentVaultBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RecordListViewModel
    private lateinit var adapter: RecordAdapter

    private var allRecords: List<HealthRecordEntity> = emptyList()

    private val categories = arrayOf(
        "All Categories",
        "Lab Report",
        "Prescription",
        "Doctor Visit",
        "Vaccination",
        "Hospital Record",
        "Scan / X-Ray",
        "Medical Document",
        "Other"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentVaultBinding.inflate(
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

        adapter = RecordAdapter(
            records = emptyList(),
            onRecordClick = { record ->
                openDetail(record)
            },
            onShareClick = { record ->
                showShareCenter(listOf(record))
            }
        )

        binding.rvRecords.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvRecords.adapter = adapter

        binding.spinnerCategory.adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categories
            )

        binding.spinnerCategory.setSelection(0)

        binding.spinnerCategory.setOnItemSelectedListener(
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    applyFilters()
                }
            }
        )

        binding.etSearch.addTextChangedListener(
            object : android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    applyFilters()
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {}
            }
        )

        binding.fabAdd.setOnClickListener {
            showAddRecordDialog()
        }

        binding.btnShareCenter.setOnClickListener {
            if (allRecords.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "No records available to share.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showShareCenter(allRecords)
            }
        }

        binding.btnTimeline.setOnClickListener {
            showTimeline()
        }

        viewModel.allRecords.observe(viewLifecycleOwner) { records ->

            allRecords = records

            binding.tvRecordCount.text =
                "${records.size} records"

            applyFilters()
        }
    }

    private fun applyFilters() {

        val query = binding.etSearch.text
            ?.toString()
            ?.trim()
            ?.lowercase()
            ?: ""

        val category = binding.spinnerCategory
            .selectedItem
            ?.toString()
            ?: "All Categories"

        val filtered = allRecords.filter { record ->

            val matchesSearch =
                query.isEmpty() ||
                        record.title.lowercase().contains(query) ||
                        record.category.lowercase().contains(query) ||
                        record.doctorName.lowercase().contains(query) ||
                        record.date.lowercase().contains(query)

            val matchesCategory =
                category == "All Categories" ||
                        record.category == category

            matchesSearch && matchesCategory
        }

        adapter.updateRecords(filtered)

        binding.tvEmpty.visibility =
            if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun openDetail(record: HealthRecordEntity) {

        parentFragmentManager.beginTransaction()
            .replace(
                com.example.healthvault.R.id.fragmentContainer,
                RecordDetailFragment.newInstance(record.id)
            )
            .addToBackStack(null)
            .commit()
    }

    private fun showAddRecordDialog() {

        val context = requireContext()

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 10, 40, 10)
        }

        val title = EditText(context).apply {
            hint = "Record title"
        }

        val doctor = EditText(context).apply {
            hint = "Doctor name"
        }

        val date = EditText(context).apply {
            hint = "Date (e.g. 13 Aug 2026)"
        }

        val category = Spinner(context)

        category.adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            categories.drop(1)
        )

        val notes = EditText(context).apply {
            hint = "Notes (optional)"
            minLines = 3
            gravity = android.view.Gravity.TOP
        }

        layout.addView(title)
        layout.addView(category)
        layout.addView(doctor)
        layout.addView(date)
        layout.addView(notes)

        AlertDialog.Builder(context)
            .setTitle("Add Health Record")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->

                val recordTitle =
                    title.text.toString().trim()

                if (recordTitle.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Record title is required.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                viewModel.addRecord(
                    HealthRecordEntity(
                        title = recordTitle,
                        category = category.selectedItem.toString(),
                        doctorName =
                            doctor.text.toString().trim()
                                .ifEmpty { "N/A" },
                        date =
                            date.text.toString().trim()
                                .ifEmpty { "N/A" },
                        notes = notes.text.toString().trim()
                    )
                )

                Toast.makeText(
                    context,
                    "Record added to your Vault.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showTimeline() {

        if (allRecords.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "No records available for timeline.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val timeline = allRecords.joinToString(
            separator = "\n\n"
        ) { record ->

            val icon = when (record.category) {
                "Lab Report" -> "🧪"
                "Prescription" -> "💊"
                "Doctor Visit" -> "🩺"
                "Vaccination" -> "💉"
                "Hospital Record" -> "🏥"
                "Scan / X-Ray" -> "🩻"
                else -> "📋"
            }

            "$icon  ${record.date}\n" +
                    "     ${record.title}\n" +
                    "     ${record.category} • Dr. ${record.doctorName}"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("🕐 Health Timeline")
            .setMessage(timeline)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showShareCenter(records: List<HealthRecordEntity>) {

        val context = requireContext()

        val includeDoctor = android.widget.CheckBox(context).apply {
            text = "Include doctor"
            isChecked = true
        }

        val includeDate = android.widget.CheckBox(context).apply {
            text = "Include date"
            isChecked = true
        }

        val includeCategory = android.widget.CheckBox(context).apply {
            text = "Include category"
            isChecked = true
        }

        val includeNotes = android.widget.CheckBox(context).apply {
            text = "Include personal notes"
            isChecked = false
        }

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 10, 40, 10)
        }

        layout.addView(includeDoctor)
        layout.addView(includeDate)
        layout.addView(includeCategory)
        layout.addView(includeNotes)

        AlertDialog.Builder(context)
            .setTitle("📤 Advanced Share Center")
            .setMessage(
                "${records.size} record(s) selected.\n" +
                        "Choose the information included in your Share Pack."
            )
            .setView(layout)
            .setPositiveButton("Generate Share Pack") { _, _ ->

                val shareText = buildSharePack(
                    records = records,
                    includeDoctor = includeDoctor.isChecked,
                    includeDate = includeDate.isChecked,
                    includeCategory = includeCategory.isChecked,
                    includeNotes = includeNotes.isChecked
                )

                shareText(shareText)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun buildSharePack(
        records: List<HealthRecordEntity>,
        includeDoctor: Boolean,
        includeDate: Boolean,
        includeCategory: Boolean,
        includeNotes: Boolean
    ): String {

        return buildString {

            appendLine("╔════════════════════════════════╗")
            appendLine("║        HEALTH VAULT            ║")
            appendLine("║        HEALTH SHARE PACK       ║")
            appendLine("╚════════════════════════════════╝")
            appendLine()
            appendLine("Records shared: ${records.size}")
            appendLine("Generated by Health Vault")
            appendLine()

            records.forEachIndexed { index, record ->

                appendLine("${index + 1}. ${record.title}")

                if (includeCategory) {
                    appendLine("   Category: ${record.category}")
                }

                if (includeDoctor) {
                    appendLine("   Doctor: ${record.doctorName}")
                }

                if (includeDate) {
                    appendLine("   Date: ${record.date}")
                }

                if (includeNotes && record.notes.isNotBlank()) {
                    appendLine("   Notes: ${record.notes}")
                }

                appendLine()
            }

            appendLine("────────────────────────────────")
            appendLine("🔒 Shared from Health Vault")
        }
    }

    private fun shareText(text: String) {

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_SUBJECT,
                "Health Vault Share Pack"
            )
            putExtra(Intent.EXTRA_TEXT, text)
        }

        startActivity(
            Intent.createChooser(
                intent,
                "Share Health Pack via"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}