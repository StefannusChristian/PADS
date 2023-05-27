package com.example.salesapp.Home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.salesapp.databinding.HomeRvListItemBinding

class HomePagePromoAdapter : RecyclerView.Adapter<HomePagePromoAdapter.HomeViewHolder>() {

    private val promosList = mutableListOf<ProductResponse>()

    private var onItemClickCallback: OnItemClickCallback? = null
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class HomeViewHolder(private val binding: HomeRvListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val productImage = binding.productImage
        private val productDesc = binding.productDescription


        fun bind(productResponse: ProductResponse) {
            val requestOptions = RequestOptions().transform(RoundedCorners(8))
            with(binding) {
                Glide.with(productImage.context)
                    .load(productResponse.img_link)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(productImage)
                productDesc.text = productResponse.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = HomeRvListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    fun setPromos(productResponses: List<ProductResponse>) {
        promosList.clear()
        promosList.addAll(productResponses)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: ProductResponse)
    }

}
