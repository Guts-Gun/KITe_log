package gutsandgun.kite_log.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {
    @Comment("생성일자")
    @Column(name = "reg_dt")
    private String regDt;

    @Comment("수정일자")
    @Column(name = "mod_dt")
    private String modDt;

    @PrePersist
    public void onPrePersist() {
        this.regDt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        this.modDt = this.regDt;
    }

    @PreUpdate
    public void onPreUpdate() {
        this.modDt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
    }
    public LocalDateTime getRegDt() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        return LocalDateTime.parse(regDt, format);
    }

    public LocalDateTime getModDt() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        return LocalDateTime.parse(modDt, format);
    }
}
