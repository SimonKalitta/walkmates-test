package com.walkmates.lab1;

import com.walkmates.model.Seeker;
import com.walkmates.model.TrustTier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Lab 1, Part B — specification-based tests for {@link Seeker}.
 *
 * <p>Design your tests on paper first (equivalence partitions, boundary values, decision table)
 * from {@code docs/REQUIREMENTS.md} FR-1.1 / FR-1.3 / FR-1.2, then implement them here. One
 * worked example is provided; the {@code TODO}s are yours.</p>
 */
class SeekerSpecBasedTest {

    // ---- Worked example: boundary value at the maximum single top-up (FR-1.3) ----
    @Test
    @DisplayName("Top-up exactly at the 5000 SEK single-transaction maximum is accepted")
    void topUpAtSingleMaximumIsAccepted() {
        Seeker seeker = new Seeker("sam@example.com", "Sam", "0707654321");

        seeker.addFunds(Seeker.MAX_SINGLE_TOP_UP); // 5000.00, the boundary value

        assertThat(seeker.getBalance()).isEqualTo(Seeker.MAX_SINGLE_TOP_UP);
    }

    // TODO (EP): one valid + one invalid equivalence class for email, name, and phone (FR-1.1).
    // TODO (BVA): just-below / at / just-above the 10.00 minimum top-up (FR-1.3).
    // TODO (BVA): a top-up that would push the balance above 20000.00 is rejected (FR-1.3).
    // TODO (Decision table): expected fee + max-bookings for each trust tier (FR-1.2).

