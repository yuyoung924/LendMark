package com.example.lendmark.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lendmark.data.local.RecentRoomEntity
import com.example.lendmark.databinding.ItemRecentRoomBinding

class RecentlyViewedRoomsAdapter(
    private val items: List<RecentRoomEntity>,
    private val onClick: ((RecentRoomEntity) -> Unit)? = null   // ← ⭐ 선택 파라미터
) : RecyclerView.Adapter<RecentlyViewedRoomsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRecentRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecentRoomEntity) {
            binding.tvRoomName.text = item.roomName

            // onClick이 null이 아니면 클릭 이벤트 설정
            binding.root.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentRoomBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
