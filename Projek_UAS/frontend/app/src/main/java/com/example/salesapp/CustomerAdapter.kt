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

    private val customerList = mutableListOf<Customer>()

    private var onItemClickCallback: OnItemClickCallback? = null
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class CustomerViewHolder(private val binding: CustomerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val customerImage = binding.customerImage
        private val customerName = binding.customerName
        private val customerAddress = binding.customerAddress

        fun bind(customer: Customer) {
            val requestOptions = RequestOptions().transform(RoundedCorners(8))
            with(binding) {
                Glide.with(customerImage.context)
                    .load(customer.img_link)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(customerImage)
                binding.root.setOnClickListener{onItemClickCallback?.onItemClicked(customer)}
                customerName.text = customer.username
                customerAddress.text = customer.address
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding = CustomerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerViewHolder(binding)
    }

    override fun getItemCount(): Int = customerList.size

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = customerList[position]

        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(customer)
        }

        holder.bind(customer)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCustomers(customers: List<Customer>) {
        customerList.clear()
        customerList.addAll(customers)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: Customer)
    }


}