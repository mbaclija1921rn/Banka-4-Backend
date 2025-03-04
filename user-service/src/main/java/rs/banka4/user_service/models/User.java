package rs.banka4.user_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@MappedSuperclass
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    @Column(nullable = false)
    public String firstName;

    @Column(nullable = false)
    public String lastName;

    @Column(nullable = false)
    public LocalDate dateOfBirth;

    @Column(nullable = false)
    public String gender;

    @Email
    @Column(nullable = false, unique = true)
    public String email;

    @Column(nullable = false)
    public String phone;

    @Column(nullable = false)
    public String address;

    @Column()
    public String password;

    @Column(nullable = false)
    public boolean enabled;

    @Column(nullable = false)
    public long permissionBits;

    public EnumSet<Privilege> getPrivileges() {
        List<Privilege> privileges = Stream.of(Privilege.values())
                .filter(p -> (permissionBits & p.bit()) != 0)
                .toList();
        if (privileges.isEmpty()) {
            return EnumSet.noneOf(Privilege.class);
        }
        return EnumSet.copyOf(privileges);
    }

    public void setPrivileges(Collection<Privilege> privileges) {
        permissionBits = privileges.stream()
                .map(Privilege::bit)
                .reduce(0L, (x, y) -> x | y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}