package com.finsol.tech.presentation.notification

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finsol.tech.R
import com.finsol.tech.presentation.watchlist.adapter.ChildWatchListAdapter1

class NotificationsAdapter(private val context: Context?, private val resources: Resources) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.notiDate.text = "Dummy Date"
        holder.notiMsg.text = "Dummy Text - " + resources.getString(R.string.dummy_noti_text)
    }

    override fun getItemCount(): Int {
        return 10;
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val notiDate: TextView = itemView.findViewById(R.id.date)
        val notiMsg: TextView = itemView.findViewById(R.id.noti_msg)
    }
}