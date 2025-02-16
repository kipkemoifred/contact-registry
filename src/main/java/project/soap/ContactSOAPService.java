package project.soap;

import project.UserDAO;
import project.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
//@WebServlet("/ContactSOAPService")
public class ContactSOAPService extends HttpServlet {
    private UserDAO contactDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);

        // Extract organization name from SOAP request
        String organizationName = extractOrganizationName(requestBody);

        if (organizationName == null || organizationName.isEmpty()) {
            sendSOAPError(out, "Organization name is required.");
            return;
        }

        // Fetch contacts
        List<User> contacts = contactDAO.getContactsByOrganization(organizationName);
        System.out.println("contacts "+contacts);

        // Generate SOAP response
        out.println(generateSOAPResponse(contacts));
    }

    private String extractOrganizationName(String soapRequest) {
        int start = soapRequest.indexOf("<organizationName>") + "<organizationName>".length();
        int end = soapRequest.indexOf("</organizationName>");
        return (start != -1 && end != -1) ? soapRequest.substring(start, end) : null;
    }

    private void sendSOAPError(PrintWriter out, String message) {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        out.println("  <soap:Body>");
        out.println("    <soap:Fault>");
        out.println("      <faultcode>SOAP-ENV:Client</faultcode>");
        out.println("      <faultstring>" + message + "</faultstring>");
        out.println("    </soap:Fault>");
        out.println("  </soap:Body>");
        out.println("</soap:Envelope>");
    }

    private String generateSOAPResponse(List<User> contacts) {
        StringBuilder response = new StringBuilder();
        response.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        response.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        response.append("  <soap:Body>");
        response.append("    <getContactsResponse>");

        for (User contact : contacts) {
            response.append("      <contact>");
            response.append("        <fullName>").append(contact.getName()).append("</fullName>");
            response.append("        <phoneNumber>").append(contact.getPhoneNumber()).append("</phoneNumber>");
            response.append("        <email>").append(contact.getEmail()).append("</email>");
            response.append("        <organization>").append(contact.getOrganization()).append("</organization>");
            response.append("      </contact>");
        }

        response.append("    </getContactsResponse>");
        response.append("  </soap:Body>");
        response.append("</soap:Envelope>");

        return response.toString();
    }
}

