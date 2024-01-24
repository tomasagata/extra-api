package org.mojodojocasahouse.extra.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.model.Investment;
import org.mojodojocasahouse.extra.model.UserDevice;
import org.mojodojocasahouse.extra.repository.UserDeviceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingService {

    private final UserDeviceRepository deviceRepository;
    private final FirebaseMessaging fcm;


    public void sendPushNotificationToDevicesOfUser(Investment investment) {
        List<String> tokens = getFCMTokensOfUser(investment.getUser());
        MulticastMessage msg = buildInvestmentReturnMessage(tokens, investment);
        BatchResponse batchResponse;
        try {
            batchResponse = fcm.sendEachForMulticast(msg);
        } catch (FirebaseMessagingException fcmException) {
            handleFirebaseMessagingException(fcmException);
            return;
        }
        handleBatchSendResponse(batchResponse);
    }

    private List<String> getFCMTokensOfUser(ExtraUser user) {
        return deviceRepository
                .getDevicesOfUser(user).stream()
                .map(UserDevice::getFcmToken)
                .collect(Collectors.toList());
    }

    private MulticastMessage buildInvestmentReturnMessage(List<String> tokens,
                                                          Investment investment) {
        return MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle("Investment return accredited")
                        .setBody("Accredited return of $" + investment.getDepositAmount() +
                                 " from investment \"" + investment.getName() + "\"")
                        .build())
                .putData("investmentName", investment.getName())
                .putData("amount", investment.getDepositAmount().toPlainString())
                .addAllTokens(tokens)
                .build();
    }

    private void handleBatchSendResponse(BatchResponse batchResponse) {
        batchResponse
                .getResponses()
                .forEach(this::handleSendResponse);
    }

    private void handleSendResponse(SendResponse response) {
        if(!response.isSuccessful()) {
            handleFirebaseMessagingException(response.getException());
        }
    }

    private void handleFirebaseMessagingException(FirebaseMessagingException e) {
        if(e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
            // FCM client token has expired and requires a new registration on clients' behalf.
            // Expired token should be deleted from the database.
            log.debug("Client FCM token has expired");
        }
    }

}
