package com.processout.example.ui.shared

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.processout.example.ui.adapter.ViewBindingAdapter

inline fun <VB : ViewBinding> ViewGroup.viewBinding(
    binding: (LayoutInflater, ViewGroup, Boolean) -> VB
): VB = binding(LayoutInflater.from(context), this, false)

fun <VB : ViewBinding, Item> RecyclerView.setup(
    items: List<Item>,
    bindingClass: (LayoutInflater, ViewGroup, Boolean) -> VB,
    bindHolder: View.(VB?, Item) -> Unit,
    itemClick: View.(Item) -> Unit = {},
    manager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)
): ViewBindingAdapter<VB, Item> {
    val viewBindingAdapter by lazy {
        ViewBindingAdapter(
            items,
            bindingClass,
            { binding: VB?, item: Item -> bindHolder(binding, item) },
            { itemClick(it) }
        )
    }

    layoutManager = manager
    adapter = viewBindingAdapter
    return viewBindingAdapter
}
