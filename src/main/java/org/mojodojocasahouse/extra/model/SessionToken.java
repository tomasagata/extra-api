package org.mojodojocasahouse.extra.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.mojodojocasahouse.extra.exception.InvalidSessionTokenException;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "SESSION_TOKENS")
@Getter
public class SessionToken {

    @Id
    private UUID id;

    @Column(name = "GRANT_TIMESTAMP", nullable = false)
    private Timestamp grantTimestamp;

    @Column(name = "EXPIRATION_TIMESTAMP", nullable = false)
    private Timestamp expirationTimestamp;

    @ManyToOne
    @JoinColumn(name = "LINKED_USER_ID", nullable = false)
    private ExtraUser linkedUser;

    public SessionToken() {}

    public SessionToken(ExtraUser linkedUser){
        this.id = UUID.randomUUID();
        this.grantTimestamp = new Timestamp(System.currentTimeMillis());
        this.expirationTimestamp = Timestamp.from(grantTimestamp.toInstant().plus(24, ChronoUnit.HOURS));
        this.linkedUser = linkedUser;
    }

    public SessionToken(UUID id, ExtraUser linkedUser){
        this.id = id;
        this.grantTimestamp = new Timestamp(System.currentTimeMillis());
        this.expirationTimestamp = Timestamp.from(grantTimestamp.toInstant().plus(24, ChronoUnit.HOURS));
        this.linkedUser = linkedUser;
    }

    public void validate() throws InvalidSessionTokenException{
        if(this.id == null || expirationTimestamp.before(new Timestamp(System.currentTimeMillis()))){
            throw new InvalidSessionTokenException();
        }
    }

}
