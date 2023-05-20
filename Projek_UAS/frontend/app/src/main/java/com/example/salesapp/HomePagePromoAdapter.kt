package com.example.salesapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.salesapp.databinding.HomeListItemBinding

class HomePagePromoAdapter : RecyclerView.Adapter<HomePagePromoAdapter.HomeViewHolder>() {

    private val promosList = mutableListOf<Product>()

    private var onItemClickCallback: OnItemClickCallback? = null
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class HomeViewHolder(private val binding: HomeListItemBinding) :
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
                productDesc.text = product.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = HomeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int = promosList.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val promos = promosList[position]
        holder.bind(promos)

        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(promos)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPromos(products: List<Product>) {
        promosList.clear()
        promosList.addAll(products)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: Product)
    }

}
