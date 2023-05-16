package com.example.salesapp

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.salesapp.databinding.CartListItemBinding

class CartAdapter(private val list: ArrayList<CartResponse>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: CartListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val cartImage = binding.cartImage
        private val cartDesc = binding.cartDesc
        private val carPrive = binding.cartPrice

        fun bind(response: CartResponse) {
            with(binding) {
                Glide.with(cartImage.context)
                    .load(response.imageUrl)
                    .into(cartImage)
                cartDesc.text = response.desc
                cartPrice.text = response.price
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
