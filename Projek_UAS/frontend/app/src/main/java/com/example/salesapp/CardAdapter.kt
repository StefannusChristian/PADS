package com.example.salesapp

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.salesapp.databinding.CartListItemBinding

class CartAdapter(private val list: ArrayList<CartResponse>,
                  private val viewModel: CartViewModel
) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: CartListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val cartImage = binding.cartImage
        private val cartDesc = binding.cartDesc
        private val cartPrice = binding.cartPrice
        private val cartQuantity = binding.cartQtyTv
        private val cartIncrementButton = binding.cartIncBtn
        private val cartDecrementButton = binding.cartDecBtn
        private val cartCheckBox = binding.cartCheckbox

        fun bind(response: CartResponse) {
            with(binding) {
                Glide.with(cartImage.context)
                    .load(response.imageUrl)
                    .into(cartImage)
                cartDesc.text = response.desc
                cartPrice.text = response.price
                cartQuantity.text = response.quantity.toString()
                cartCheckBox.isChecked = response.isChecked

                cartIncrementButton.setOnClickListener {
                    viewModel.incrementQuantity(response)
                    cartQuantity.text = response.quantity.toString()
                }

                cartDecrementButton.setOnClickListener {
                    viewModel.decrementQuantity(response)
                    cartQuantity.text = response.quantity.toString()
                }

                cartCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    response.isChecked = isChecked
                    viewModel.updateTotalPrice()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
