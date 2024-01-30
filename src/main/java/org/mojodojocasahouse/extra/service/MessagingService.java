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
        switch (e.getErrorCode()) {
            case UNAVAILABLE -> log.debug("Firebase: UNAVAILABLE");
            case INTERNAL -> log.debug("Firebase: INTERNAL");
            case INVALID_ARGUMENT -> log.debug("Firebase: INVALID_ARGUMENT");
            case ABORTED -> log.debug("Firebase: ABORTED");
            case UNKNOWN -> log.debug("Firebase: UNKNOWN");
            case CONFLICT -> log.debug("Firebase: CONFLICT");
            case CANCELLED -> log.debug("Firebase: CANCELLED");
            case DATA_LOSS -> log.debug("Firebase: DATA_LOSS");
            case NOT_FOUND -> log.debug("Firebase: NOT_FOUND");
            case OUT_OF_RANGE -> log.debug("Firebase: OUT_OF_RANGE");
            case ALREADY_EXISTS -> log.debug("Firebase: ALREADY_EXISTS");
            case UNAUTHENTICATED -> log.debug("Firebase: UNAUTHENTICATED");
            case DEADLINE_EXCEEDED -> log.debug("Firebase: DEADLINE_EXCEEDED");
            case PERMISSION_DENIED -> log.debug("Firebase: PERMISSION_DENIED");
            case RESOURCE_EXHAUSTED -> log.debug("Firebase: RESOURCE_EXHAUSTED");
            case FAILED_PRECONDITION -> log.debug("Firebase: FAILED_PRECONDITION");
        }

        if(e.getMessagingErrorCode() == null) {
            return;
        }

        switch (e.getMessagingErrorCode()) {
            case INTERNAL -> log.debug("FCM: Internal Server Error");
            case UNAVAILABLE -> log.debug("FCM is temporarily unavailable");
            case UNREGISTERED -> log.debug("FCM: client token has expired");
            case QUOTA_EXCEEDED -> log.debug("FCM: limit exceeded for target");
            case INVALID_ARGUMENT -> log.debug("FCM: One or more arguments specified in the request were invalid.");
            case SENDER_ID_MISMATCH -> log.debug("FCM: The authenticated sender ID is different from the sender ID for the registration token.");
            case THIRD_PARTY_AUTH_ERROR -> log.debug("FCM: APNs certificate or web push auth key was invalid or missing.");
        }
    }

}
