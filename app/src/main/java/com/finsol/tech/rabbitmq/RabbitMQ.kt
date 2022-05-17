package com.finsol.tech.rabbitmq

import com.rabbitmq.client.*
import java.nio.charset.StandardCharsets

object RabbitMQ {

    private var factory: ConnectionFactory? = null
    private var subscriberThread: Thread? = null
    private var channel : Channel? = null
    private var securityIDList : ArrayList<String> = ArrayList()

    private val QUEUE_NAME = "responseQ"
    private val EXCHANGE_NAME = "BroadcastSenderEx1" // 'ResponseEx'
    private val ROUTE_KEY = "broadcast.master." //response


    init {
        subscribeToRabbitMQ()
    }

    fun getFactory() : ConnectionFactory {
        if (factory == null) {
            factory = ConnectionFactory()
            factory?.username = "finsoltech"
            factory?.password = "pass123!"
            factory?.virtualHost = "/"
            factory?.host = "43.204.110.131"
            factory?.port = 9009
        }
        println("Factory : ")
        return factory!!
    }

    private fun subscribeToRabbitMQ() {
        subscriberThread = Thread {
            getFactory().newConnection().use { connection ->
                connection.createChannel().use { channel ->
                    this.channel = channel
                    if(securityIDList.size > 0){
                        registerPendingConsumers()
                        securityIDList.clear()
                    }
                    channel.exchangeDeclare(EXCHANGE_NAME, "topic")
                    while (true){

                    }
                }
            }

        }
        subscriberThread!!.start()
    }

    private fun registerPendingConsumers() {
        securityIDList.forEach {
            registerConsumer(it)
        }
    }

    fun registerConsumer(securityID : String) {
        if(channel == null){
            securityIDList.add(securityID)
        }
        val queueName = channel?.queueDeclare()?.queue
        channel?.queueBind(queueName, EXCHANGE_NAME, ROUTE_KEY+securityID)
        val deliverCallback =
            DeliverCallback { consumerTag: String?, delivery: Delivery ->
                val message = String(delivery.body, StandardCharsets.UTF_8)
                println("[$consumerTag] Received message: '$message'")
            }
        val cancelCallback = CancelCallback { consumerTag: String? ->
            println("[$consumerTag] was canceled")
        }
        channel?.basicConsume(queueName, false, "myConsumerTag"+securityID, deliverCallback, cancelCallback)
    }
}