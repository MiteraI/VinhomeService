package app.vinhomes.entity.order;

import app.vinhomes.entity.Order;
import app.vinhomes.entity.ServiceCategory;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "tbl_service"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "service_name")
    private String serviceName;

    @Column(nullable = false)
    private double price;

    @Column(name = "pnum", nullable = false)
    private int numOfPeople;

    @Column(name = "description")
    private String description;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "servicecate_id",
            referencedColumnName = "servicecate_id",
            nullable = false
    )
    @ToString.Exclude
    @JsonBackReference
    private ServiceCategory serviceCategory;

}
