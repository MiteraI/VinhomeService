package app.vinhomes.entity.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


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

    @Column(
        name = "description",
        length = 1024
    )
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

    @Column(name = "url_image", nullable = true)
    private String urlImage;

    @Column(name = "status", columnDefinition = "integer default 1")
    private int status;

}
