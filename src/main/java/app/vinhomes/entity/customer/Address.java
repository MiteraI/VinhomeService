package app.vinhomes.entity.customer;

import app.vinhomes.entity.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tbl_address"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    private Long address_id;

    @Column(name = "block")
    private String buildingBlock;

    @Column(name = "room")
    private String buildingRoom;
    @OneToOne(
            cascade = CascadeType.MERGE
    )
    @JoinColumn(
            name="address_id"
    )
    @MapsId
    @ToString.Exclude
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Account account;
}

