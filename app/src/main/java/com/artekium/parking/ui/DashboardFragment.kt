package com.artekium.parking.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.artekium.parking.R
import com.artekium.parking.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    private val viewModel by activityViewModels<DashboardViewModel>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonStart.setOnClickListener {
            viewModel.startParking(20)
        }

        binding.listEntries.layoutManager = LinearLayoutManager(context)
        binding.listEntries.adapter = ParkingRecyclerViewAdapter()

        viewModel.started.observe(viewLifecycleOwner) {
            binding.buttonStart.visibility = if (it) View.GONE else View.VISIBLE
            binding.textviewCapacity.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.capacity.observe(viewLifecycleOwner) {
            binding.textviewCapacity.text = getString(R.string.dashboard_capacity, it)
        }

        viewModel.entries.observe(viewLifecycleOwner) { entries ->
            (binding.listEntries.adapter as ParkingRecyclerViewAdapter).apply {
                this.submitList(entries)
                this.notifyDataSetChanged()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}