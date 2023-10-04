package org.mojodojocasahouse.extra.tests.model;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.exception.InvalidSessionTokenException;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.testmodels.TestSessionToken;

import java.util.UUID;

class SessionTokenTest {

    @Test
    public void testValidatingValidSessionTokenThrowsNothing(){
        ExtraUser linkedUser = new ExtraUser(
                "m",
                "j",
                "mj@me.com",
                "Somepassword1!"
        );
        TestSessionToken validToken = new TestSessionToken(
                UUID.randomUUID(),
                false,
                200,
                linkedUser
        );

        Assertions.assertThatNoException().isThrownBy(validToken::validate);
    }

    @Test
    public void testValidatingExpiredSessionTokenThrowsInvalidSessionTokenException(){
        ExtraUser linkedUser = new ExtraUser(
                "m",
                "j",
                "mj@me.com",
                "Somepassword1!"
        );
        TestSessionToken validToken = new TestSessionToken(
                UUID.randomUUID(),
                false,
                -200,
                linkedUser
        );

        Assertions.assertThatThrownBy(validToken::validate).isInstanceOf(InvalidSessionTokenException.class);
    }

    @Test
    public void testValidatingRevokedSessionTokenThrowsInvalidSessionTokenException(){
        ExtraUser linkedUser = new ExtraUser(
                "m",
                "j",
                "mj@me.com",
                "Somepassword1!"
        );
        TestSessionToken validToken = new TestSessionToken(
                UUID.randomUUID(),
                true,
                200,
                linkedUser
        );

        Assertions.assertThatThrownBy(validToken::validate).isInstanceOf(InvalidSessionTokenException.class);

    }

}