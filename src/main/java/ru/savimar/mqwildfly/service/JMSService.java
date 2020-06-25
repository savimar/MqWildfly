package ru.savimar.mqwildfly.service;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ru.savimar.mqwildfly.Entity.Person;
import ru.savimar.mqwildfly.Repository.PersonRepository;


import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.*;
import java.util.List;


@Stateless
@LocalBean
public class JMSService {

    @Resource(mappedName = "java:/ConnectionFactory")
    ConnectionFactory factory;

    @Resource(mappedName = "java:/jms/queue/ExpiryQueue")
    Queue queue;

    @EJB
    XMLService xmlService;
    @EJB
    PersonRepository repository;


    private static final Logger LOGGER = LoggerFactory.getLogger(JMSService.class);


    public String receive() throws JMSException {
        Connection connection;
        TextMessage textMsg;
        connection = factory.createConnection();
        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);
        try {
            // Consumer
            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();

            textMsg = (TextMessage) consumer.receive();
            LOGGER.info("Получение сообщения " + textMsg.getText());
            System.out.println("Received: " + textMsg.getText());

        } finally {
            if (session != null) {
                session.close();
            }
            connection.close();
        }
        return textMsg.getText();
    }

    private void send(String txt) throws JMSException {
        Connection connection = null;
        try {
            connection = factory.createConnection();
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(queue);
            Message msg = session.createTextMessage(txt);

            producer.send(msg);
            LOGGER.info("Отправка сообщения " + txt);
            System.out.println("Sending text '" + txt + "'");
            session.close();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }

    }

    public String sendToJMS(String xml) throws Exception {
        long middleNamesBegin = System.currentTimeMillis();
        List<String> middleNames = xmlService.parseSax(xml);
        long middleNamesEnd = System.currentTimeMillis();
        long middleNamesDelta = middleNamesEnd - middleNamesBegin;

        /* parseXPath*/
        long lastNamesBegin = System.currentTimeMillis();
        List<String> lastNames = xmlService.parseXPath(xml);
        long lastNamesEnd = System.currentTimeMillis();
        long lastNamesDelta = lastNamesEnd - lastNamesBegin;

        /*parseStax*/
        long firstNamesBegin = System.currentTimeMillis();
        List<String> firstNames = xmlService.parseStax(xml);
        long firstNamesEnd = System.currentTimeMillis();
        long firstNamesDelta = firstNamesEnd - firstNamesBegin;

        /*parseToDb*/
        long dbBegin = System.currentTimeMillis();
        xmlService.parseToDb(xml);
        long dbEnd = System.currentTimeMillis();
        long dbDelta = dbEnd - dbBegin;

        boolean b = xmlService.checkXMLforXSD("C:\\Users\\Maria\\Downloads\\Telegram Desktop\\example61.xml", "C:\\Users\\Maria\\Downloads\\ServiceV6_1_9.xsd");
        System.out.println("XML соответствует XSD : " + b);

        String str = String.format("Имена: %s, отчества %s, фамилии %s. \nВыполнение parseSax %d, выполнение parseXPath %d, выполнение parseStax %d, выполнение parseToDb %d \nXML соответствует XSD : %s", firstNames.toString(), middleNames.toString(), lastNames.toString(), middleNamesDelta, lastNamesDelta, firstNamesDelta, dbDelta, b);

        send(str);
        return str;
    }

    public List<Person> findAll() {
        return repository.findAll();
    }
}


