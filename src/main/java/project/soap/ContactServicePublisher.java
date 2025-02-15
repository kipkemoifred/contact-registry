package project.soap;

import jakarta.xml.ws.Endpoint;

public class ContactServicePublisher {
    public static void main(String[] args) {
        String url = "http://localhost:8080/ws/contactService";
        Endpoint.publish(url, new ContactServiceImpl());
        System.out.println("SOAP Service is running at: " + url + "?wsdl");
    }
}

