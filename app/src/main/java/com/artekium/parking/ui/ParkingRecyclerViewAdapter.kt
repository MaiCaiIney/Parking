package com.artekium.parking.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.artekium.parking.databinding.FragmentItemBinding

data class DashboardItem(val license: String, val startedAt: String, var finished: Boolean) {
    override fun toString(): String = "Vehículo $license - Hora de ingreso: $startedAt"
}

class ParkingRecyclerViewAdapter :
    ListAdapter<DashboardItem, ParkingRecyclerViewAdapter.ViewHolder>(ParkingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.licenseView.text = item.license
        holder.startedAtView.text = item.startedAt
        holder.finishedView.visibility = if (item.finished) View.VISIBLE else View.GONE
    }

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val licenseView: TextView = binding.itemLicense
        val startedAtView: TextView = binding.itemStartedAt
        val finishedView: TextView = binding.itemFinished
    }
}

class ParkingDiffCallback : DiffUtil.ItemCallback<DashboardItem>() {
    override fun areItemsTheSame(oldItem: DashboardItem, newItem: DashboardItem) =
        oldItem.license == newItem.license && oldItem.startedAt == newItem.startedAt

    override fun areContentsTheSame(oldItem: DashboardItem, newItem: DashboardItem) =
        oldItem.finished == newItem.finished
}