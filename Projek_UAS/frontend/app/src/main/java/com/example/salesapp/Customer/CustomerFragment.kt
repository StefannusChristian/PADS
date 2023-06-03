package com.example.salesapp.Customer

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salesapp.PatchCustomerRequest
import com.example.salesapp.PostCustomerRequest
import com.example.salesapp.R
import com.example.salesapp.SharedViewModel.SharedViewModel
import com.example.salesapp.databinding.CustomerAddCustPopupBinding
import com.example.salesapp.databinding.CustomerFragmentBinding
import com.example.salesapp.databinding.ToolbarMainLayoutBinding

class CustomerFragment : Fragment(), CustomerAdapter.OnItemClickCallback {

    private lateinit var binding: CustomerFragmentBinding
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var toolBarBinding: ToolbarMainLayoutBinding
    private lateinit var addCustomerBinding: CustomerAddCustPopupBinding
    val sharedViewModel: SharedViewModel by activityViewModels()
    private val customerAdapter: CustomerAdapter by lazy { CustomerAdapter(sharedViewModel) }
    private lateinit var sortSpinner: Spinner

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CustomerFragmentBinding.inflate(inflater, container, false)
        toolBarBinding = ToolbarMainLayoutBinding.bind(binding.root.findViewById(R.id.mainToolbar))
        customerViewModel = ViewModelProvider(this)[CustomerViewModel::class.java]

        setupRecyclerViews()
        observeCustomerList()

        customerAdapter.setOnItemClickCallback(this)

        customerViewModel.fetchCustomers(sharedViewModel.salesUsername)

        sortSpinner = binding.customerSortSpinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.customer_sort_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sortSpinner.adapter = adapter
        }

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                if (selectedItem != "Sort By"){
                    customerViewModel.sortCustomerList(selectedItem)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        toolBarBinding.toolbarBtn.setOnClickListener {
            findNavController().navigate(R.id.homePageFragment)
        }

        toolBarBinding.toolbarHeader.text = "Customers List"

        val addCustomerBtn = binding.addCustomerBtn
        addCustomerBtn.setOnClickListener {
            showAddDialog()
        }

        return binding.root
    }

    override fun onUnsubscribeClicked(sales_username: String, customer_name: String) {
        customerViewModel.unsubscribeCustomer(PatchCustomerRequest(sales_username, customer_name),sales_username)
    }

    private fun showSortDialog() {
        val options = arrayOf("Name", "Address")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sort By")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> sortByName()
                    1 -> sortByAddress()
                }
            }
            .show()
    }

    private fun showAddDialog() {
        val dialog = Dialog(requireContext())
        val addCustomerBinding = CustomerAddCustPopupBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(addCustomerBinding.root)

        val addBtn = addCustomerBinding.addBtn
        addBtn.setOnClickListener {
            val username = addCustomerBinding.customerUsername.text.toString()
            val address = addCustomerBinding.customerAddress.text.toString()
            val imageLink = addCustomerBinding.customerImageLink.text.toString()
            val newCustomer = PostCustomerRequest(
                sales_username = sharedViewModel.salesUsername,
                customer_username = username,
                customer_address = address,
                customer_img_link = imageLink
            )
            customerViewModel.addCustomer(newCustomer, sharedViewModel.salesUsername)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun sortByName() {
        customerViewModel.sortCustomersByName()
    }

    private fun sortByAddress() {
        customerViewModel.sortCustomersByAddress()
    }

    private fun setupRecyclerViews() {
        binding.rvCustomer.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = customerAdapter
        }
    }

    private fun observeCustomerList() {
        customerViewModel.customers.observe(viewLifecycleOwner) { customerList ->
            customerList?.let {
                customerAdapter.setCustomers(it)
            }
        }
    }
}
