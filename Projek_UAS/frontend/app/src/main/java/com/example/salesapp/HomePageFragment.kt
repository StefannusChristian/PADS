package com.example.salesapp

import android.app.Dialog
import android.os.Bundle
import android.util.Log
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
import com.example.salesapp.databinding.AddCustomerPopupBinding
import com.example.salesapp.databinding.FragmentHomePageBinding
import com.example.salesapp.databinding.HomeProductPopupBinding

class HomePageFragment : Fragment(){

    private lateinit var binding: FragmentHomePageBinding
    private lateinit var homeViewModel: HomeViewModel
    private val homeAdapter: HomePageAdapter by lazy { HomePageAdapter(homeViewModel) }
    private val homePromoAdapter: HomePagePromoAdapter by lazy { HomePagePromoAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomePageBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setupRecyclerViews()

        homeAdapter.setOnItemClickCallback(object : HomePageAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Product) {
                showProductDetailDialog(data)
            }

            override fun onAddToCartClicked(salesUsername: String, productId: Int, Qty: Int) {
                TODO("Not yet implemented")
            }
        })

        homePromoAdapter.setOnItemClickCallback(object : HomePagePromoAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Product) {
                showProductDetailDialog(data)
            }
        })

        observeProductList()
        observePromosList()

        homeViewModel.fetchProducts()
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

    private fun showProductDetailDialog(product: Product) {
        val dialogBinding = HomeProductPopupBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        val priceTag = getString(R.string.price_tag)

        val productImage = dialogBinding.dialogProductImage
        val productName = dialogBinding.dialogProductName
        val productPrice = dialogBinding.dialogProductPrice
        val productDescription = dialogBinding.dialogProductDescription
        val productPromo = dialogBinding.dialogProductIsPromo
        val productId = dialogBinding.dialogProductId

        productImage.apply {
            Glide.with(requireContext())
                .load(product.img_link)
                .into(this)
        }

        productName.text = product.name

        val productPriceString = priceTag + " " + product.price
        productPrice.text = productPriceString
        productDescription.text = product.description
        productPromo.text = product.promo.toString()
        productId.text = "Product ID: "+product.id.toString()


        val addToCartBtn: Button = dialogBinding.addToCartBtn
        addToCartBtn.setOnClickListener{
            val qty: Int = dialogBinding.addToCartQty.text.toString().toIntOrNull()?:0
            val productToAdd = AddToCartRequest(sales_username = "salesA", product_id = product.id, qty = qty)
            homeViewModel.addToCart(productToAdd)
            dialog.dismiss()
        }

        dialog.show()
    }

}

