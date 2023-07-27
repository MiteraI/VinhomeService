package app.vinhomes.entity.order;

import app.vinhomes.entity.order.Service;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "tbl_service_category"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "servicecate_id")
    private Long serviceCategoryId;

    @Column(name = "servicecate_name", nullable = false)
    private String serviceCategoryName;

    @Column(
            name = "description",
            length = 1024
    )
    private String description;

    @OneToMany(
            mappedBy = "serviceCategory",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @JsonManagedReference
    @Column(nullable = false)
    private List<Service> services;
    public void addService(Service service) {
        if(services == null) {
            services = new ArrayList<>();
            services.add(service);
        } else services.add(service);
    }
    @Column(name = "url_image")
    private String urlImage;
}
