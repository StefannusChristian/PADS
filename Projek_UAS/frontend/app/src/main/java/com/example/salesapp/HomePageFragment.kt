package com.example.salesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.salesapp.databinding.FragmentHomePageBinding

class HomePageFragment : Fragment() {

    private lateinit var binding: FragmentHomePageBinding
    private val list = ArrayList<HomeResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomePageBinding.inflate(inflater, container, false)

        val rvHome: RecyclerView = binding.rvHome
        rvHome.setHasFixedSize(true)
        list.addAll(getHomeProducts())
        rvHome.layoutManager = GridLayoutManager(context,2)
        val home_adapter = HomePageAdapter(list)
        rvHome.adapter = home_adapter

        val rvHomePromo: RecyclerView = binding.rvHomePromo
        rvHomePromo.setHasFixedSize(true)
        rvHomePromo.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        val home_promo_adapter = HomePagePromoAdapter(list)
        rvHomePromo.adapter = home_promo_adapter

        return binding.root
    }

    private fun getHomeProducts(): ArrayList<HomeResponse>{
        val imageUrls = resources.getStringArray(R.array.image_source)
        val desc = resources.getStringArray(R.array.image_desc)

        val listHomeProducts = ArrayList<HomeResponse>()
        for (i in imageUrls.indices){
            val homeProducts = HomeResponse(
                imageUrls[i],
                desc[i],
            )
            listHomeProducts.add(homeProducts)
        }
        return listHomeProducts
    }

}