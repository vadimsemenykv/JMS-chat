package com.cyber.jms_chat;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;
import javax.jms.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by vadim on 29.12.14.
 */
public class JMSsender {
    private String nick;

    private ConnectionFactory factory;
    private QueueConnection connection;
    private Session session;
    private MessageProducer queueSender;

    private AtomicBoolean stop = new AtomicBoolean(false);

    public JMSsender(String nick) {
        this.nick = nick;

        try{
            factory = new com.sun.messaging.ConnectionFactory();
            factory.setProperty(ConnectionConfiguration.imqAddressList, "mq://127.0.0.1:7676, mq://127.0.0.1:7676");
            connection = factory.createQueueConnection("admin", "admin");
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic ioQueue = session.createTopic("ChatTopic");

            queueSender = session.createProducer(ioQueue);
        }
        catch (JMSException e){
            System.out.println("Error: " + e.getMessage());
        }
        finally{
            close();
        }
    }

    public void send(String msg) {
        try {
            TextMessage outMsg = session.createTextMessage(msg);
            outMsg.setStringProperty("NickName", JMSsender.this.nick);
            queueSender.send(outMsg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        stop.set(true);
        close();
    }

    private void close() {
        if (queueSender != null && stop.get()) {
            try {
                queueSender.close();
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
