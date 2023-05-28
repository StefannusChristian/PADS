package com.example.salesapp.Promo

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.salesapp.Home.HomeViewModel
import com.example.salesapp.Home.ProductResponse
import com.example.salesapp.R
import com.example.salesapp.SharedViewModel.SharedViewModel
import com.example.salesapp.databinding.PromoFragmentBinding
import com.example.salesapp.databinding.HomeProductPopupBinding
import com.example.salesapp.databinding.ToolbarMainLayoutBinding

class PromoFragment : Fragment() {

    private lateinit var binding: PromoFragmentBinding
    private lateinit var toolBarBinding: ToolbarMainLayoutBinding
    private lateinit var homeViewModel: HomeViewModel
    private val promoAdapter: PromoAdapter by lazy { PromoAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = PromoFragmentBinding.inflate(inflater, container, false)
        toolBarBinding = ToolbarMainLayoutBinding.bind(binding.root.findViewById(R.id.mainToolbar))
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setupRecyclerViews()

        promoAdapter.setOnItemClickCallback(object: PromoAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ProductResponse) {
                showProductDetailsDialog(data)
            }
        })

        observePromosList()

        toolBarBinding.toolbarBtn.setOnClickListener {
            findNavController().navigate(R.id.homePageFragment)
        }

        homeViewModel.fetchPromos()

        return binding.root
    }

    private fun setupRecyclerViews() {
        binding.rvPromo.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context,2)
            adapter = promoAdapter
        }
    }

    private fun observePromosList() {
        homeViewModel.promos.observe(viewLifecycleOwner) { promosList ->
            promosList?.let {
                promoAdapter.setPromos(it)
            }
        }
    }

    private fun showProductDetailsDialog(productResponse: ProductResponse) {
        val dialogBinding = HomeProductPopupBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        val priceTag = getString(R.string.price_tag)

        val productImage = dialogBinding.dialogProductImage
        val productName = dialogBinding.dialogProductName
        val productPrice = dialogBinding.dialogProductPrice
        val productDescription = dialogBinding.dialogProductDescription
        val productPromo = dialogBinding.dialogProductPromo

        productImage.apply {
            Glide.with(context)
                .load(productResponse.img_link)
                .into(this)
        }

        productName.text = productResponse.name

        val productPriceString = priceTag + " " + productResponse.price
        productPrice.text = productPriceString
        productDescription.text = productResponse.description
        productPromo.text = productResponse.promo.toString()

        dialog.show()
    }

}