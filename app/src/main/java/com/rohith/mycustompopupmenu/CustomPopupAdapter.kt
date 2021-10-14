package com.rohith.mycustompopupmenu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.rohith.mycustompopupmenu.databinding.CustomListItemBinding

class CustomPopupAdapter(private val callback: RecyclerviewCallbacks<CustomItem>
) : RecyclerView.Adapter<CustomPopupAdapter.CustomViewHolder>() {


    private val customItems = mutableListOf<CustomItem>()
    //var callback: RecyclerviewCallbacks<CustomItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding =
            CustomListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val customItem = this.customItems[position]
        holder.binding.run {
            if (customItem.icon != null){
                imageIcon.isVisible = true
                imageIcon.setImageDrawable(customItem.icon)
            }
            tvName.text = customItem.title
            root.setOnClickListener {
                callback?.onCustomItemClick(
                    holder.binding.root,
                    position,
                    customItem
                )
            }
        }
    }

//    fun setOnClick(click: RecyclerviewCallbacks<CustomItem>){
//        callback = click
//    }


    override fun getItemCount() = this.customItems.size

    fun addCustomItem(customList: List<CustomItem>) {
        this.customItems.addAll(customList)
        notifyDataSetChanged()
    }

    inner class CustomViewHolder(val binding: CustomListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    interface RecyclerviewCallbacks<T> {
        fun onCustomItemClick(view: View, position: Int, item: T)
    }
}