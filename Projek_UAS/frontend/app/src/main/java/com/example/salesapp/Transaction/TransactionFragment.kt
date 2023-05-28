package com.example.salesapp.Transaction

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.example.salesapp.R
import com.example.salesapp.databinding.ToolbarMainLayoutBinding
import com.example.salesapp.databinding.TransactionFragmentBinding

class TransactionFragment: Fragment() {
    private lateinit var binding: TransactionFragmentBinding
    private lateinit var transactionAdapter: TransactionAdapter
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var toolBarBinding: ToolbarMainLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TransactionFragment", "MASUK ON CREATE VIEW!")
        binding = TransactionFragmentBinding.inflate(layoutInflater)
        toolBarBinding = ToolbarMainLayoutBinding.bind(binding.root.findViewById(R.id.mainToolbar))

        toolBarBinding.toolbarBtn.setOnClickListener{
            findNavController().navigate(R.id.homePageFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("TransactionFragment", "MASUK ON VIEW CREATED!")

        setupRecyclerView()
        Log.d("TransactionFragment", "INI DIBAWAH SETUP RECYCLER VIEW!")

        viewModel.orderList.observe(viewLifecycleOwner, Observer { orders ->
            transactionAdapter.orderList = orders
        })

        fetchOrder()
        Log.d("TransactionFragment", "INI DIBAWAH SETUP ORDER!")

    }


    private fun setupRecyclerView() {
        Log.d("TransactionFragment", "MASUK SETUP RECYCLER VIEW")
        transactionAdapter = TransactionAdapter()
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
        Log.d("TransactionFragment", "MASUK FETCH ORDER")
        viewModel.fetchOrder()
    }

    private fun showConfirmationDialog(position: Int) {

        if (transactionAdapter.orderList[position].status == "active"){
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to change the status to 'canceled'?")
                .setPositiveButton("Yes") { dialog, _ ->
                    changeOrderStatus(position)
                    Log.d("Transaction Fragment", "Pop Up")
                    viewModel.cancelOrder("salesA", transactionAdapter.orderList[position].id)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()}
    }

    private fun changeOrderStatus(position: Int) {
        viewModel.changeOrderStatus(position)

        transactionAdapter.notifyItemChanged(position)
    }

}