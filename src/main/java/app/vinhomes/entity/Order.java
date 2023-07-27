package app.vinhomes.entity;

import app.vinhomes.entity.order.Payment;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.Service;
import app.vinhomes.entity.type_enum.OrderStatus;
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    //private int status; //0 - pending, 1 - cancel, 2 - complete

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

    @Column(columnDefinition = "integer default 0")
    private int rating;

    @Column(
            length = 1024
    )
    private String comment;

    @ManyToOne
    @JoinColumn(
            name = "account_id",
            referencedColumnName = "account_id",
            nullable = false
    )
    @JsonIgnoreProperties(
            {
                    "email", "password",
                    "accountName", "cookie", "dob", "accountStatus",
                    "role"
            }
    )
    private Account account;


    //@Column(nullable = false)
    private String phoneNumber;

    @Column(name = "url_image_confirm")
    private String urlImageConfirm;
}
