package com.finsol.tech.rabbitmq

import com.finsol.tech.data.model.toMarketData
import com.rabbitmq.client.*
import java.nio.charset.StandardCharsets

object RabbitMQ {
    private var mySingletonViewModel: MySingletonViewModel? = null
    private var factory: ConnectionFactory? = null
    private var subscriberThread: Thread? = null
    private var channel: Channel? = null
    private var securityIDList: ArrayList<String> = ArrayList()

    private val QUEUE_NAME = "responseQ"
    private val EXCHANGE_NAME = "BroadcastSenderEx1" // 'ResponseEx'
    private val ROUTE_KEY = "broadcast.master." //response

    private val consumerList: ArrayList<subscriberModel> = ArrayList()

    init {
        subscribeToRabbitMQ()
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
        }
        return factory!!
    }

    private fun subscribeToRabbitMQ() {
        subscriberThread = Thread {
            try {
                getFactory().newConnection().use { connection ->
                    connection.createChannel().use { channel ->
                        this.channel = channel
                        if (securityIDList.size > 0) {
                            subscribeForPendingList()
                            securityIDList.clear()
                        }
                        channel.exchangeDeclare(EXCHANGE_NAME, "topic")
                        while (true) {

                        }
                    }
                }
            }catch (ex : Exception){
                ex.printStackTrace()
            }

        }
        subscriberThread!!.start()
    }

    private fun subscribeForPendingList() {
        securityIDList.forEach {
            subscribeForMarketData(it)
        }
    }

    fun subscribeForMarketData(securityID: String) {

        if (channel == null) {
            securityIDList.add(securityID)
        }
        val queueName = channel?.queueDeclare()?.queue
        channel?.queueBind(queueName, EXCHANGE_NAME, ROUTE_KEY + securityID)
        val deliverCallback =
            DeliverCallback { consumerTag: String?, delivery: Delivery ->
                val message = String(delivery.body, StandardCharsets.UTF_8)
                updateMarketData(message)
//                println("[$consumerTag] Received message: '$message'")
            }
        val cancelCallback = CancelCallback { consumerTag: String? ->
            //println("[$consumerTag] was canceled")
        }

        val consumerTag = "myConsumerTag" + securityID
        val list = consumerList.filter {
            it.consumerTag == consumerTag
        }
        if(list.isEmpty()){
            channel?.basicConsume(queueName, false, consumerTag, deliverCallback, cancelCallback)
            consumerList.add(subscriberModel(securityID,consumerTag,queueName!!))
        }
    }

    private fun updateMarketData(marketData: String) {
        val market = marketData.toMarketData()
        mySingletonViewModel?.updateContract(market)
    }

    fun unregisterAll(){
        consumerList.forEach {
            channel?.queueUnbind(it.queueName,EXCHANGE_NAME,ROUTE_KEY+it.securityID)
        }
        consumerList.clear()
    }

    fun unregisterSingleSubscriber(securityID: String){
        val subscriberModel = consumerList.filter {
            it.securityID == securityID
        }
        if(subscriberModel.isNotEmpty()){
            channel?.queueUnbind(subscriberModel[0].queueName,EXCHANGE_NAME,ROUTE_KEY+subscriberModel[0].securityID)
        }
    }



}

data class subscriberModel(val securityID: String,val consumerTag : String,val queueName : String)