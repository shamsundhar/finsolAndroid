package com.finsol.tech.presentation.notification

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.finsol.tech.R
import com.finsol.tech.db.AppDatabase
import com.finsol.tech.db.Notification
import com.finsol.tech.util.Utilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationsAdapter(private val context: Context, private val resources: Resources) :
    RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    private val appDatabase: AppDatabase = AppDatabase.getDatabase(context)
    private var allNotification = listOf<Notification>()


    init {
        getAllNotificationFromDB()
    }

    private fun getAllNotificationFromDB() {
        GlobalScope.launch {
            allNotification = appDatabase.notificationDao().getAll()
            withContext(Dispatchers.Main){
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = allNotification.get(position)
        holder.notiDate.text = Utilities.getDateTime(notification.receivedTimeStamp, "yyyy-MM-dd HH:mm:ss")
        holder.notiMsg.text = notification.responseMessage
        updateMessageContentColor(notification,holder)
    }


    private fun updateMessageContentColor(notification: Notification, holder: ViewHolder){
        println("error mesage $notification.responseMessageType")
        if(notification.responseMessageType.equals("Error",true)){
            holder.notiDate.setTextColor(ContextCompat.getColor(context, R.color.red))
            holder.notiMsg.setTextColor(ContextCompat.getColor(context, R.color.red))
        }else if(notification.responseMessageType.equals("Information",true)){
            holder.notiDate.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            holder.notiMsg.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }else{
            //Alert
            holder.notiDate.setTextColor(ContextCompat.getColor(context, R.color.green))
            holder.notiMsg.setTextColor(ContextCompat.getColor(context, R.color.green))

        }

    }

    override fun getItemCount(): Int {
        return allNotification.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val notiDate: TextView = itemView.findViewById(R.id.date)
        val notiMsg: TextView = itemView.findViewById(R.id.noti_msg)
    }
}