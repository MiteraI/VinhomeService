package app.vinhomes.entity;

import app.vinhomes.entity.order.Payment;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.Service;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(
        name = "tbl_order"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    private double price;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_time")
    private Date createTime;

    @OneToOne(
            optional = false,
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "payment_id",
            referencedColumnName = "payment_id"
    )
    private Payment payment;

    @OneToOne(
            optional = false,
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "service_id",
            referencedColumnName = "service_id"
    )
    private Service service;

    @OneToOne(
            optional = false,
            cascade = CascadeType.ALL,
            mappedBy = "order"
    )
    private Schedule schedule;

    @ManyToOne(
            optional = false
    )
    @JoinColumn(
            name = "account_id",
            referencedColumnName = "account_id"
    )
    private Account account;

}
