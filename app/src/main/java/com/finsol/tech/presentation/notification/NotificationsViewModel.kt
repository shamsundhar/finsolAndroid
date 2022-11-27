package com.finsol.tech.presentation.notification

import androidx.lifecycle.ViewModel
import com.finsol.tech.FinsolApplication
import com.finsol.tech.db.AppDatabase
import com.finsol.tech.domain.portfolio.GetPortfolioData
import com.finsol.tech.rabbitmq.MySingletonViewModel.updateRabbitMQNotificationCounter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationsViewModel @Inject constructor() : ViewModel() {
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(FinsolApplication.context)

    fun clearAllNotifications() {
        GlobalScope.launch {
            appDatabase.notificationDao().deleteAll()
            updateRabbitMQNotificationCounter()
        }
    }
}