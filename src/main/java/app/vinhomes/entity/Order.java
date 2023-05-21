package app.vinhomes.entity;

import app.vinhomes.entity.order.Payment;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.Service;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tbl_order"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(nullable = false)
    private double price;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_time")
    private LocalDateTime createTime;

    @ManyToOne(
            cascade = CascadeType.MERGE,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "payment_id",
            referencedColumnName = "payment_id",
            nullable = false
    )
    private Payment payment;

    @ManyToOne(
            cascade = CascadeType.MERGE,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "service_id",
            referencedColumnName = "service_id",
            nullable = false
    )
    private Service service;

    @OneToOne(
            optional = false,
            cascade = CascadeType.ALL,
            mappedBy = "order",
            fetch = FetchType.EAGER
    )
    @JsonManagedReference
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(
            name = "account_id",
            referencedColumnName = "account_id",
            nullable = false
    )
    @JsonBackReference
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "accountId")
    @JsonIdentityReference(alwaysAsId = true)
    private Account account;

}
