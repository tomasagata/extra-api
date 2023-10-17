package org.mojodojocasahouse.extra.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.mojodojocasahouse.extra.exception.InvalidPasswordResetTokenException;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "PASSWORD_RESET_TOKENS")
@Getter
public class PasswordResetToken {

    @Id
    protected UUID id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    protected ExtraUser user;

    @Column(name = "VALID_UNTIL")
    protected Timestamp validUntil;

    public PasswordResetToken(ExtraUser user){
        this.id = UUID.randomUUID();
        this.user = user;
        this.validUntil = Timestamp.from(
                new Timestamp(System.currentTimeMillis())
                        .toInstant()
                        .plus(15, ChronoUnit.MINUTES)
        );
    }

    public PasswordResetToken(){}

    public void assertValid() throws InvalidPasswordResetTokenException{
        if( this.id == null || new Timestamp(System.currentTimeMillis()).after(validUntil)){
            throw new InvalidPasswordResetTokenException();
        }
    }
}
