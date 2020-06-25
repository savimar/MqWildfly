package ru.savimar.mqwildfly.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ru.savimar.mqwildfly.Entity.Person;
import ru.savimar.mqwildfly.Repository.PersonRepository;
import ru.savimar.mqwildfly.service.JMSService;
import ru.savimar.mqwildfly.service.XMLService;
import ru.savimar.mqwildfly.xml.StaxStreamProcessor;

import javax.ejb.EJB;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@WebServlet(urlPatterns = "/show")
public class ShowAll extends HttpServlet {

    @EJB
    JMSService jmsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowAll.class);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        Writer writer = resp.getWriter();

        switch (action == null ? "info" : action) {
            case "jms":
                try {
                  writer.write( jmsService.receive());
                } catch (JMSException e) {
                    LOGGER.error(e.getMessage());
                }
                break;
            case "info":
            default:
                List<Person> users = jmsService.findAll();

                if (users == null || users.isEmpty()) {
                    writer.write("You have no users");
                } else {
                    for (Person user : users) {
                        writer.write(user.toString() + "<br/>");
                    }
                }
                break;
        }

        writer.close();
     }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/xml;charset=UTF-8");
        PrintWriter writer = response.getWriter();

        Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
        String xml = s.hasNext() ? s.next() : "";

        String str = null;
        try {
            str = jmsService.sendToJMS(xml);
        } catch (Exception e) {
           LOGGER.error(e.getMessage());
        }

        writer.write(str);
        writer.close();

    }

}
