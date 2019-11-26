/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.testejms;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.jms.Session;
import javax.jms.MessageListener;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 *
 * @author mpcsj
 */
public class JmsSubscriber implements MessageListener {

    public static final String FACTORY_NAME = "ConnectionFactory";
    public static final String TOPIC_NAME = "topic/salaMsg1";

    private TopicConnection connect;
    private String idUser;

    public JmsSubscriber(String factoryName, String topicName, String idUser)
            throws JMSException, NamingException {
        this.idUser = idUser;
        Context jndiContext = new InitialContext();
        TopicConnectionFactory factory = (TopicConnectionFactory) jndiContext.lookup(factoryName);
        Topic topic = (Topic) jndiContext.lookup(topicName);
        this.connect = factory.createTopicConnection();
        TopicSession session = connect.createTopicSession(false,
                Session.AUTO_ACKNOWLEDGE);
        TopicSubscriber subscriber = session.createSubscriber(topic);
        subscriber.setMessageListener(this);
        connect.start();
    }

    public String getIdUser() {
        return idUser;
    }

    public void onMessage(Message message) {
        try {
            TextMessage textMsg = (TextMessage) message;
            String text = textMsg.getText();
            if (!textMsg.getStringProperty(JmsPublisher.USER_ID).equals(getIdUser())) {
                // se a mensagem nao veio da propria pessoa quem mandou, ela
                // vê a msg
                String res = textMsg.getStringProperty(JmsPublisher.USER_NAME)
                        + ": " + text;
                System.out.println(res);
            }
        } catch (JMSException ex) {

            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            new JmsSubscriber(FACTORY_NAME,
                    TOPIC_NAME, System.currentTimeMillis() + "");
        } else {
            new JmsSubscriber(args[0], args[1], System.currentTimeMillis() + "");
        }
    }
}
