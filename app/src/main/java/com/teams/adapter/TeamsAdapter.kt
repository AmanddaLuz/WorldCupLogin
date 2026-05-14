package com.teams.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.worldcuplogin.databinding.TeamItemBinding
import com.example.worldcuplogin.databinding.TeamItemShimmerBinding
import com.teams.model.TeamModel

class TeamsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<TeamModel>()
    private var isLoading = true
    private val shimmerCount = 6

    fun updateList(newItems: List<TeamModel>) {
        isLoading = false
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun showLoading(show: Boolean) {
        isLoading = show
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) VIEW_TYPE_SHIMMER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SHIMMER) {
            val binding = TeamItemShimmerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ShimmerViewHolder(binding)
        } else {
            val binding = TeamItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            TeamsViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TeamsViewHolder && !isLoading) {
            holder.bind(items[position])
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading) shimmerCount else items.size
    }

    inner class TeamsViewHolder(
        private val binding: TeamItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TeamModel) {
            binding.tvName.text = item.name
            binding.tvGroup.text = item.group
            binding.linearLayout.setOnClickListener {
                Toast.makeText(binding.root.context, item.name + " - " + item.group, Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class ShimmerViewHolder(
        private val binding: TeamItemShimmerBinding
    ) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val VIEW_TYPE_SHIMMER = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}
