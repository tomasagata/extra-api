package org.mojodojocasahouse.extra.repository;

import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.model.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    @Query("SELECT d FROM UserDevice d WHERE d.user = :user")
    List<UserDevice> getDevicesOfUser(ExtraUser user);

    Optional<UserDevice> findByFcmToken(String token);

}
