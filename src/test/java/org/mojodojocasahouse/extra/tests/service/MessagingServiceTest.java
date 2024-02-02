package org.mojodojocasahouse.extra.tests.service;

import com.google.firebase.ErrorCode;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.SendResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mojodojocasahouse.extra.model.Category;
import org.mojodojocasahouse.extra.model.ExtraUser;
import org.mojodojocasahouse.extra.model.Investment;
import org.mojodojocasahouse.extra.model.UserDevice;
import org.mojodojocasahouse.extra.repository.UserDeviceRepository;
import org.mojodojocasahouse.extra.service.MessagingService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessagingServiceTest {

    @Mock
    private UserDeviceRepository deviceRepository;
    @Mock
    private FirebaseMessaging fcm;
    @InjectMocks
    private MessagingService messagingService;


    @Test
    public void testSendingPushNotificationsToDevicesOfUserIsSuccessful() throws FirebaseMessagingException {
        ExtraUser user = new ExtraUser(
                "Michael",
                "Jackson",
                "mj@me.com",
                "Somepassword1!"
        );
        List<UserDevice> devices = List.of(new UserDevice("token", user));
        Category category = new Category("test category", (short) 1, user);
        Investment investment = new Investment(
                "test investment",
                BigDecimal.TEN,
                Timestamp.valueOf("2023-10-10 00:00:00"),
                BigDecimal.ONE,
                10,
                1,
                user,
                category
        );
        BatchResponse mockBatchResponse = mock(BatchResponse.class);
        SendResponse mockResponse = mock(SendResponse.class);
        List<SendResponse> mockResponses = List.of(mockResponse);

        given(deviceRepository.getDevicesOfUser(any())).willReturn(devices);
        given(fcm.sendEachForMulticast(any())).willReturn(mockBatchResponse);
        doAnswer(invocation -> mockResponses).when(mockBatchResponse).getResponses();
        doAnswer(invocation -> true).when(mockResponse).isSuccessful();

        messagingService.sendPushNotificationToDevicesOfUser(investment);

        verify(deviceRepository).getDevicesOfUser(any());
        verify(fcm).sendEachForMulticast(any());
        verify(mockBatchResponse).getResponses();
        verify(mockResponse).isSuccessful();

    }

}
