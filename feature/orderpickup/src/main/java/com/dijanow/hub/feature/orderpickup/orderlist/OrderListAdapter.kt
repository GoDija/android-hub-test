package com.dijanow.hub.feature.orderpickup.orderlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dija.hub.featurelib.repository.orders.models.Order
import com.dija.hub.featurelib.repository.orders.models.OrderStatus
import com.dijanow.hub.feature.orderpickup.R
import com.dijanow.hub.feature.orderpickup.databinding.OrderlistItemBinding
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class OrderListAdapter(private val onOrderClicked: (Order) -> Unit) :
	RecyclerView.Adapter<OrderListAdapter.OrderViewHolder>() {

	var orderItem: List<Order> = emptyList()
		set(value) {
			field = value
			notifyDataSetChanged()
		}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
		val binding =
			OrderlistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return OrderViewHolder(binding)
	}

	override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
		holder.bindView(orderItem[position])
	}

	override fun getItemCount(): Int = orderItem.size

	inner class OrderViewHolder(private val binding: OrderlistItemBinding) :
		RecyclerView.ViewHolder(binding.root) {

		private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss")
			.withZone(ZoneId.systemDefault())

		init {
			binding.root.setOnClickListener { onOrderClicked(orderItem[adapterPosition]) }
		}

		@SuppressLint("SetTextI18n")
		fun bindView(order: Order) {

			binding.orderId.text = "Order #${order.orderDisplayId}"
			binding.orderStatus.text = order.status.toHumanReadable()
			binding.orderCreatedAt.text = dateFormatter.format(order.createdAt)
			binding.orderStore.text = order.storeName
			binding.orderQuantity.text = "Quantity: ${order.numberOfItems}"

			if (order.status.isNotMoreThan(OrderStatus.PICKED)) {
				binding.orderStatus.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.order_status_bg_picklisted))
			} else {
				binding.orderStatus.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.order_status_bg_packed))
			}
		}
	}


}