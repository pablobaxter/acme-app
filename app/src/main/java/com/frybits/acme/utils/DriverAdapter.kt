package com.frybits.acme.utils

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.frybits.acme.R
import com.frybits.acme.databinding.DriverItemViewBinding
import com.frybits.acme.models.Driver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Recycler Adapter for list of drivers.
 */
class DriverAdapter(private val driverList: List<Driver>, lastDriver: Driver? = null) : RecyclerView.Adapter<DriverAdapter.DriverViewHolder>() {

    private val _driverSelectedStateFlow: MutableStateFlow<Driver?> = MutableStateFlow(lastDriver)
    private var lastSelectedItem = lastDriver?.let { driverList.indexOf(it) } ?: -1
    var selectedItem: Int = lastSelectedItem
        set(value) {
            // Grab the driver in that position
            val driver = driverList[value]

            // Set driver
            if (_driverSelectedStateFlow.value == driver) {
                _driverSelectedStateFlow.value = null
            } else {
                _driverSelectedStateFlow.value = driver
            }
            field = value
        }

    /**
     * Exposed flow to notify of selected driver
     */
    val driverSelectedFlow: Flow<Driver?> = _driverSelectedStateFlow.asStateFlow()

    inner class DriverViewHolder(private val binding: DriverItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                // Get position of selected item
                selectedItem = adapterPosition

                // Record last selected item to properly notify of data set changes
                lastSelectedItem = if (lastSelectedItem == -1) {
                    selectedItem
                } else {
                    notifyItemChanged(lastSelectedItem)
                    selectedItem
                }
                notifyItemChanged(selectedItem)
            }
        }

        fun setDriver(driver: Driver) {
            if (driver == _driverSelectedStateFlow.value) {
                val context = binding.root.context
                binding.root.background = ColorDrawable(context.getColor(R.color.teal_200))
            } else {
                binding.root.background = null
            }

            binding.driverName.text = driver.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        return DriverViewHolder(DriverItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        holder.setDriver(driverList[position])
    }

    override fun getItemCount(): Int {
        return driverList.size
    }
}
