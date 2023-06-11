package app.vinhomes.entity;

import app.vinhomes.entity.customer.Address;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.entity.worker.WorkerStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(
        name = "tbl_account",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "email_unique_constraint",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "account_name_constraint",
                        columnNames = "account_name"
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(name = "pwd", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Basic
    @Temporal(TemporalType.DATE)
    private LocalDate dob; //date of birth

    private String cookie; //Only used for cookie authorization

    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "integer default 1"
    )
    private int accountStatus;

    @Column(nullable = false)
    private int role;

    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "address_id",
            referencedColumnName = "address_id"
    )

    private Address address;

    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @JsonIgnore
    @JsonManagedReference
    private List<Phone> phones;
    public void addPhone(Phone phone) {
        if(phones == null) {
            phones = new ArrayList<>();
            phones.add(phone);
        } else phones.add(phone);
    }

    //Optional attribute for cascade delete only
    @OneToOne(
            mappedBy = "account",
            cascade = CascadeType.ALL
    )
    @ToString.Exclude
    @JsonIgnore
    @JsonManagedReference
    private WorkerStatus workerStatus;

    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL
    )
    @ToString.Exclude
    @JsonIgnore
    @JsonManagedReference
    private List<Order> orders;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(Integer.toString(role)));
    }

    @Override
    public String getUsername() {
        return accountName;
    }
    @Override
    public String getPassword(){
        return password;}


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
