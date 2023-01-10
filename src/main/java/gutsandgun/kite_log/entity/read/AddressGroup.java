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
@SQLDelete(sql= "UPDATE address_group SET is_deleted=true WHERE id = ?")
@Table(name="address_group")
public class AddressGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fk_user_address_id")
    private Long userAddressId;

    @Column(name = "fk_user_group_id")
    private Long userGroupId;

    private Boolean isDeleted = false;
}
