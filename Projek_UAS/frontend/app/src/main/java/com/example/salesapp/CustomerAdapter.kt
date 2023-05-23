package com.example.salesapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.salesapp.databinding.CustomerItemBinding

class CustomerAdapter(private val customerViewModel: CustomerViewModel) : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    var customerList = mutableListOf<GetCustomerResponse>()

    private var onItemClickCallback: OnItemClickCallback? = null

    interface OnItemClickCallback {
        fun onUnsubscribeClicked(sales_username: String, customer_name: String)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class CustomerViewHolder(private val binding: CustomerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val customerImage : ImageView = binding.customerImage
        private val customerName : TextView = binding.customerName
        private val customerAddress : TextView = binding.customerAddress
        val unsubscribeButton: Button = binding.unsubscribeBtn

        init {
            unsubscribeButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val customer = customerList[position]
                    onItemClickCallback?.onUnsubscribeClicked(
                        customerViewModel.salesUsername,
                        customer.name
                    )
                }
            }
        }

        fun bind(customer: GetCustomerResponse) {
            val requestOptions = RequestOptions().transform(RoundedCorners(8))
            with(binding) {
                Glide.with(customerImage.context)
                    .load(customer.img_link)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(customerImage)
                customerName.text = customer.name
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
        holder.bind(customer)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCustomers(customers: List<GetCustomerResponse>) {
        customerList.clear()
        customerList.addAll(customers)
        notifyDataSetChanged()
    }

}