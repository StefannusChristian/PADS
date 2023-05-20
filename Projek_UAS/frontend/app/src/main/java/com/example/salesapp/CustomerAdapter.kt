package com.example.salesapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.salesapp.databinding.CustomerItemBinding

class CustomerAdapter : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    private val productList = mutableListOf<Product>()

    private var onItemClickCallback: OnItemClickCallback? = null
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class CustomerViewHolder(private val binding: CustomerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val productImage = binding.productImage
        private val productDesc = binding.productDescription

        fun bind(product: Product) {
            val requestOptions = RequestOptions().transform(RoundedCorners(8))
            with(binding) {
                Glide.with(productImage.context)
                    .load(product.img_link)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(productImage)
                binding.root.setOnClickListener{onItemClickCallback?.onItemClicked(product)}
                productDesc.text = product.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding = CustomerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerViewHolder(binding)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val product = productList[position]

        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(product)
        }

        holder.bind(product)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setProducts(products: List<Product>) {
        productList.clear()
        productList.addAll(products)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: Product)
    }


}