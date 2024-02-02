package org.mojodojocasahouse.extra.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FCM_TOKEN", nullable = false, unique = true)
    private String fcmToken;

    @Column(name = "MODIFIED_TIMESTAMP", nullable = false)
    @Setter
    private Timestamp modified;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    @Setter
    private ExtraUser user;

    public UserDevice(String fcmToken, ExtraUser user) {
        this.fcmToken = fcmToken;
        this.modified = new Timestamp(System.currentTimeMillis());
        this.user = user;
    }

    public UserDevice() {}
}
