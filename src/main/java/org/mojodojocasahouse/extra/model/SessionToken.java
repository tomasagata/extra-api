package org.mojodojocasahouse.extra.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.mojodojocasahouse.extra.exception.InvalidSessionTokenException;
import org.mojodojocasahouse.extra.exception.SessionAlreadyRevokedException;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "SESSION_TOKENS")
public class SessionToken {

    @Id
    @Getter
    protected UUID id;

    @Column(name = "GRANT_TIMESTAMP", nullable = false)
    protected Timestamp grantTimestamp;

    @Column(name = "EXPIRATION_TIMESTAMP", nullable = false)
    protected Timestamp expirationTimestamp;

    @Column(name = "REVOKED", nullable = false)
    protected Boolean revoked;

    @ManyToOne
    @JoinColumn(name = "LINKED_USER_ID", nullable = false)
    @Getter
    protected ExtraUser linkedUser;

    public SessionToken() {}

    public SessionToken(ExtraUser linkedUser){
        this.id = UUID.randomUUID();
        this.grantTimestamp = new Timestamp(System.currentTimeMillis());
        this.expirationTimestamp = Timestamp.from(grantTimestamp.toInstant().plus(20, ChronoUnit.MINUTES));
        this.revoked = false;
        this.linkedUser = linkedUser;
    }

    public void validate() throws InvalidSessionTokenException{
        if(id == null || expirationTimestamp.before(new Timestamp(System.currentTimeMillis())) || revoked){
            throw new InvalidSessionTokenException();
        }
    }

    public void revoke() throws SessionAlreadyRevokedException {
        if(this.revoked){
            throw new SessionAlreadyRevokedException();
        }
        this.revoked = true;
    }

}
