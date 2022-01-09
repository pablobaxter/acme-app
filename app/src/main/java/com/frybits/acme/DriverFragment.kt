package com.frybits.acme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.frybits.acme.viewmodels.DriverViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverFragment : Fragment() {

    private val driverViewModel by viewModels<DriverViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}