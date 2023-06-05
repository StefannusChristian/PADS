package com.example.salesapp.Group

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.salesapp.R
import com.example.salesapp.databinding.GroupFragmentBinding
import com.example.salesapp.databinding.ToolbarMainLayoutBinding

class GroupFragment : Fragment() {

    private lateinit var binding: GroupFragmentBinding
    private lateinit var toolBarBinding: ToolbarMainLayoutBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GroupFragmentBinding.inflate(inflater, container, false)
        toolBarBinding = ToolbarMainLayoutBinding.bind(binding.root.findViewById(R.id.mainToolbar))
        toolBarBinding.toolbarBtn.setOnClickListener {
            findNavController().navigate(R.id.homePageFragment)
        }
        toolBarBinding.toolbarHeader.text = "Group Members"
        return binding.root
    }


}