package com.finsol.tech.rabbitmq

import com.finsol.tech.FinsolApplication
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.data.model.toMarketData
import com.finsol.tech.db.AppDatabase
import com.finsol.tech.db.Notification
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

    private var appDb : AppDatabase



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
            factory?.host = "43.204.79.47"
            factory?.port = 9009
            factory?.requestedHeartbeat = 60
        }
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

    private fun subscribeToMarginData(ctclId: String) {
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

    fun subscribeForUserUpdates(userID: String) {
        if (connection == null || isUserSubscribed) {
            return
        }
        subscribeToExchangeNotifications()
        subscribeToMarginData("123")
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
                            updateUserData(message)
//                            println("User message: '$message'")
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

    private fun updateNotificationDataToDB (notificationData : String){
        val gson = Gson()
        val notiObj = gson.fromJson(notificationData, Notification::class.java)
        if(ignoreNotificationList.contains(notiObj.responseCode)){
            return
        }
        notiObj.receivedTimeStamp = System.currentTimeMillis()
        GlobalScope.launch(Dispatchers.IO) {
            try{
                appDb.notificationDao().insert(notiObj)
            }catch (e : Exception){
                println("error isssss $e")
            }
        }
    }

    private fun updateUserData(message: String) {
        val userDataJsonObj = JSONObject(message).get("userOrderAgentObject")
        val gson = Gson()
        val pendingOrderModel =
            gson.fromJson(userDataJsonObj.toString(), PendingOrderModel::class.java)
        mySingletonViewModel?.updateUserOrdersData(pendingOrderModel)

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
                }
            }.start()
        }

    }

    private fun updateMarketData(marketData: String) {
        val market = marketData.toMarketData()
        mySingletonViewModel?.updateContract(market)
    }

    fun unregisterAll() {
        Thread {
            runBlocking(Dispatchers.IO) {
                connection?.let {
                    it.close()
                    connection = null
                    connectToRabbitMQ()
                    consumerList.clear()
                    isUserSubscribed = false
                    isAlreadySubscribedToNotifications = false
                    isAlreadySubscribedToMarginData = false
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