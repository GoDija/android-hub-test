package com.dijanow.hub.feature.home.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.dijanow.hub.feature.home.databinding.SettingsFragmentBinding
import com.dijanow.hub.repository.HubRepository
import com.dijanow.hub.repository.model.InventoryLocation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

	private var mainScope: CoroutineScope? = null
	private var _binding: SettingsFragmentBinding? = null
	private val binding: SettingsFragmentBinding
		get() = _binding!!

	@Inject
	lateinit var hubRepository: HubRepository

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		_binding = SettingsFragmentBinding.inflate(inflater, container, false)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		mainScope = MainScope()

		refreshHubSelected()

	}

	private var hubSelectorDialog: AlertDialog? = null

	private fun openHubSelector() {
		hubSelectorDialog?.cancel()

		hubSelectorDialog = MaterialAlertDialogBuilder(requireContext())
			.setTitle("Select an Hub")
			.setMessage("Loading...")
			.setNegativeButton("Cancel") { d, _ -> d.dismiss() }
			.show()

		mainScope?.launch {

			val locations = hubRepository.getAvailableInventoryLocation()
			hubSelectorDialog?.cancel()

			hubSelectorDialog = MaterialAlertDialogBuilder(requireContext())
				.setItems(locations.map { it.name }.toTypedArray()) { d, which ->
					onHubSelected(locations[which])
					d.dismiss()
				}
				.setNegativeButton("Cancel") { d, _ -> d.dismiss() }
				.show()

		}

	}

	private fun refreshHubSelected() {
		mainScope?.launch {
			val selectedInventoryLocation = hubRepository.getSelectedInventoryLocation()

			if (selectedInventoryLocation == null) {
				binding.hubSelector.text = "Select"
			} else {
				binding.hubSelector.text = selectedInventoryLocation.name
			}

			binding.hubSelector.setOnClickListener { openHubSelector() }
		}
	}

	private fun onHubSelected(location: InventoryLocation) {
		mainScope?.launch {
			hubRepository.storeSelectedInventoryLocation(location)
			refreshHubSelected()
		}
	}

	override fun onDestroyView() {
		mainScope?.cancel()
		hubSelectorDialog?.cancel()
		super.onDestroyView()
	}
}