package com.example.salesapp

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.salesapp.databinding.FragmentHomePageBinding
import com.example.salesapp.databinding.HomeProductPopupBinding

class HomePageFragment : Fragment() {

    private lateinit var binding: FragmentHomePageBinding
    private lateinit var homeViewModel: HomeViewModel
    private val homeAdapter: HomePageAdapter by lazy { HomePageAdapter() }
    private val homePromoAdapter: HomePagePromoAdapter by lazy { HomePagePromoAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomePageBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setupRecyclerViews()

        homeAdapter.setOnItemClickCallback(object: HomePageAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Product) {
                showProductDetailsDialog(data)
            }
        })

        homePromoAdapter.setOnItemClickCallback(object: HomePagePromoAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Product) {
                showProductDetailsDialog(data)
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

    private fun showProductDetailsDialog(product: Product) {
        val dialogBinding = HomeProductPopupBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        val priceTag = getString(R.string.price_tag)

        val productImage = dialogBinding.dialogProductImage
        val productName = dialogBinding.dialogProductName
        val productPrice = dialogBinding.dialogProductPrice
        val productDescription = dialogBinding.dialogProductDescription
        val productIsPromo = dialogBinding.dialogProductIsPromo

        productImage.apply {
            Glide.with(context)
                .load(product.img_link)
                .into(this)
        }

        productName.text = product.name

        val productPriceString = priceTag + " " + product.price
        productPrice.text = productPriceString
        productDescription.text = product.description
        productIsPromo.text = product.is_promo.toString()

        dialog.show()
    }


}
