package tn.esprit.spring.event.demo.Auth;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.spring.event.demo.Model.Address;
import tn.esprit.spring.event.demo.Model.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private Address address;          // ✅ remplacer String par Address
    private String email;
    private String password;
    private Date datebirth = new Date();
    private List<String> phones;      // ✅ correspond au User entity
    private Role role;
    private String description;       // facultatif
}
