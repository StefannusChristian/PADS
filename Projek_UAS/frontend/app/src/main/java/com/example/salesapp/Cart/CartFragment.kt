package com.example.salesapp.Cart

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salesapp.*
import com.example.salesapp.Customer.CustomerViewModel
import com.example.salesapp.SharedViewModel.SharedViewModel
import com.example.salesapp.databinding.CartFragmentBinding
import com.example.salesapp.databinding.ToolbarMainLayoutBinding

class CartFragment : Fragment(), CartAdapter.OnItemClickCallback, CartAdapter.OnItemCheckedCallback {

    private lateinit var binding: CartFragmentBinding
    private lateinit var toolBarBinding: ToolbarMainLayoutBinding
    private lateinit var customerSpinner: Spinner
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var cartViewModel: CartViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CartFragmentBinding.inflate(inflater, container, false)
        toolBarBinding = ToolbarMainLayoutBinding.bind(binding.root.findViewById(R.id.mainToolbar))
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        customerViewModel = ViewModelProvider(this)[CustomerViewModel::class.java]
        customerSpinner = binding.customerSpinner
        cartAdapter = CartAdapter(cartViewModel, sharedViewModel, requireContext())

        setupRecyclerViews()
        observeCartList()

        cartViewModel.fetchCartItems(sharedViewModel.salesUsername)

        customerViewModel.fetchCustomers(sharedViewModel.salesUsername)
        customerViewModel.customerNamesList.observe(viewLifecycleOwner) { listName ->
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listName)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            customerSpinner.adapter = adapter

            customerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    updateOrderButtonText()
                    updateOrderButtonState()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }

        binding.selectAllButton.setOnClickListener {
            val isChecked = cartAdapter.getSelectedItems().isEmpty()
            cartAdapter.setAllItemsChecked(isChecked)
            updateOrderButtonText()
            updateOrderButtonState()
        }

        binding.cancelButton.setOnClickListener {
            cartViewModel.cancelSelection()
        }

        cartViewModel.totalPrice.observe(viewLifecycleOwner) { totalPrice ->
            binding.totalPrice.text = totalPrice.toString()
        }

        cartViewModel.updateTotalPrice()

        toolBarBinding.toolbarBtn.setOnClickListener {
            val updatedCarts = mutableListOf<UpdatedDetailCart>()

            for (cart in cartAdapter.cartList) {
                updatedCarts.add(UpdatedDetailCart(cart.id, cart.qty))
            }

            val request = UpdateDetailCartsRequest(
                sales_username = sharedViewModel.salesUsername,
                updated_detail_carts = updatedCarts
            )

            cartViewModel.updateDetailCarts(request, sharedViewModel.salesUsername)

            findNavController().navigate(R.id.homePageFragment)
        }

        val removeAllCartBtn = binding.removeAllBtn
        removeAllCartBtn.setOnClickListener {
            val request = RemoveAllCartRequest(sales_username = sharedViewModel.salesUsername)
            cartViewModel.removeAllCarts(request,sharedViewModel.salesUsername)
        }

        val orderBtn = binding.orderButton
        orderBtn.setOnClickListener {
            val selectedItems = cartAdapter.getSelectedItems()
            val productIds = selectedItems.map { it.id }
            val custUsername = binding.customerSpinner.selectedItem.toString()

            val request = AddOrderRequest(
                sales_username = sharedViewModel.salesUsername,
                customer_username = custUsername,
                cartproductids = productIds
            )

            val updatedCarts = mutableListOf<UpdatedDetailCart>()
            for (cart in selectedItems) {
                updatedCarts.add(UpdatedDetailCart(cart.id, cart.qty))
            }
            val updateCartRequest = UpdateDetailCartsRequest(
                sales_username = sharedViewModel.salesUsername,
                updated_detail_carts = updatedCarts
            )

            cartViewModel.addOrder(request, updateCartRequest, sharedViewModel.salesUsername)
        }

        updateOrderButtonText()
        updateOrderButtonState()

        return binding.root
    }

    override fun onRemoveCartClicked(salesUsername: String, product_id: Int) {
        val cart = RemoveCartRequest(salesUsername, product_id)
        cartViewModel.removeCart(cart, sharedViewModel.salesUsername)
    }

    override fun onItemChecked(item: GetCartResponse, isChecked: Boolean) {
        updateOrderButtonText()
        updateOrderButtonState()
    }

    private fun setupRecyclerViews() {
        binding.rvCart.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
        cartAdapter.setOnItemClickCallback(this)
        cartAdapter.setOnItemCheckedCallback(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeCartList() {
        cartViewModel.cartList.observe(viewLifecycleOwner) { cartList ->
            cartList?.let {
                cartAdapter.setCartProducts(it)
                cartAdapter.notifyDataSetChanged()
                cartViewModel.updateTotalPrice()
                updateOrderButtonText()
                updateOrderButtonState()
            }
        }
    }

    private fun updateOrderButtonText() {
        val selectedCount = cartAdapter.getSelectedItems().size
        val orderButtonText = if (selectedCount > 0) {
            getString(R.string.order_button_text, selectedCount)
        } else {
            getString(R.string.order_button_default_text)
        }
        binding.orderButton.text = orderButtonText
    }

    private fun updateOrderButtonState() {
        val selectedCount = cartAdapter.getSelectedItems().size
        val selectedCustomer = binding.customerSpinner.selectedItem?.toString()
        val isCustomerSelected = selectedCustomer != "Select a Customer"
        val isAnyItemChecked = selectedCount > 0
        binding.orderButton.isEnabled = isCustomerSelected && isAnyItemChecked
    }
}