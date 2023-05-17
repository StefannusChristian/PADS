package com.example.salesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.salesapp.databinding.FragmentCartBinding

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private val list = ArrayList<CartResponse>()
    private lateinit var viewModel: CartViewModel
    private val cartAdapter: CartAdapter by lazy { CartAdapter(list, viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[CartViewModel::class.java]

        val rvCart: RecyclerView = binding.rvCart
        rvCart.setHasFixedSize(true)
        rvCart.layoutManager = LinearLayoutManager(context)
        list.addAll(getCartProducts())
        rvCart.adapter = cartAdapter

        // Observe the total price and update the UI
        viewModel.totalPrice.observe(viewLifecycleOwner) { totalPrice ->
            binding.totalPrice.text = totalPrice.toString()
        }

        viewModel.updateTotalPrice()

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
                quantity = 1,
                isChecked = false
            )
            cartProductsList.add(cartProducts)
        }

        viewModel.cartItems.value = cartProductsList
        return cartProductsList
    }

}