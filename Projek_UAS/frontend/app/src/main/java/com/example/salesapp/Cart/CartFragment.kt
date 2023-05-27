package com.example.salesapp.Cart

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salesapp.*
import com.example.salesapp.databinding.CartFragmentBinding
import com.example.salesapp.databinding.ToolbarMainLayoutBinding
class CartFragment : Fragment(), CartAdapter.OnItemClickCallback, CartAdapter.OnItemCheckedCallback {

    private lateinit var binding: CartFragmentBinding
    private lateinit var toolBarBinding: ToolbarMainLayoutBinding
    private var numCheckedItems: Int = 0


    private lateinit var cartViewModel: CartViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private val cartAdapter: CartAdapter by lazy {
        CartAdapter(
            cartViewModel,
            sharedViewModel,
            requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CartFragmentBinding.inflate(inflater, container, false)
        toolBarBinding = ToolbarMainLayoutBinding.bind(binding.root.findViewById(R.id.mainToolbar))
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)


        setupRecyclerViews()
        observeCartList()

        cartViewModel.fetchCartItems()

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

            cartViewModel.updateDetailCarts(request)

            findNavController().navigate(R.id.homePageFragment)
        }

        binding.selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            cartAdapter.setAllItemsChecked(isChecked)
            cartViewModel.updateTotalPrice()
            updateOrderButtonText()
        }

        val removeAllCartBtn = binding.removeAllBtn
        removeAllCartBtn.setOnClickListener {
            val request = RemoveAllCartRequest(sales_username = sharedViewModel.salesUsername)
            cartViewModel.removeAllCarts(request)
        }

        val orderBtn = binding.orderButton
        orderBtn.setOnClickListener {
            val selectedItems = cartAdapter.getSelectedItems()
            val productIds = selectedItems.map { it.id }
            var custUsername = binding.customerNameEt.text.toString()

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

            cartViewModel.addOrder(request, updateCartRequest)
            custUsername = ""
        }



        updateOrderButtonText()
        return binding.root
    }



    override fun onRemoveCartClicked(salesUsername: String, product_id: Int) {
        val cart = RemoveCartRequest(salesUsername, product_id)
        cartViewModel.removeCart(cart)
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
                for (item in it) {
                    item.isChecked = false
                }
                cartAdapter.notifyDataSetChanged()
                cartViewModel.updateTotalPrice()
                updateOrderButtonText()
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

    override fun onItemChecked(item: GetCartResponse, isChecked: Boolean) {
        updateOrderButtonText()
    }


}