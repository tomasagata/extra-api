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
            log.debug("Sending " + tokens.size() + " messages to user devices.");
            batchResponse = fcm.sendEachForMulticast(msg);
        } catch (FirebaseMessagingException fcmException) {
            log.debug("Send operation failed.");
            handleFirebaseMessagingException(fcmException);
            return;
        }
        log.debug("Send operation success.");
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
        log.debug("Firebase: " + e.getErrorCode().name());

        if(e.getMessagingErrorCode() == null) {
            return;
        }

        log.debug("FCM: " + e.getMessagingErrorCode().name());
    }

}
