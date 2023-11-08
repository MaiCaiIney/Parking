package com.artekium.parking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.artekium.parking.R
import com.artekium.parking.databinding.FragmentNewCarBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewCarFragment : Fragment() {

    private var _binding: FragmentNewCarBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNewCarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.started.observe(viewLifecycleOwner) {
            binding.buttonSave.isEnabled = it
        }

        binding.buttonSave.setOnClickListener {
            val license = binding.edittextLicense.editText?.text?.toString()?.uppercase()
            val brand = binding.edittextBrand.editText?.text?.toString()?.uppercase()
            val model = binding.edittextModel.editText?.text?.toString()?.uppercase()
            val color = binding.edittextColor.editText?.text?.toString()?.uppercase()

            if (license?.isNotEmpty() == true) {
                viewModel.enterVehicle(
                    license = license,
                    brand = brand,
                    model = model,
                    color = color
                )
            } else {
                showToast(R.string.new_car_license_required)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showToast(resource: Int) {
        Toast.makeText(context, resource, Toast.LENGTH_SHORT).show()
    }
}