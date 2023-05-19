package com.example.salesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salesapp.databinding.FragmentPromoBinding

class PromoFragment : Fragment() {

    private lateinit var binding: FragmentPromoBinding
    private lateinit var homeViewModel: HomeViewModel
    private val homePromoAdapter: HomePagePromoAdapter by lazy { HomePagePromoAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPromoBinding.inflate(inflater, container, false)

        setupRecyclerViews()
        observePromosList()

        return binding.root
    }

    private fun setupRecyclerViews() {
        binding.rvPromo.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = homePromoAdapter
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