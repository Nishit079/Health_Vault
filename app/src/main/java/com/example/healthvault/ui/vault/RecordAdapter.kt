package com.example.healthvault.ui.vault

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthvault.data.local.HealthRecordEntity
import com.example.healthvault.databinding.ItemRecordBinding

class RecordAdapter(
    private var records: List<HealthRecordEntity>,
    private val onRecordClick: (HealthRecordEntity) -> Unit,
    private val onShareClick: (HealthRecordEntity) -> Unit
) : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    inner class RecordViewHolder(
        val binding: ItemRecordBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecordViewHolder {

        val binding = ItemRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecordViewHolder,
        position: Int
    ) {

        val record = records[position]

        holder.binding.tvTitle.text = record.title
        holder.binding.tvCategory.text = record.category
        holder.binding.tvDateDoctor.text =
            "${record.date}  •  Dr. ${record.doctorName}"

        holder.binding.tvCategoryIcon.text =
            when (record.category) {
                "Lab Report" -> "🧪"
                "Prescription" -> "💊"
                "Doctor Visit" -> "🩺"
                "Vaccination" -> "💉"
                "Hospital Record" -> "🏥"
                "Scan / X-Ray" -> "🩻"
                "Medical Document" -> "📋"
                else -> "❤️"
            }

        holder.itemView.setOnClickListener {
            onRecordClick(record)
        }

        holder.binding.ibShare.setOnClickListener {
            onShareClick(record)
        }
    }

    override fun getItemCount(): Int = records.size

    fun updateRecords(newRecords: List<HealthRecordEntity>) {
        records = newRecords
        notifyDataSetChanged()
    }
}