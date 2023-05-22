package com.example.salesapp

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
import com.example.salesapp.databinding.FragmentPromoBinding
import com.example.salesapp.databinding.HomeProductPopupBinding
import com.example.salesapp.databinding.MainToolbarBinding

class PromoFragment : Fragment() {

    private lateinit var binding: FragmentPromoBinding
    private lateinit var toolBarBinding: MainToolbarBinding
    private lateinit var homeViewModel: HomeViewModel
    private val promoAdapter: PromoAdapter by lazy { PromoAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPromoBinding.inflate(inflater, container, false)
        toolBarBinding = MainToolbarBinding.bind(binding.root.findViewById(R.id.mainToolbar))
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setupRecyclerViews()

        promoAdapter.setOnItemClickCallback(object:PromoAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Product) {
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

    private fun showProductDetailsDialog(product: Product) {
        val dialogBinding = HomeProductPopupBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        val priceTag = getString(R.string.price_tag)

        val productImage = dialogBinding.dialogProductImage
        val productName = dialogBinding.dialogProductName
        val productPrice = dialogBinding.dialogProductPrice
        val productDescription = dialogBinding.dialogProductDescription
        val productPromo = dialogBinding.dialogProductIsPromo

        productImage.apply {
            Glide.with(context)
                .load(product.img_link)
                .into(this)
        }

        productName.text = product.name

        val productPriceString = priceTag + " " + product.price
        productPrice.text = productPriceString
        productDescription.text = product.description
        productPromo.text = product.promo.toString()

        dialog.show()
    }

}