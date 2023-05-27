package com.example.salesapp.Inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.salesapp.R
import com.example.salesapp.databinding.CartFragmentBinding

class InventoryFragment : Fragment() {

    private lateinit var binding: CartFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CartFragmentBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.inventory_fragment, container, false)
    }

}