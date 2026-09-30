package com.walkmates.lab2;

import com.walkmates.model.Booking;
import com.walkmates.model.Listing;
import com.walkmates.model.ListingType;
import com.walkmates.model.Seeker;
import com.walkmates.model.TrustTier;
import com.walkmates.model.Provider;
import com.walkmates.model.ListingStatus;
import com.walkmates.model.BookingStatus;
import com.walkmates.repository.BookingRepository;
import com.walkmates.repository.ListingRepository;
import com.walkmates.repository.ProviderRepository;
import com.walkmates.repository.SeekerRepository;
import com.walkmates.service.BookingService;
import com.walkmates.service.NotificationService;
import com.walkmates.service.PricingCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock SeekerRepository seekers;
    @Mock ListingRepository listings;
    @Mock ProviderRepository providers;
    @Mock BookingRepository bookings;
    @Mock NotificationService notifications;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(seekers, listings, providers, bookings, new PricingCalculator(), notifications);
    }

    private Seeker seeker(TrustTier tier) {
        Seeker seeker = new Seeker("p@example.com", "Pat", "0701112233");
        seeker.setTrustTier(tier);
        return seeker;
    }

    private Listing listing(ListingType type) {
        return new Listing("provider-1", "A listing", "desc", type);
    }

    @Test
    @DisplayName("Rejects booking when seeker is exactly at tier max bookings (FR-4.4 rule 2)")
    void rejectBookingWhenAtMaxBookings() {
        Seeker seeker = seeker(TrustTier.NEW);
        Listing listing = listing(ListingType.DOG_WALK);
        Booking existingBooking = new Booking(seeker.getId(), "Existing booking", 60);

        when(seekers.findById(seeker.getId())).thenReturn(Optional.of(seeker));
        when(listings.findById(listing.getId())).thenReturn(Optional.of(listing));

        when(bookings.findBySeekerId(seeker.getId())).thenReturn(List.of(existingBooking));

        assertThatThrownBy(() -> bookingService.createBooking(seeker.getId(), listing.getId(), 60))
                .isInstanceOf(BookingService.BookingRejectedException.class)
                .hasMessage("Seeker booking limit reached for tier " + seeker.getTrustTier());
    }

    @Test
    @DisplayName("Check for unknown seeker")
    void checkUnknownSeeker() {
        Listing listing = listing(ListingType.DOG_WALK);

        assertThrows(BookingService.BookingRejectedException.class, () -> bookingService.createBooking(null, listing.getId(), 60));
    }

    @Test
    @DisplayName("Check for unknown listing")
    void checkUnknownListing() {
        Seeker seeker = seeker(TrustTier.NEW);

        when(seekers.findById(seeker.getId())).thenReturn(Optional.of(seeker));
        assertThrows(BookingService.BookingRejectedException.class, () -> bookingService.createBooking(seeker.getId(), null, 60));
    }

    @Test
    @DisplayName("Check for unknown provider")
    void checkUnknownProvider() {
        Seeker seeker = seeker(TrustTier.NEW);
        Listing listing = listing(ListingType.DOG_WALK);

        when(seekers.findById(seeker.getId())).thenReturn(Optional.of(seeker));
        when(listings.findById(listing.getId())).thenReturn(Optional.of(listing));
        assertThatThrownBy(() -> bookingService.createBooking(seeker.getId(), listing.getId(), 60))
                .isInstanceOf(BookingService.BookingRejectedException.class)
                .hasMessage("Unknown provider for listing");
    }

    @Test
    @DisplayName("Check provider capacity")
    void checkProviderCapacity() {
        Seeker seeker = seeker(TrustTier.NEW);
        Listing listing = listing(ListingType.DOG_WALK);
        Provider provider = new Provider("provider", 62.39, 17.31, 1);
        Booking existingBooking = new Booking("other-seeker", listing.getId(), 60);

        when(seekers.findById(seeker.getId())).thenReturn(Optional.of(seeker));
        when(listings.findById(listing.getId())).thenReturn(Optional.of(listing));
        when(providers.findById(listing.getProviderId())).thenReturn(Optional.of(provider));
        when(listings.findByProviderId(listing.getProviderId())).thenReturn(List.of(listing));
        when(bookings.findByListingId(listing.getId())).thenReturn(List.of(existingBooking));

        assertThatThrownBy(() -> bookingService.createBooking(seeker.getId(), listing.getId(), 60))
                .isInstanceOf(BookingService.BookingRejectedException.class)
                .hasMessageContaining("Provider is at capacity");
    }

    @Test
    @DisplayName("Check listing availability")
    void checkListingAvailability() {
        Seeker seeker = seeker(TrustTier.NEW);
        Listing listing = listing(ListingType.DOG_WALK);
        listing.transitionTo(ListingStatus.BOOKED);

        when(seekers.findById(seeker.getId())).thenReturn(Optional.of(seeker));
        when(listings.findById(listing.getId())).thenReturn(Optional.of(listing));

        assertThatThrownBy(() -> bookingService.createBooking(seeker.getId(), listing.getId(), 60))
                .isInstanceOf(BookingService.BookingRejectedException.class)
                .hasMessage("Listing is not available");
    }

    @Test
    @DisplayName("Check insufficient seeker balance")
    void checkInsufficientSeekerBalance() {
        Seeker seeker = seeker(TrustTier.NEW);
        Listing listing = listing(ListingType.DOG_WALK);
        Provider provider = new Provider("provider", 62.39, 17.31, 1);
        seeker.addFunds(91.00);

        when(seekers.findById(seeker.getId())).thenReturn(Optional.of(seeker));
        when(listings.findById(listing.getId())).thenReturn(Optional.of(listing));
        when(providers.findById(listing.getProviderId())).thenReturn(Optional.of(provider));

        assertThatThrownBy(() -> bookingService.createBooking(seeker.getId(), listing.getId(), 60))
                .isInstanceOf(BookingService.BookingRejectedException.class)
                .hasMessage("Insufficient balance for booking");
    }

    @Test
    @DisplayName("Check sufficient seeker balance")
    void checkSufficientSeekerBalance() {
        Seeker seeker = seeker(TrustTier.NEW);
        Listing listing = listing(ListingType.DOG_WALK);
        Provider provider = new Provider("provider", 62.39, 17.31, 1);
        seeker.addFunds(92.00);

        when(seekers.findById(seeker.getId())).thenReturn(Optional.of(seeker));
        when(listings.findById(listing.getId())).thenReturn(Optional.of(listing));
        when(providers.findById(listing.getProviderId())).thenReturn(Optional.of(provider));

        Booking result = bookingService.createBooking(seeker.getId(), listing.getId(), 60);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(result.getPrice()).isEqualTo(92.00);
        assertThat(seeker.getBalance()).isEqualTo(0.00);
        assertThat(listing.getStatus()).isEqualTo(ListingStatus.BOOKED);
        verify(notifications).sendBookingConfirmed(seeker, result);
    }
}
