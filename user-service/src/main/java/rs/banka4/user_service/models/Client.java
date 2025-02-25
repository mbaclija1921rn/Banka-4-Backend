package rs.banka4.user_service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

@Entity(name = "clients")
@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
@With
public class Client implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String gender;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private long permissionBits;

    @ElementCollection
    @CollectionTable(name = "client_account_links", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "account_link")
    private List<String> linkedAccounts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getPrivileges();
    }

    public EnumSet<Privilege> getPrivileges() {
        return EnumSet.copyOf(
                Stream.of(Privilege.values())
                        .filter(p -> (permissionBits & p.bit()) != 0)
                        .toList()
        );
    }

    public void setPrivileges(Collection<Privilege> privileges) {
        permissionBits = privileges.stream()
                .map(Privilege::bit)
                .reduce(0L, (x, y) -> x | y);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}