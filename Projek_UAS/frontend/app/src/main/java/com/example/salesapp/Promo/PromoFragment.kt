package com.example.salesapp.Promo

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.salesapp.AddToCartRequest
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
    private val sharedViewModel: SharedViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
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
                showProductDetailDialog(data)
            }
        })

        observePromosList()

        toolBarBinding.toolbarBtn.setOnClickListener {
            findNavController().navigate(R.id.homePageFragment)
        }

        toolBarBinding.toolbarHeader.text = "Promos"

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
                AddToCartRequest(sales_username = sharedViewModel.salesUsername, product_id = productResponse.id, qty = qty)
            homeViewModel.addToCart(productToAdd)
            dialog.dismiss()
        }

        dialog.show()
    }

}