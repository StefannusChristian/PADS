package com.example.salesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salesapp.databinding.FragmentHomePageBinding

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
        observeProductList()
        observePromosList()

        homeViewModel.fetchProducts()
        homeViewModel.fetchPromos()

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



}
