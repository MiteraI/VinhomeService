package app.vinhomes.entity.customer;

import app.vinhomes.entity.Account;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tbl_phone"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "account")
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phone_id")
    private Long phoneId;

    @Column(nullable = false)
    private String number;

    @OneToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "account_id",
            referencedColumnName = "account_id",
            nullable = false
    )
    @JsonBackReference
    private Account account;

}
