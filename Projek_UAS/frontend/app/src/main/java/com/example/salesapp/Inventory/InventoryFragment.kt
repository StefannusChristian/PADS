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
    private lateinit var newRecyclerview : RecyclerView
    private lateinit var newArrayList : ArrayList<ProductResponse>
    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var toolBarBinding: ToolbarMainLayoutBinding

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
        fetchTodoData()

        binding.sortButton.setOnClickListener {
            showSortDialog()
        }

    }

    private fun showSortDialog() {
        val options = arrayOf("Available", "Total", "Ordered")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sort By")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> sortByAvailable()
                    1 -> sortByTotal()
                    2 -> sortByOrdered()
                }
            }
            .show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sortByAvailable() {
        val sortedList = inventoryAdapter.prodList.toMutableList().apply {
            sortByDescending { it.available_qty }
        }
        inventoryAdapter.prodList = sortedList
        inventoryAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sortByTotal() {
        val sortedList = inventoryAdapter.prodList.toMutableList().apply {
            sortByDescending { it.total_qty }
        }
        inventoryAdapter.prodList = sortedList
        inventoryAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sortByOrdered() {
        val sortedList = inventoryAdapter.prodList.toMutableList().apply {
            sortByDescending { it.ordered_qty }
        }
        inventoryAdapter.prodList = sortedList
        inventoryAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() = binding.recyclerView.apply {
        inventoryAdapter = InventoryAdapter()
        adapter = inventoryAdapter
        layoutManager = LinearLayoutManager(context)
    }

    private fun fetchTodoData() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressBar.isVisible = true
            val response = try {
                RetrofitClient.product_instance.getAllProducts()
            } catch (e: IOException) {
                binding.progressBar.isVisible = false
                return@launch
            } catch (e: HttpException) {
                binding.progressBar.isVisible = false
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                inventoryAdapter.prodList = response.body()!!
            } else {
                // Tambahin toast message aja
            }

            binding.progressBar.isVisible = false
        }
    }

}