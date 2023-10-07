package org.mojodojocasahouse.extra.tests.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mojodojocasahouse.extra.model.Authority;
import org.mojodojocasahouse.extra.model.ExtraUser;

import java.util.HashSet;
import java.util.Set;

public class AuthorityTest {

    @Test
    public void testGettingAuthorityId(){
        Authority auth = new Authority("ROLE_USER");

        Assertions.assertThat(auth.getId()).isNull();
    }

    @Test
    public void testGettingAuthorityRole(){
        Authority auth = new Authority("ROLE_USER");

        Assertions.assertThat(auth.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    public void testGettingAuthorityUsers(){
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "a_hashed_password"
        );
        Authority auth = new Authority("ROLE_USER", Set.of(user));

        Assertions.assertThat(auth.getUsers()).containsExactlyInAnyOrder(user);
    }

}
