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
import androidx.recyclerview.widget.RecyclerView
import com.example.salesapp.databinding.FragmentCartBinding
import com.example.salesapp.databinding.MainToolbarBinding

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var toolBarBinding: MainToolbarBinding
    private val list = ArrayList<CartResponse>()
    private lateinit var viewModel: CartViewModel
    private val cartAdapter: CartAdapter by lazy { CartAdapter(list, viewModel) }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        toolBarBinding = MainToolbarBinding.bind(binding.root.findViewById(R.id.mainToolbar))
        viewModel = ViewModelProvider(this)[CartViewModel::class.java]

        val rvCart: RecyclerView = binding.rvCart
        rvCart.setHasFixedSize(true)
        rvCart.layoutManager = LinearLayoutManager(context)
        list.addAll(getCartProducts())
        rvCart.adapter = cartAdapter

        viewModel.totalPrice.observe(viewLifecycleOwner) { totalPrice ->
            binding.totalPrice.text = totalPrice.toString()
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            list.clear()
            if (items != null) {
                list.addAll(items)
            }
            cartAdapter.notifyDataSetChanged()
        }

        binding.cancelButton.setOnClickListener {
            viewModel.cancelSelection()
        }

        viewModel.updateTotalPrice()

        toolBarBinding.toolbarBtn.setOnClickListener {
            findNavController().navigate(R.id.homePageFragment)
        }

        return binding.root
    }

    private fun getCartProducts(): ArrayList<CartResponse>{
        val imageUrls = resources.getStringArray(R.array.image_source)
        val desc = resources.getStringArray(R.array.image_desc)
        val price = resources.getStringArray(R.array.price_list)

        val cartProductsList = ArrayList<CartResponse>()
        for (i in imageUrls.indices){
            val cartProducts = CartResponse(
                imageUrls[i],
                price[i],
                desc[i],
                quantity = 0,
                isChecked = false
            )
            cartProductsList.add(cartProducts)
        }

        viewModel.cartItems.value = cartProductsList
        return cartProductsList
    }

}