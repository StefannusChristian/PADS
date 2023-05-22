package com.example.salesapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salesapp.databinding.FragmentCartBinding
import com.example.salesapp.databinding.MainToolbarBinding
class CartFragment : Fragment(), CartAdapter.OnItemClickCallback {

    private lateinit var binding: FragmentCartBinding
    private lateinit var toolBarBinding: MainToolbarBinding

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
        binding = FragmentCartBinding.inflate(inflater, container, false)
        toolBarBinding = MainToolbarBinding.bind(binding.root.findViewById(R.id.mainToolbar))
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
            findNavController().navigate(R.id.homePageFragment)
        }

        val removeAllCartBtn = binding.removeAllBtn
        removeAllCartBtn.setOnClickListener {
            val request = RemoveAllCartRequest(sales_username = sharedViewModel.salesUsername)
            cartViewModel.removeAllCarts(request)
        }

        return binding.root
    }

    override fun onRemoveCartClicked(salesUsername: String, product_id: Int) {
        cartViewModel.removeCart(RemoveCartRequest(salesUsername, product_id))
    }

    private fun setupRecyclerViews() {
        binding.rvCart.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
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
            }
        }
    }



}