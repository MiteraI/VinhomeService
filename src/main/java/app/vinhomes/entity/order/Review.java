package app.vinhomes.entity.order;

import app.vinhomes.entity.Account;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Entity
@Table(
        name = "tbl_review"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    //The constraint for rating must be check through application and service layer
    private int rating;

    private String comment;

    @ManyToOne(
    )
    @JoinColumn(
            name = "service_id",
            referencedColumnName = "service_id",
            nullable = false
    )
    private Service service;

    @ManyToOne(
    )
    @JoinColumn(
            name = "account_id",
            referencedColumnName = "account_id",
            nullable = false
    )
    @JsonIgnoreProperties({"email", "address", "phone", "password", "accountName"})
    private Account account;
}
