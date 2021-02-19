package com.dijanow.hub.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.dijanow.hub.feature.home.databinding.HomeActivityBinding
import com.dijanow.hub.feature.home.settings.SettingsFragment
import com.dijanow.hub.feature.orderpickup.orderlist.OrderListFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

	private lateinit var binding: HomeActivityBinding
	private lateinit var mainScope: CoroutineScope

	private val viewModel: HomeActivityViewModel by viewModels()
	private var newOrderDialog: AlertDialog? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = HomeActivityBinding.inflate(LayoutInflater.from(this))
		setContentView(binding.root)

		binding.bottomNavigation.setOnNavigationItemSelectedListener {
			when (it.itemId) {
				R.id.page_orders -> openOrdersPage()
				R.id.page_settings -> openSettingsPage()
				else -> throw RuntimeException("WTF!? $it")
			}

			true
		}

		if (savedInstanceState == null) {
			openOrdersPage()
		}
	}

	private fun openOrdersPage() {
		val fragment = supportFragmentManager.findFragmentByTag(ORDERS_PAGE_TAG) ?: OrderListFragment()

		supportFragmentManager.commit {
			replace(R.id.container, fragment, ORDERS_PAGE_TAG)
		}

	}

	private fun openSettingsPage() {
		val fragment = supportFragmentManager.findFragmentByTag(SETTINGS_PAGE_TAG) ?: SettingsFragment()

		supportFragmentManager.commit {
			replace(R.id.container, fragment, SETTINGS_PAGE_TAG)
		}

	}

	override fun onResume() {
		super.onResume()

		mainScope = MainScope()
		mainScope.launch {
			viewModel.viewModelFlow.collect {
				if (it.newOrderDialog) {
					showNewOrdersReceived()
				}
			}
		}
	}

	override fun onPause() {
		super.onPause()
		mainScope.cancel()
	}

	override fun onDestroy() {
		newOrderDialog?.dismiss()
		super.onDestroy()
	}

	private fun showNewOrdersReceived() {
		newOrderDialog?.dismiss()

		newOrderDialog = AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog)
			.setTitle("New order received")
			.setMessage("You received a new orders!")
			.setPositiveButton("Gotcha") { dialog, _ ->
				mainScope.launch { viewModel.newOrderDialogSeen() }
				dialog.dismiss()
			}
			.setCancelable(false)
			.show()
	}

	companion object {
		private const val ORDERS_PAGE_TAG = "ORDERS_PAGE_TAG"
		private const val SETTINGS_PAGE_TAG = "SETTINGS_PAGE_TAG"
	}
}