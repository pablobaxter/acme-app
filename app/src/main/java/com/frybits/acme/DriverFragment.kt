package com.frybits.acme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.frybits.acme.databinding.FragmentDriverBinding
import com.frybits.acme.utils.DriverAdapter
import com.frybits.acme.viewmodels.DriverViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DriverFragment : Fragment() {

    // Injected view model for driver data
    private val driverViewModel by viewModels<DriverViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDriverBinding.inflate(inflater, container, false)

        // Putting all UI work in a coroutine. This will launch after 'onCreateView' has completed.
        lifecycleScope.launch {
            // RecyclerView setup
            val driverRecyclerView = binding.driverRecyclerView
            driverRecyclerView.layoutManager = LinearLayoutManager(context)
            driverRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            val adapter = DriverAdapter(driverViewModel.getDriverList(), driverViewModel.lastSelectedDriver)
            driverRecyclerView.adapter = adapter

            val routeTextView = binding.routeTextView

            // Listener for selected drivers
            adapter.driverSelectedFlow.collect { driver ->
                if (driver == null) {
                    routeTextView.text = ""
                    driverViewModel.resetLastSelectedDriver()
                } else {
                    routeTextView.text = driverViewModel.getRouteForDriver(driver).toString()
                }
            }
        }

        return binding.root
    }
}
