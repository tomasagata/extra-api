package org.mojodojocasahouse.extra.testmodels;

import jakarta.persistence.*;
import lombok.Getter;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.model.SessionToken;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "SESSION_TOKENS")
@Getter
public class TestSessionToken extends SessionToken {

    public TestSessionToken(UUID id, Boolean revoked, Integer secondsToExpire, ExtraUser linkedUser){
        this.id = id;

        // Set expiration timestamp = now + secondsToExpire in the future. secondsToExpire may be negative.
        this.expirationTimestamp = Timestamp.from(
                new Timestamp(System.currentTimeMillis())
                        .toInstant()
                        .plus(secondsToExpire, ChronoUnit.SECONDS)
        );

        // grant timestamp should always be set to 20 minutes earlier than
        // expiration timestamp for consistency with actual SessionToken
        this.grantTimestamp = Timestamp.from(
                expirationTimestamp
                        .toInstant()
                        .minus(20, ChronoUnit.MINUTES)
        );

        this.revoked = revoked;
        this.linkedUser = linkedUser;
    }

    public TestSessionToken() {

    }
}
