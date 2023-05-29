package com.example.salesapp.Inventory

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.salesapp.API.RetrofitClient
import com.example.salesapp.Home.ProductResponse
import com.example.salesapp.R
import com.example.salesapp.databinding.InventoryFragmentBinding
import com.example.salesapp.databinding.ToolbarMainLayoutBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class InventoryFragment : Fragment() {

    private lateinit var binding: InventoryFragmentBinding
    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var toolBarBinding: ToolbarMainLayoutBinding
    private val viewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = InventoryFragmentBinding.inflate(inflater, container, false)
        toolBarBinding = ToolbarMainLayoutBinding.bind(binding.root.findViewById(R.id.mainToolbar))
        toolBarBinding.toolbarBtn.setOnClickListener{
            findNavController().navigate(R.id.homePageFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel.fetchInventoryData()

        binding.sortButton.setOnClickListener {
            showSortDialog()
        }

        viewModel.inventory.observe(viewLifecycleOwner) { inventory ->
            inventory?.let {
                inventoryAdapter.prodList = it
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
    }

    private fun showSortDialog() {
        val options = arrayOf("Available", "Total", "Ordered")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sort By")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> viewModel.sortByAvailable()
                    1 -> viewModel.sortByTotal()
                    2 -> viewModel.sortByOrdered()
                }
            }
            .show()
    }


    private fun setupRecyclerView() = binding.recyclerView.apply {
        inventoryAdapter = InventoryAdapter()
        adapter = inventoryAdapter
        layoutManager = LinearLayoutManager(context)
    }


}