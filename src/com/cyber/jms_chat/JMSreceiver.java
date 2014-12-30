package com.cyber.jms_chat;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;
import javax.jms.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by vadim on 29.12.14.
 */
public class JMSreceiver implements MessageListener{
    Session session = null;
    ConnectionFactory factory;
    QueueConnection connection = null;
    MessageConsumer consumer = null;

    AtomicBoolean stop = new AtomicBoolean(false);

    public JMSreceiver() {
        try {
            factory = new com.sun.messaging.ConnectionFactory();
            factory.setProperty(ConnectionConfiguration.imqAddressList, "mq://localhost:7676,mq://localhost:7676");
            connection = factory.createQueueConnection("admin", "admin");
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic ioQueue = session.createTopic("ChatTopic");

            consumer = session.createConsumer(ioQueue);
            consumer.setMessageListener(this);
        } catch (JMSException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            close();
        }
    }

    public void onMessage(Message msg){
        String msgText;
        try{
            if (msg instanceof TextMessage){
                msgText= ((TextMessage) msg).getText();
                System.out.println(msg.getStringProperty("NickName") + ": " + msgText);
            }else{
                System.out.println("Got a non-text message");
            }
        }
        catch (JMSException e){
            System.out.println("Error while consuming a message: " + e.getMessage());
        }
    }

    public void stop() {
        stop.set(true);
        close();
    }

    private void close() {
        if (consumer != null && stop.get()) {
            try {
                consumer.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        if (session != null && stop.get()) {
            try {
                session.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        if (connection != null && stop.get()) {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
