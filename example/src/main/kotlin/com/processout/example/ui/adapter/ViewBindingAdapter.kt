package com.processout.example.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

class ViewBindingAdapter<VB : ViewBinding, Item>(
    items: List<Item>,
    bindingClass: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val bindHolder: View.(VB?, Item) -> Unit
) : BaseViewBindingAdapter<VB, Item>(items, bindingClass) {

    private var itemClick: View.(Item) -> Unit = {}
    var viewBinding: VB? = null

    constructor(
        items: List<Item>,
        bindingClass: (LayoutInflater, ViewGroup, Boolean) -> VB,
        bindHolder: View.(VB?, Item) -> Unit,
        itemViewClick: View.(Item) -> Unit = {}
    ) : this(items, bindingClass, bindHolder) {
        this.itemClick = itemViewClick
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (position == holder.bindingAdapterPosition) {
            this.viewBinding = binding
            holder.itemView.bindHolder(binding, itemList[position])
        }
    }

    override fun onItemClick(itemView: View, position: Int) {
        itemView.itemClick(itemList[position])
    }
}
