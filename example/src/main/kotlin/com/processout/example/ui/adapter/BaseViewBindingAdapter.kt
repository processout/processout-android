package com.processout.example.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.processout.example.ui.shared.viewBinding

@SuppressLint("NotifyDataSetChanged")
abstract class BaseViewBindingAdapter<VB : ViewBinding, Item> constructor(
    protected var itemList: List<Item>,
    private val bindingClass: (LayoutInflater, ViewGroup, Boolean) -> VB
) : RecyclerView.Adapter<BaseViewBindingAdapter.Holder>() {

    var binding: VB? = null

    init {
        update(itemList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = itemList.size
    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val viewBinding = parent.viewBinding(bindingClass)
        this.binding = viewBinding

        val viewHolder = Holder(viewBinding.root)
        val itemView = viewHolder.itemView

        itemView.tag = viewHolder
        itemView.setOnClickListener {
            val adapterPosition = viewHolder.bindingAdapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onItemClick(itemView, adapterPosition)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = itemList[position]
        holder.itemView.bind(item)
    }

    override fun onViewRecycled(holder: Holder) {
        super.onViewRecycled(holder)
        onViewRecycled(holder.itemView)
    }

    private fun updateAdapterWithDiffResult(result: DiffUtil.DiffResult) {
        result.dispatchUpdatesTo(this)
    }

    private fun calculateDiff(newItems: List<Item>): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(DiffUtilCallback(itemList, newItems))
    }

    private fun update(items: List<Item>) {
        updateAdapterWithDiffResult(calculateDiff(items))
    }

    private fun add(item: Item) {
        itemList.toMutableList().add(item)
        notifyItemInserted(itemList.size)
    }

    private fun remove(position: Int) {
        itemList.toMutableList().removeAt(position)
        notifyItemRemoved(position)
    }

    protected open fun View.bind(item: Item) {}
    protected open fun onViewRecycled(itemView: View) {}
    protected open fun onItemClick(itemView: View, position: Int) {}
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