    @Test
    @DisplayName("More than one @ in email is rejected at registration")
    void invalidAtInEmailIsRejectedAtRegistration() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("example@example@example.domain.com", "Sam", "0707654321"));
    }

    @Test
    @DisplayName("Correct local part in email is accepted at registration")
    void validLocalPartIsAcceptedAtRegistration() {
        assertDoesNotThrow(() -> new Seeker("example@domain.com", "Sam", "0707654321"));
    }

    @Test
    @DisplayName("Correct dot in email is accepted at registration")
    void validDotInEmailIsAcceptedAtRegistration() {
        assertDoesNotThrow(() -> new Seeker("example@domain.com", "Sam", "0707654321"));
    }

    @Test
    @DisplayName("Incorrect email length in email is rejected at registration")
    void invalidEmailLengthIsRejectedAtRegistration() {
        String longEmail = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz012345678912@abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz0123456789.abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz0123456789.abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz0123456789.com";
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker(longEmail, "Sam", "0707654321"));
    }

    @Test
    @DisplayName("Incorrect display name length is rejected at registration")
    void invalidDisplayNameLengthAtRegistration() {
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("example@domain.com", "Alexander_The_Great_ conqueror_Of_Worlds89", "0707654321"));
    }

    @Test
    @DisplayName("Special characters in display name is rejected at registration")
    void specialCharactersInDisplayNameIsRejectedAtRegistration() {
        String bestName = "Sergio <3";
        assertThrows(IllegalArgumentException.class,
                () -> new Seeker("example@domain.com", bestName, "0707654321"));
    }

    @Test
    @DisplayName("Correct 07 phone number length is accepted at registration")
    void valid07PhoneNumberLengthIsAcceptedAtRegistration() {
        assertDoesNotThrow(() -> new Seeker("example@domain.com", "Sam", "0712345678"));
    }

    @Test
    @DisplayName("Correct international phone number length is accepted at registration")
    void validInternationalPhoneNumberLengthIsAcceptedAtRegistration() {
        assertDoesNotThrow(() -> new Seeker("example@domain.com", "Sam", "+46723456789"));
    }

    @Test
    @DisplayName("No special characters in 07 phone number is accepted at registration")
    void noSpecialCharactersIn07PhoneNumberIsAcceptedAtRegistration() {
        assertDoesNotThrow(() -> new Seeker("example@domain.com", "Sam", "0712345678"));
    }

    @Test
    @DisplayName("No special characters in international phone number is accepted at registration")
    void noSpecialCharactersInInternationalPhoneNumberIsAcceptedAtRegistration() {
        assertDoesNotThrow(() -> new Seeker("example@domain.com", "Sam", "+46723456789"));
    }

    @Test
    @DisplayName("Invalid top-up amount is rejected")
    void invalidTopUpAmountIsRejected() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");
        assertThrows(IllegalArgumentException.class,
                () -> seeker.addFunds(5000.01));
    }

    @Test
    @DisplayName("Invalid resulting balance amount is rejected")
    void invalidResultingBalanceAmountIsRejected() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");
        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        seeker.addFunds(2000.00);
        assertThrows(IllegalArgumentException.class,
                () -> seeker.addFunds(4000.00));
    }

    // ========== BVA ==========

    @Test
    @DisplayName("Just below lower top-up boundary reject")
    void justBelowTopUpBoundaryReject() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");
        assertThrows(IllegalArgumentException.class, () -> seeker.addFunds(9.99));
    }

    @Test
    @DisplayName("Lower top-up boundary accept")
    void lowerTopUpBoundaryAccept() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");
        assertDoesNotThrow(() -> seeker.addFunds(10.00));
    }

    @Test
    @DisplayName("Just above lower top-up boundary accept")
    void justAboveLowerTopUpBoundaryAccept() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");
        assertDoesNotThrow(() -> seeker.addFunds(10.01));
    }

    @Test
    @DisplayName("Just below upper top-up boundary accept")
    void justBelowUpperTopUpBoundaryAccept() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");
        assertDoesNotThrow(() -> seeker.addFunds(4999.99));
    }

    @Test
    @DisplayName("Upper top-up boundary accept")
    void upperTopUpBoundaryAccept() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");
        assertDoesNotThrow(() -> seeker.addFunds(5000.00));
    }

    @Test
    @DisplayName("Just above upper top-up boundary reject")
    void justAboveUpperTopUpBoundaryReject() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");
        assertThrows(IllegalArgumentException.class, () -> seeker.addFunds(5000.01));
    }

    @Test
    @DisplayName("Just below maximum balance accept")
    void justBelowMaximumBalanceAccept() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");
        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        assertDoesNotThrow(() -> seeker.addFunds(4999.99));
    }

    @Test
    @DisplayName("Maximum balance accept")
    void maximumBalanceAccept() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");
        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        assertDoesNotThrow(() -> seeker.addFunds(5000.00));
    }

    @Test
    @DisplayName("Just above maximum balance reject")
    void justAboveMaximumBalanceReject() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");
        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        seeker.addFunds(5000.00);
        seeker.addFunds(4999.99);
        assertThrows(IllegalArgumentException.class, () -> seeker.addFunds(0000.02));
    }

    // ========== Decision table ==========

    @Test
    @DisplayName("NEW tier: max 1 concurrent booking, 15% platform fee")
    void newTierLimits() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");

        assertThat(seeker.getTrustTier()).isEqualTo(TrustTier.NEW);
        assertThat(seeker.getMaxConcurrentBookings()).isEqualTo(1);
        assertThat(seeker.getTrustTier().getPlatformFee()).isEqualTo(0.15);
    }

    @Test
    @DisplayName("VERIFIED tier: max 3 concurrent bookings, 12% platform fee")
    void verifiedTierLimits() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");

        seeker.setTrustTier(TrustTier.VERIFIED);
        assertThat(seeker.getMaxConcurrentBookings()).isEqualTo(3);
        assertThat(seeker.getTrustTier().getPlatformFee()).isEqualTo(0.12);
    }

    @Test
    @DisplayName("TRUSTED tier: max 5 concurrent bookings, 8% platform fee")
    void trustedTierLimits() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");

        seeker.setTrustTier(TrustTier.TRUSTED);
        assertThat(seeker.getMaxConcurrentBookings()).isEqualTo(5);
        assertThat(seeker.getTrustTier().getPlatformFee()).isEqualTo(0.08);
    }

    @Test
    @DisplayName("PRO_SITTER tier: max 10 concurrent bookings, 5% platform fee")
    void proSitterTierLimits() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");

        seeker.setTrustTier(TrustTier.PRO_SITTER);
        assertThat(seeker.getMaxConcurrentBookings()).isEqualTo(10);
        assertThat(seeker.getTrustTier().getPlatformFee()).isEqualTo(0.05);
    }

    @Test
    @DisplayName("Test seeker email getter")
    void testSeekerEmailGetter() {
        String email = "example@mail.com";
        Seeker seeker = new Seeker(email, "Sam", "0712345678");

        assertEquals(email, seeker.getEmail());
    }

    @Test
    @DisplayName("Seeker ID getter returns ID")
    void seekerIDGetter() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");

        String id = seeker.getId();
        assertFalse(id.isEmpty());
    }

    @Test
    @DisplayName("Seeker display name getter returns correct name")
    void seekerDisplayNameGetter() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");

        String displayName = seeker.getDisplayName();
        assertEquals("Sam",  displayName);
    }

    @Test
    @DisplayName("Seeker phone number getter returns correct number")
    void seekerPhoneNumberGetter() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");

        String phoneNumber = seeker.getPhoneNumber();
        assertEquals("0712345678",  phoneNumber);
    }

    @Test
    @DisplayName("Set trust tier not null")
    void setTrustTierNull() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");

        assertThrows(IllegalArgumentException.class, () -> seeker.setTrustTier(null));
    }

    @Test
    @DisplayName("Charge negative")
    void chargeNegative() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");

        assertThrows(IllegalArgumentException.class, () -> seeker.charge(-1));
    }

    @Test
    @DisplayName("Charge more than balance in wallet")
    void chargeMoreThanBalanceInWallet() {
        Seeker seeker = new Seeker("example@domain.com", "Sam", "0712345678");

        assertThrows(IllegalArgumentException.class, () -> seeker.charge(10));
    }
}
