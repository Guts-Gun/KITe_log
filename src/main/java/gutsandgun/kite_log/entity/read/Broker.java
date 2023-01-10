package gutsandgun.kite_log.entity.read;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql= "UPDATE broker SET is_deleted=true WHERE id = ?")
@Table(name="broker")
public class Broker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;

    private String ip;

    private Float price;

    private Float speed;

    private Float failureRate;

    private Boolean isDeleted = false;
}
