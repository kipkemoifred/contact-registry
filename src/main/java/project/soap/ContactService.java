package project.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import project.model.User;

import java.util.List;

@WebService
public interface ContactService {
    @WebMethod
    List<User> getContactsByOrganization(String organizationName);
}

