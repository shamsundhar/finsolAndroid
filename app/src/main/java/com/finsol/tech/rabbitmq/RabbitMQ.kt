package com.finsol.tech.rabbitmq

import com.finsol.tech.FinsolApplication
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.data.model.toMarketData
import com.finsol.tech.db.AppDatabase
import com.finsol.tech.db.Notification
import com.finsol.tech.domain.ctcl.CTCLDetails
import com.finsol.tech.rabbitmq.MySingletonViewModel.updateRabbitMQNotificationCounter
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.AppConstants.KEY_PREF_USER_CTCL
import com.finsol.tech.util.PreferenceHelper
import com.google.gson.Gson
import com.rabbitmq.client.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.nio.charset.StandardCharsets

@OptIn(DelicateCoroutinesApi::class)
object RabbitMQ {
    private var connection: Connection? = null
    private var mySingletonViewModel: MySingletonViewModel? = null
    private var factory: ConnectionFactory? = null
    private var subscriberThread: Thread? = null

    private val QUEUE_NAME_USER = "responseQmobile"
    private val EXCHANGE_NAME_USER = "ResponseExMobile"
    private val ROUTE_KEY_USER = "responsemobile"

    private val QUEUE_NAME = "responseQ"
    private val EXCHANGE_NAME = "BroadcastSenderEx1" // 'ResponseEx'
    private val ROUTE_KEY = "broadcast.master." //response

    private val EXCHANGE_NAME_NOTIFICATION = "ResponseEx"
    private val EXCHANGE_TYPE_NOTIFICATION = "topic"
    private val ROUTE_KEY_NOTIFICATION = "response"
    private val TAG_NOTIFICATION = "NOTIFICATION_TAG"

    private val EXCHANGE_NAME_MARGIN_DATA = "client_margins"
    private val EXCHANGE_TYPE_MARGIN_DATA = "topic"
    private val TAG_MARGIN_DATA = "MARGINDATA_TAG"

    private val consumerList: ArrayList<subscriberModel> = ArrayList()
    private var isUserSubscribed: Boolean = false
    private var isAlreadySubscribedToNotifications: Boolean = false
    private var isAlreadySubscribedToMarginData: Boolean = false
    private var ignoreNotificationList = listOf<Int>(3, 4, 9, 10, 12, 13, 14, 15, 16, 17)

    private var appDb: AppDatabase

    val preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()


    init {
        connectToRabbitMQ()
        appDb = AppDatabase.getDatabase(FinsolApplication.context)
    }

    fun setMySingletonViewModel(mySingletonViewModel: MySingletonViewModel?) {
        this.mySingletonViewModel = mySingletonViewModel
    }

    fun getFactory(): ConnectionFactory {
        if (factory == null) {
            factory = ConnectionFactory()
            factory?.username = "finsoltech"
            factory?.password = "pass123!"
            factory?.virtualHost = "/"

            factory?.port = 9009
            factory?.requestedHeartbeat = 60
        }
        factory?.host = getIPAddress()
        return factory!!
    }


