package com.finsol.tech.presentation.watchlist


import androidx.recyclerview.widget.DiffUtil
import com.finsol.tech.data.model.Contracts

class WatchListDiffUtils(
    private val oldList: List<Contracts>,
    private val newList: List<Contracts>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].securityID == newList[newItemPosition].securityID
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].securityID != newList[newItemPosition].securityID -> {
                false
            }oldList[oldItemPosition].closePrice != newList[newItemPosition].closePrice -> {
                false
            }
            else -> {
                true
            }
        }
    }
}