package com.finsol.tech.rabbitmq

import com.finsol.tech.data.model.toMarketData
import com.rabbitmq.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

object RabbitMQ {
    private lateinit var connection: Connection
    private var mySingletonViewModel: MySingletonViewModel? = null
    private var factory: ConnectionFactory? = null
    private var subscriberThread: Thread? = null

    private val QUEUE_NAME_USER = "responseQ"
    private val EXCHANGE_NAME_USER = "ResponseEx"
    private val ROUTE_KEY_USER = "response"

    private val QUEUE_NAME = "responseQ"
    private val EXCHANGE_NAME = "BroadcastSenderEx1" // 'ResponseEx'
    private val ROUTE_KEY = "broadcast.master." //response

    private val consumerList: ArrayList<subscriberModel> = ArrayList()

    init {
        connectToRabbitMQ()
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
            factory?.host = "43.204.110.131"
            factory?.port = 9009
            factory?.requestedHeartbeat = 60
        }
        return factory!!
    }

    private fun subscribeForUserUpdates(userID: String) {
        if (!::connection.isInitialized) {
            return
        }
        connection.createChannel().use { channel ->
            channel.exchangeDeclare(EXCHANGE_NAME_USER + userID, "direct")
            channel?.queueBind(
                QUEUE_NAME_USER + userID,
                EXCHANGE_NAME_USER + userID,
                ROUTE_KEY_USER + userID
            )
            val deliverCallback =
                DeliverCallback { consumerTag: String?, delivery: Delivery ->
                    val message = String(delivery.body, StandardCharsets.UTF_8)
//                println(" User message: '$message'")
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

        }


    }

    private fun connectToRabbitMQ() {
        subscriberThread = Thread {
            try {
                connection = getFactory().newConnection()
                subscribeForUserUpdates("1120")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
        subscriberThread!!.start()
    }


    fun subscribeForMarketData(securityID: String) {
        if (!::connection.isInitialized) {
            return
        }

        val consumerTag = "myConsumerTag" + securityID
        val list = consumerList.filter {
            it.consumerTag == consumerTag
        }

        if (list.isEmpty()) {
            Thread {
                runBlocking(Dispatchers.IO) {
                    val channel = connection.createChannel()
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
                        false,
                        consumerTag,
                        deliverCallback,
                        cancelCallback
                    )
                    consumerList.add(subscriberModel(securityID, consumerTag, queueName!!))
                }
            }.start()
        }

    }

    private fun updateMarketData(marketData: String) {
        val market = marketData.toMarketData()
        mySingletonViewModel?.updateContract(market)
    }

    fun unregisterAll() {
//        consumerList.forEach {
//            channel?.queueUnbind(it.queueName, EXCHANGE_NAME, ROUTE_KEY + it.securityID)
//        }
//        consumerList.clear()
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

data class subscriberModel(val securityID: String, val consumerTag: String, val queueName: String)