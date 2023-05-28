package com.example.salesapp.Home

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.salesapp.AddToCartRequest
import com.example.salesapp.R
import com.example.salesapp.databinding.HomeFragmentBinding
import com.example.salesapp.databinding.HomeProductPopupBinding

class HomePageFragment : Fragment() {

    private lateinit var binding: HomeFragmentBinding
    private lateinit var homeViewModel: HomeViewModel
    private val homeAdapter: HomePageAdapter by lazy { HomePageAdapter(homeViewModel) }
    private val homePromoAdapter: HomePagePromoAdapter by lazy { HomePagePromoAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setupRecyclerViews()

        homeAdapter.setOnItemClickCallback(object : HomePageAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ProductResponse) {
                showProductDetailDialog(data)
            }

            override fun onAddToCartClicked(salesUsername: String, productId: Int, Qty: Int) {
                TODO("Not yet implemented")
            }
        })

        homePromoAdapter.setOnItemClickCallback(object : HomePagePromoAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ProductResponse) {
                showProductDetailDialog(data)
            }
        })

        observeProductList()
        observePromosList()

        homeViewModel.fetchAvailProducts()
        homeViewModel.fetchPromos()

        val goToPromo: Button = binding.goToPromoBtn
        goToPromo.setOnClickListener {
            findNavController().navigate(R.id.action_homePageFragment_to_promoFragment)
        }

        return binding.root
    }

    private fun setupRecyclerViews() {
        binding.rvHome.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter = homeAdapter
        }

        binding.rvHomePromo.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = homePromoAdapter
        }
    }

    private fun observeProductList() {
        homeViewModel.products.observe(viewLifecycleOwner) { productList ->
            productList?.let {
                homeAdapter.setProducts(it)
            }
        }
    }

    private fun observePromosList() {
        homeViewModel.promos.observe(viewLifecycleOwner) { promosList ->
            promosList?.let {
                homePromoAdapter.setPromos(it)
            }
        }
    }

    private fun showProductDetailDialog(productResponse: ProductResponse) {
        val dialogBinding = HomeProductPopupBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        val productImage = dialogBinding.dialogProductImage
        val productName = dialogBinding.dialogProductName
        val productPrice = dialogBinding.dialogProductPrice
        val productDescription = dialogBinding.dialogProductDescription
        val productPromo = dialogBinding.dialogProductPromo
        val productId = dialogBinding.dialogProductId

        productImage.apply {
            Glide.with(requireContext())
                .load(productResponse.img_link)
                .into(this)
        }

        productName.text = productResponse.name

        productPrice.text = getString(R.string.price_tag,productResponse.price.toString())
        productDescription.text = productResponse.description
        productPromo.text = productResponse.promo.toString()
        productId.text = getString(R.string.product_id,productResponse.id.toString())


        val addToCartBtn: Button = dialogBinding.addToCartBtn
        val qtyET: EditText = dialogBinding.addToCartQty

        qtyET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val quantity = s.toString().toIntOrNull() ?: 0
                addToCartBtn.isEnabled = quantity > 0
            }
        })

        addToCartBtn.setOnClickListener {
            val qty: Int = dialogBinding.addToCartQty.text.toString().toIntOrNull() ?: 0
            val productToAdd =
                AddToCartRequest(sales_username = "salesA", product_id = productResponse.id, qty = qty)
            homeViewModel.addToCart(productToAdd)
            dialog.dismiss()
        }

        dialog.show()
    }

}

