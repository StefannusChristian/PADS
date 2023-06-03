package com.example.salesapp.Group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.salesapp.databinding.GroupFragmentBinding

class GroupFragment : Fragment() {

    private lateinit var binding: GroupFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GroupFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }


}