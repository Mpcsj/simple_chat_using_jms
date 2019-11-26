/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.testejms;

import java.util.Scanner;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicPublisher;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author mpcsj
 */
public class JmsPublisher {

    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    private TopicPublisher publisher;
    private TopicSession session;
    private TopicConnection connect;
    private String userID, userName;

    public JmsPublisher(String factoryName, String topicName)
            throws JMSException, NamingException {
        Context jndiContext = new InitialContext();
        TopicConnectionFactory factory = (TopicConnectionFactory) jndiContext.lookup(factoryName);
        Topic topic = (Topic) jndiContext.lookup(topicName);
        this.connect = factory.createTopicConnection();
        this.session = connect.createTopicSession(false,
                Session.AUTO_ACKNOWLEDGE);
        this.publisher = session.createPublisher(topic);
        this.userID = System.currentTimeMillis() + "";
    }

    public void publish(String message) throws JMSException {
        TextMessage textMsg = this.session.createTextMessage();
        textMsg.setStringProperty(USER_ID, getUserID());
        textMsg.setStringProperty(USER_NAME, getUserName());
        textMsg.setText(message);
//        System.out.println("Publicando mensagem: " + message);
        this.publisher.publish(textMsg);
    }

    public String getUserID() {
        return userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        if (userName == null) {
            return "";
        }
        return userName;
    }

    public void close() throws JMSException {
        this.connect.close();
    }

    public static void main(String[] args) throws Exception {
        JmsPublisher publisher;
        publisher = new JmsPublisher(JmsSubscriber.FACTORY_NAME,
                JmsSubscriber.TOPIC_NAME);

        System.out.println("Entrando no grupo");
        // criando um objeto para ouvir mensagens do grupo
        JmsSubscriber subscriber = new JmsSubscriber(
                JmsSubscriber.FACTORY_NAME, JmsSubscriber.TOPIC_NAME,
                publisher.getUserID());

        // criando um leitor de inputs do usuario
        Scanner sc = new Scanner(System.in);
        System.out.println("Diga-nos seu nome: ");
        // definindo o nome do usuario
        publisher.setUserName(sc.nextLine());
        // informando a todos no grupo subscrito que o
        // usuario atual acabou de entrar
        publisher.publish("entrei");
        String msg;
        do {
            msg = sc.nextLine();
            publisher.publish(msg + "\n");
        } while (!msg.equals("adeus"));
        publisher.publish("estou saindo");
        System.exit(0);
    }
}
