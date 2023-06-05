package com.example.salesapp.Transaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salesapp.R
import com.example.salesapp.SharedViewModel.SharedViewModel
import com.example.salesapp.databinding.ToolbarMainLayoutBinding
import com.example.salesapp.databinding.TransactionFragmentBinding

class TransactionFragment: Fragment() {
    private lateinit var binding: TransactionFragmentBinding
    private lateinit var transactionAdapter: TransactionAdapter
    private val transactionViewModel: TransactionViewModel by viewModels()
    private lateinit var toolBarBinding: ToolbarMainLayoutBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TransactionFragmentBinding.inflate(layoutInflater)
        toolBarBinding = ToolbarMainLayoutBinding.bind(binding.root.findViewById(R.id.mainToolbar))

        toolBarBinding.toolbarBtn.setOnClickListener{
            findNavController().navigate(R.id.homePageFragment)
        }

        toolBarBinding.toolbarHeader.text = "Transaction"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        transactionViewModel.orderList.observe(viewLifecycleOwner, Observer { orders ->
            transactionAdapter.orderList = orders
        })

        fetchOrder()

    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(context)
        binding.recyclerView.adapter = transactionAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)


        transactionAdapter.setOnItemClickListener(object : TransactionAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                showConfirmationDialog(position)
            }

        })


        transactionAdapter.setOnButtonClickListener(object : TransactionAdapter.onButtonClickListener{
            override fun onButtonClick(position: Int) {
                showConfirmationDialog(position)
            }

        })
    }

    private fun fetchOrder(){
        transactionViewModel.fetchOrder(sharedViewModel.salesUsername)
    }

    private fun showConfirmationDialog(position: Int) {

        if (transactionAdapter.orderList[position].status == "active"){
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to change the status to 'canceled'?")
                .setPositiveButton("Yes") { dialog, _ ->
                    changeOrderStatus(position)
                    transactionViewModel.cancelOrder(sharedViewModel.salesUsername, transactionAdapter.orderList[position].id)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()}
    }

    private fun changeOrderStatus(position: Int) {
        transactionViewModel.changeOrderStatus(position)
        transactionAdapter.notifyItemChanged(position)
    }

}