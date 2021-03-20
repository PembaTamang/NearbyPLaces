package com.example.nearbyplaces.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nearbyplaces.databinding.ItemRecyclerViewBinding
import com.example.nearbyplaces.models.LocationResponse
import com.example.nearbyplaces.models.ModelRecycler
import com.example.nearbyplaces.utils.GlideInstance
import com.example.nearbyplaces.utils.mlog


class RecyclerAdapter(val arrayList: ArrayList<ModelRecycler>,private val listener : (Int) -> Unit) : RecyclerView.Adapter<RecyclerAdapter.FVH>() {
   inner class FVH(private val itemBinding: ItemRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: ModelRecycler) {
            itemBinding.apply {
                header.text = data.name
                subHeader.text = data.type
                GlideInstance.getInstance(image.context).load(data.imgUrl).into(image)
                mlog("image URL ${data.imgUrl}")

            }
            itemBinding.root.setOnClickListener {
                listener.invoke(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FVH {
        return FVH(
            ItemRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FVH, position: Int) {
        val data = arrayList[position]
        holder.bind(data)
    }

    override fun getItemCount() = arrayList.size
}