    private fun subscribeToExchangeNotifications() {
        if (connection == null || isAlreadySubscribedToNotifications) {
            return
        }
        isAlreadySubscribedToNotifications = true
        Thread {
            runBlocking(Dispatchers.IO) {
                try {
                    val channel = connection!!.createChannel()
                    channel.exchangeDeclare(EXCHANGE_NAME_NOTIFICATION, EXCHANGE_TYPE_NOTIFICATION)
                    val queueName = channel?.queueDeclare()?.queue

                    channel?.queueBind(
                        queueName,
                        EXCHANGE_NAME_NOTIFICATION,
                        ROUTE_KEY_NOTIFICATION
                    )
                    val deliverCallback =
                        DeliverCallback { consumerTag: String?, delivery: Delivery ->
                            val message = String(delivery.body, StandardCharsets.UTF_8)
//                            println("[$consumerTag] Received message: '$message'")
                            updateNotificationDataToDB(message)
                        }
                    val cancelCallback = CancelCallback { consumerTag: String? ->
//                        println("[$consumerTag] a")
                    }

                    channel?.basicConsume(
                        queueName,
                        true,
                        TAG_NOTIFICATION,
                        deliverCallback,
                        cancelCallback
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    isAlreadySubscribedToNotifications = false
                }
            }
        }.start()
    }

    fun subscribeToMarginData(ctclId: String) {
        if (connection == null || isAlreadySubscribedToMarginData) {
            return
        }
        isAlreadySubscribedToMarginData = true
        Thread {
            runBlocking(Dispatchers.IO) {
                try {
                    val channel = connection!!.createChannel()

                    channel.exchangeDeclare(EXCHANGE_NAME_MARGIN_DATA, EXCHANGE_TYPE_MARGIN_DATA)
                    val queueName = channel?.queueDeclare()?.queue

                    channel?.queueBind(
                        queueName,
                        EXCHANGE_NAME_MARGIN_DATA,
                        ctclId
                    )
                    val deliverCallback =
                        DeliverCallback { consumerTag: String?, delivery: Delivery ->
                            val message = String(delivery.body, StandardCharsets.UTF_8)
//                            println("[$consumerTag] Received message: '$message'")
//                            println("Margin message : $message")
                            updateUserCTCLData(message)
                        }
                    val cancelCallback = CancelCallback { consumerTag: String? ->
//                        println("[$consumerTag] a")
                    }

                    channel?.basicConsume(
                        queueName,
                        true,
                        TAG_MARGIN_DATA,
                        deliverCallback,
                        cancelCallback
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                    isAlreadySubscribedToMarginData = false
                }
            }
        }.start()
    }

    private fun updateUserCTCLData(message: String) {
        try {
            val updatedMsg = message.replace("[", "").replace("]", "").replace("\"", "")
            val marginDetailsArray = updatedMsg.split(",")
            val ctclDetails = CTCLDetails(
                CTCLName = marginDetailsArray[0],
                ExchangeName = marginDetailsArray[1],
                OpenClientBalance = marginDetailsArray[2],
                UtilisedMargin = marginDetailsArray[3],
                AvailableMargin = marginDetailsArray[4],
                TotalIntradayPnL = marginDetailsArray[5],
                TotalPnL = marginDetailsArray[6],
                ErrorMessage = marginDetailsArray[7],
            )
            mySingletonViewModel?.updateUserCTCLData(ctclDetails)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun subscribeForUserUpdates(userID: String) {
        if (connection == null || isUserSubscribed) {
            return
        }
        subscribeToExchangeNotifications()
        isUserSubscribed = true
        Thread {
            runBlocking(Dispatchers.IO) {
                try {
                    val channel = connection!!.createChannel()
                    channel.exchangeDeclare(EXCHANGE_NAME_USER + userID, "direct")
                    channel?.queueBind(
                        QUEUE_NAME_USER + userID,
                        EXCHANGE_NAME_USER + userID,
                        ROUTE_KEY_USER + userID
                    )
                    val deliverCallback =
                        DeliverCallback { consumerTag: String?, delivery: Delivery ->
                            val message = String(delivery.body, StandardCharsets.UTF_8)
//                            println("User message: '$message'")
                            updateUserData(message)
                        }
                    val cancelCallback = CancelCallback { consumerTag: String? ->
                        //println("[$consumerTag] was canceled")
                    }
                    channel?.basicConsume(
                        QUEUE_NAME_USER + userID,
                        true,
                        userID,
                        deliverCallback,
                        cancelCallback
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                    isUserSubscribed = false
                }
            }
        }.start()
    }

    private fun updateNotificationDataToDB(notificationData: String) {
        try {
            val gson = Gson()
            val notiObj = gson.fromJson(notificationData, Notification::class.java)
            if (notiObj.responseCode == 2) {
                mySingletonViewModel?.setUserLogout(true)
                return
            }

            if (notiObj.responseCode == 1) {
                notiObj.responseMessage = notiObj.responseMessage + " Connected"
            }

            if (ignoreNotificationList.contains(notiObj.responseCode)) {
                return
            }
            notiObj.receivedTimeStamp = System.currentTimeMillis()
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    appDb.notificationDao().insert(notiObj)
                    updateRabbitMQNotificationCounter()
                } catch (e: Exception) {
                    println("error isssss $e")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateUserData(message: String) {
        try {
            val userDataJsonObj = JSONObject(message).get("userOrderAgentObject")
            val commonResponse = JSONObject(message).get("commonResponse")
            if (userDataJsonObj != null && !userDataJsonObj.toString().equals("null", true)) {


                val gson = Gson()
                val pendingOrderModel =
                    gson.fromJson(userDataJsonObj.toString(), PendingOrderModel::class.java)
                mySingletonViewModel?.updateUserOrdersData(pendingOrderModel)

                if (pendingOrderModel.ExchangeMessage.isNotEmpty()) {
                    updateNotificationDataToDB(createNotificationMessage(pendingOrderModel.ExchangeMessage))
                }

            } else if (commonResponse != null && !commonResponse.toString().equals("null", true)) {
                updateNotificationDataToDB(commonResponse.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun createNotificationMessage(exchangeMessage: String): String {
        //{"responseMessageType":null,"responseCode":-10,"responseMessage":"Some message","userID":1138}
        val notificationObj = JSONObject()
        notificationObj.put("responseMessageType", null)
        notificationObj.put("responseCode", 9999)
        notificationObj.put("responseMessage", exchangeMessage)
        notificationObj.put(
            "userID", preferenceHelper.getString(
                FinsolApplication.context,
                AppConstants.KEY_PREF_USER_ID,
                ""
            )
        )
        return notificationObj.toString()
    }

    fun reConnectSocket() {
        unregisterAll()
    }

    private fun getIPAddress(): String {
        val ipAddress = preferenceHelper.getString(
            FinsolApplication.context,
            AppConstants.KEY_PREF_IP_ADDRESS_VALUE,
            ""
        )
        return ipAddress
    }

    private fun connectToRabbitMQ() {
        subscriberThread = Thread {
            try {
                connection = getFactory().newConnection()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
        subscriberThread!!.start()
    }


    fun subscribeForMarketData(securityID: String) {
        if (connection == null) {
            return
        }


        val consumerTag = "myConsumerTag" + securityID
        val list = consumerList.filter {
            it.consumerTag == consumerTag
        }

        if (list.isEmpty()) {
            consumerList.add(subscriberModel(securityID, consumerTag))
            Thread {
                runBlocking(Dispatchers.IO) {
                    try {
                        val channel = connection!!.createChannel()
                        channel.exchangeDeclare(EXCHANGE_NAME, "topic")
                        val queueName = channel?.queueDeclare()?.queue
                        channel?.queueBind(queueName, EXCHANGE_NAME, ROUTE_KEY + securityID)
                        val deliverCallback =
                            DeliverCallback { consumerTag: String?, delivery: Delivery ->
                                val message = String(delivery.body, StandardCharsets.UTF_8)
                                updateMarketData(message)
//                            println("[$consumerTag] Received message: '$message'")
                            }
                        val cancelCallback = CancelCallback { consumerTag: String? ->
                            //println("[$consumerTag] was canceled")
                        }

                        channel?.basicConsume(
                            queueName,
                            true,
                            consumerTag,
                            deliverCallback,
                            cancelCallback
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        }

    }

    private fun updateMarketData(marketData: String) {
        try {
            val market = marketData.toMarketData()
            mySingletonViewModel?.updateContract(market)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unregisterAll() {
        Thread {
            runBlocking(Dispatchers.IO) {
                try {
                    connection?.let {
                        it.close()
                        connection = null
                    }
                    connectToRabbitMQ()
                    consumerList.clear()
                    isUserSubscribed = false
                    isAlreadySubscribedToNotifications = false
                    isAlreadySubscribedToMarginData = false
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    fun unregisterSingleSubscriber(securityID: String) {
//        val subscriberModel = consumerList.filter {
//            it.securityID == securityID
//        }
//        if (subscriberModel.isNotEmpty()) {
//            channel?.queueUnbind(
//                subscriberModel[0].queueName,
//                EXCHANGE_NAME,
//                ROUTE_KEY + subscriberModel[0].securityID
//            )
//        }
    }


}

data class subscriberModel(val securityID: String, val consumerTag: String)