package com.dijanow.hub.feature.orderpickup.orderlist

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dija.hub.featurelib.repository.orders.models.Order
import com.dijanow.hub.feature.orderpickup.databinding.OrderlistFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderListFragment : Fragment() {

	private val viewModel: OrderListViewModel by viewModels()

	private var _binding: OrderlistFragmentBinding? = null
	private val binding: OrderlistFragmentBinding
		get() = _binding!!

	private val adapter = OrderListAdapter { onOrderClicked(it) }
	private lateinit var mainScope: CoroutineScope

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = OrderlistFragmentBinding.inflate(inflater, container, false)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		mainScope = MainScope()

		binding.orderList.let {
			it.adapter = adapter
			it.layoutManager =
				LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
		}

		mainScope.launch {
			viewModel.orderListViewFlow.collect {
				adapter.orderItem = it.items
				binding.labelHubName.text = it.hubName
				binding.onlineButton.isEnabled = it.allowEnableSync
				binding.onlineButton.isChecked = it.syncEnabled
			}
		}

		binding.onlineButton.setOnCheckedChangeListener { _, isChecked ->
			startService(isChecked)
		}
	}

	override fun onDestroyView() {
		_binding = null
		mainScope.cancel()
		super.onDestroyView()
	}

	private fun startService(start: Boolean) {
		val intent = Intent(requireContext(), Class.forName("com.dijanow.hub.feature.home.ordersync.OrderSyncService"))
		if (start) {
			startForegroundService(requireActivity(),intent)
		} else {
			requireActivity().stopService(intent)
		}
	}

	private fun onOrderClicked(order: Order) {
		mainScope.launch {

			var dialog: ProgressDialog? = null

			viewModel.onOrderClicked(order).collect {

				dialog?.cancel()

				if (it.loading) {
					dialog = ProgressDialog(requireContext())

					with(dialog!!) {
						setMessage("Loading order..")
						setButton("Cancel") { dialog, _ ->
							dialog.cancel()
						}
						show()
					}
				}

			}

		}


	}

}