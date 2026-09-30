package com.walkmates.lab2;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.walkmates.model.Seeker;
import com.walkmates.repository.SeekerRepository;
import com.walkmates.service.NotificationService;
import com.walkmates.service.PaymentService;
import com.walkmates.service.SeekerService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SeekerServiceTest {
    @Mock PaymentService paymentService;
    @Mock SeekerRepository seekerRepository;
    @Mock NotificationService notificationService;

    private SeekerService seekerService;
    private final String email = "p@example.com";
    private final String name = "Pat";
    private final String phoneNumber = "0701112233";
    private final String expectedErrorMessage = "Email already registered";

    @BeforeEach
    public void setUp() {
        seekerService = new SeekerService(seekerRepository, paymentService, notificationService);
    }

    @Test
    @DisplayName("Test seeker service register returns seeker")
    void testSeekerServiceRegisterReturnsSeeker() {
        when(seekerRepository.save(any(Seeker.class))).thenReturn(new Seeker(email, name, phoneNumber));
        when(seekerRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        Seeker seeker = seekerService.register(email, name, phoneNumber);

        assertInstanceOf(Seeker.class, seeker);
    }

    @Test
    @DisplayName("Test seeker service register throws duplication error")
    void testSeekerServiceRegisterThrowsDuplicationError() {
        when(seekerRepository.findByEmail(any(String.class))).thenReturn(Optional.of(new Seeker(email, name, phoneNumber)));

        assertThatThrownBy(() -> seekerService.register(email, name, phoneNumber))
                .isInstanceOf(IllegalArgumentException.class).hasMessage(expectedErrorMessage);
    }

    @Test
    @DisplayName("Test seeker service top up rejects unknown seeker")
    void testSeekerServiceTopUpRejectsUnknownSeeker() {
        String seekerId = "123";
        String paymentMethod = "";
        double amount = 0d;
        String expectedErrorMessage = "Unknown seeker: " + seekerId;

        when(seekerRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seekerService.topUp(seekerId, paymentMethod, amount))
                .isInstanceOf(IllegalArgumentException.class).hasMessage(expectedErrorMessage);
    }

    @Test
    @DisplayName("Test seeker service top up exception blocks below code")
    void testSeekerServiceTopUpExceptionBlocksBelowCode() throws PaymentService.PaymentException {
        Seeker seeker = new Seeker(email, name, phoneNumber);
        Seeker spySeeker = spy(seeker);
        String seekerId = seeker.getId();
        String paymentMethod = "";
        double amount = 10d;

        when(seekerRepository.findById(any(String.class))).thenReturn(Optional.of(spySeeker));
        when(paymentService.charge(any(String.class), any(String.class), any(Double.class))).thenThrow(PaymentService.PaymentTimeoutException.class);

        assertThatThrownBy(() -> seekerService.topUp(seekerId, paymentMethod, amount)).isInstanceOf(PaymentService.PaymentTimeoutException.class);

        verify(spySeeker, never()).addFunds(amount);
    }

    @Test
    @DisplayName("Test seeker service top up calls add funds if charge is ok")
    void testSeekerServiceTopUpCallsAddFundsIfChargeIsOk() throws PaymentService.PaymentException {
        Seeker seeker = new Seeker(email, name, phoneNumber);
        Seeker spySeeker = spy(seeker);
        String seekerId = seeker.getId();
        String paymentMethod = "";
        double amount = 10d;

        when(seekerRepository.findById(any(String.class))).thenReturn(Optional.of(spySeeker));

        assertDoesNotThrow(() -> seekerService.topUp(seekerId, paymentMethod, amount));

        verify(spySeeker, times(1)).addFunds(amount);
    }

    @Test
    @DisplayName("Test seeker service top up returns seeker")
    void testSeekerServiceTopUpReturnsSeeker() throws PaymentService.PaymentException {
        String paymentMethod = "";
        Seeker seeker = new Seeker(email, name, phoneNumber);

        when(seekerRepository.findById(any(String.class))).thenReturn(Optional.of(seeker));
        when(seekerRepository.save(any(Seeker.class))).thenReturn(seeker);

        Seeker result = seekerService.topUp(seeker.getId(), paymentMethod, 10d);

        assertInstanceOf(Seeker.class, result);
    }
}
