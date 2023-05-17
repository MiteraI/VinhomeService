package app.vinhomes.entity.order;

import app.vinhomes.entity.ServiceCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private double price;

    @Column(name = "pnum")
    private int numOfPeople;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "servicecate_id",
            referencedColumnName = "servicecate_id"
    )
    private ServiceCategory serviceCategory;
}
