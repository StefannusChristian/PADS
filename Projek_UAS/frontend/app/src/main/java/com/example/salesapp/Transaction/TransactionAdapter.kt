package com.example.salesapp.Transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.salesapp.databinding.TransactionRvListItemBinding

class TransactionAdapter(): RecyclerView.Adapter<TransactionAdapter.MyViewHolder>() {

    private lateinit var mListener: onItemClickListener
    private lateinit var mButtonListener: onButtonClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    interface onButtonClickListener {
        fun onButtonClick(position: Int)
    }

    fun setOnButtonClickListener(listener: onButtonClickListener) {
        mButtonListener = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<OrderItem>(){
        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var orderList: List<OrderItem>
        get() = differ.currentList
        set(value) {differ.submitList(value)}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = TransactionRvListItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return MyViewHolder(binding, mListener, mButtonListener)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }


    class MyViewHolder(private val binding: TransactionRvListItemBinding, listener: onItemClickListener, buttonListener: onButtonClickListener): RecyclerView.ViewHolder(binding.root) {

        val thedate: TextView = binding.dateTextView
        val cusname: TextView = binding.customerNameTextView
        val totalqty: TextView = binding.totalQuantityTextView
        val totalprice: TextView = binding.totalPriceTextView
        val orderstat: TextView = binding.statusTextView
        val statchange: Button = binding.settingsButton

        //    on click listener for each item
        init{
            // Set click listener for the item view
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }

            // Set click listener for the button
            statchange.setOnClickListener {
                buttonListener.onButtonClick(adapterPosition)

            }
        }
        //    on click listener for each item




    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = orderList[position]

        holder.thedate.text = currentItem.date
        holder.cusname.text = currentItem.customer
        holder.totalqty.text = currentItem.qty.toString()
        holder.totalprice.text = currentItem.total_price.toString()
        holder.orderstat.text = currentItem.status

        if (currentItem.status == "active") {
            holder.statchange.visibility = View.VISIBLE
        } else {
            holder.statchange.visibility = View.GONE
        }

    }
}