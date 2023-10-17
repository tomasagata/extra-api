package org.mojodojocasahouse.extra.testmodels;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.model.PasswordResetToken;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "PASSWORD_RESET_TOKENS")
@Getter
public class TestPasswordResetToken extends PasswordResetToken {
    public TestPasswordResetToken(UUID id, ExtraUser user, Integer timeToLiveSeconds){
        this.id = id;
        this.user = user;
        this.validUntil = Timestamp.from(
                new Timestamp(System.currentTimeMillis())
                        .toInstant()
                        .plusSeconds(timeToLiveSeconds)
        );
    }

    public TestPasswordResetToken(){}
}
