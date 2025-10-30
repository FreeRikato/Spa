package com.example.spas.config;

import com.example.spas.model.*;
import com.example.spas.model.enums.*;
import com.example.spas.repository.*;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1) // Ensure this runs after Flyway migrations
@Profile({ "dev" }) // Only runs in development profile
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(
        DataInitializer.class
    );

    // Repositories - Auto-injected by Spring
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final SpaRepository spaRepository;
    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    // Configuration properties for seeding control
    @Value("${app.seeding.enabled:true}")
    private boolean seedingEnabled;

    @Value("${app.seeding.force:false}")
    private boolean forceSeeding;

    public DataInitializer(
        MembershipRepository membershipRepository,
        UserRepository userRepository,
        SpaRepository spaRepository,
        ServiceRepository serviceRepository,
        BookingRepository bookingRepository,
        ReviewRepository reviewRepository
    ) {
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.spaRepository = spaRepository;
        this.serviceRepository = serviceRepository;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Main entry point - Called automatically on application startup
     * Orchestrates the seeding process in the correct order
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("=".repeat(80));
        logger.info("=== DATABASE SEEDING CHECK ===");
        logger.info("Seeding enabled: {}", seedingEnabled);
        logger.info("Force seeding: {}", forceSeeding);
        logger.info("=".repeat(80));

        // Check if seeding is disabled
        if (!seedingEnabled) {
            logger.info("Data seeding is disabled. Skipping...");
            return;
        }

        // Check if data already exists (prevent duplicate seeding on restart)
        if (membershipRepository.count() > 0 && !forceSeeding) {
            logger.warn(
                "Database already contains {} membership records. Skipping seeding to prevent duplicates.",
                membershipRepository.count()
            );
            logger.info("Set app.seeding.force=true to override this check.");
            logger.info("=".repeat(80));
            return;
        }

        logger.info("=== DATABASE SEEDING STARTED ===");
        logger.info("Seeding database with test/demo data...");
        logger.info("=".repeat(80));

        try {
            // Step 1: Seed Memberships (foundational data, no dependencies)
            logger.info("\n[1/7] Seeding Memberships...");
            seedMemberships();

            // Step 2: Seed Users (all roles: ADMIN, CLIENT, USER)
            logger.info("[2/7] Seeding Users...");
            seedUsers();

            // Step 3: Seed Spas (owned by CLIENT users)
            logger.info("[3/7] Seeding Spas...");
            seedSpas();

            // Step 4: Seed Services (offered by spas)
            logger.info("[4/7] Seeding Services...");
            seedServices();

            // Step 5: Seed Bookings (customers booking services)
            logger.info("[5/7] Seeding Bookings...");
            seedBookings();

            // Step 6: Seed Reviews (customers reviewing spas)
            logger.info("[6/7] Seeding Reviews...");
            seedReviews();

            // Step 7: Seed Wishlists (users wishlisting services)
            logger.info(
                "[7/7] Seeding Wishlists (User-Service relationships)..."
            );
            seedWishlists();

            logger.info("\n" + "=".repeat(80));
            logger.info("=== DATABASE SEEDING COMPLETED SUCCESSFULLY ===");
            logger.info(
                "Final counts - Memberships: {}, Users: {}, Spas: {}",
                membershipRepository.count(),
                userRepository.count(),
                spaRepository.count()
            );
            logger.info("=".repeat(80));
        } catch (Exception e) {
            logger.error("ERROR during database seeding: ", e);
            throw new RuntimeException("Database seeding failed", e);
        }
    }

    /**
     * STEP 1: Seed Memberships
     * Membership tiers with pricing and discount benefits
     * No dependencies - can be created first
     */
    private void seedMemberships() {
        Membership basicMembership = new Membership(
            "Basic",
            "Basic membership with 5% discount on all services",
            9.99,
            5.0
        );

        Membership premiumMembership = new Membership(
            "Premium",
            "Premium membership with 15% discount and priority booking",
            19.99,
            15.0
        );

        Membership goldMembership = new Membership(
            "Gold",
            "Gold membership with 25% discount, priority booking, and exclusive services",
            49.99,
            25.0
        );

        membershipRepository.save(basicMembership);
        membershipRepository.save(premiumMembership);
        membershipRepository.save(goldMembership);

        logger.info("✓ Created 3 membership tiers: Basic, Premium, Gold");
    }

    /**
     * STEP 2: Seed Users
     * Creates users with different roles:
     * - ADMIN: System administrator (1 user)
     * - CLIENT: Spa owners (2 users)
     * - USER: Customers (5 users)
     */
    private void seedUsers() {
        // Fetch memberships for user assignment
        Membership basicMem = membershipRepository
            .findByName("Basic")
            .orElse(null);
        Membership premiumMem = membershipRepository
            .findByName("Premium")
            .orElse(null);
        Membership goldMem = membershipRepository
            .findByName("Gold")
            .orElse(null);

        // --- ADMIN USER ---
        User admin = new User(
            "admin@spabooking.com",
            "admin123",
            "Admin",
            "Manager",
            "555-0000",
            Role.ADMIN
        );
        admin.setMembershipStatus(MembershipStatus.INACTIVE);
        userRepository.save(admin);

        // --- CLIENT USERS (Spa Owners) ---
        User client1 = new User(
            "owner1@relaxspa.com",
            "owner123",
            "Sarah",
            "Williams",
            "555-0001",
            Role.CLIENT
        );
        client1.setMembership(basicMem);
        client1.setMembershipStatus(MembershipStatus.ACTIVE);
        userRepository.save(client1);

        User client2 = new User(
            "owner2@zenwell.com",
            "owner123",
            "Michael",
            "Chen",
            "555-0002",
            Role.CLIENT
        );
        client2.setMembership(premiumMem);
        client2.setMembershipStatus(MembershipStatus.ACTIVE);
        userRepository.save(client2);

        // --- REGULAR USERS (Customers) ---
        User user1 = new User(
            "john.doe@gmail.com",
            "password123",
            "John",
            "Doe",
            "555-1001",
            Role.USER
        );
        user1.setMembership(premiumMem);
        user1.setMembershipStatus(MembershipStatus.ACTIVE);
        userRepository.save(user1);

        User user2 = new User(
            "jane.smith@gmail.com",
            "password123",
            "Jane",
            "Smith",
            "555-1002",
            Role.USER
        );
        user2.setMembership(basicMem);
        user2.setMembershipStatus(MembershipStatus.ACTIVE);
        userRepository.save(user2);

        User user3 = new User(
            "robert.johnson@gmail.com",
            "password123",
            "Robert",
            "Johnson",
            "555-1003",
            Role.USER
        );
        user3.setMembership(goldMem);
        user3.setMembershipStatus(MembershipStatus.ACTIVE);
        userRepository.save(user3);

        User user4 = new User(
            "emily.brown@gmail.com",
            "password123",
            "Emily",
            "Brown",
            "555-1004",
            Role.USER
        );
        user4.setMembership(null);
        user4.setMembershipStatus(MembershipStatus.INACTIVE);
        userRepository.save(user4);

        User user5 = new User(
            "david.lee@gmail.com",
            "password123",
            "David",
            "Lee",
            "555-1005",
            Role.USER
        );
        user5.setMembership(basicMem);
        user5.setMembershipStatus(MembershipStatus.ACTIVE);
        userRepository.save(user5);

        logger.info(
            "✓ Created 8 users: 1 ADMIN, 2 CLIENTs (spa owners), 5 USERs (customers)"
        );
    }

    /**
     * STEP 3: Seed Spas
     * Creates spa establishments owned by CLIENT users
     * Dependencies: Users (as owners)
     */
    private void seedSpas() {
        // Fetch CLIENT users to use as spa owners
        User client1 = userRepository
            .findByEmail("owner1@relaxspa.com")
            .orElse(null);
        User client2 = userRepository
            .findByEmail("owner2@zenwell.com")
            .orElse(null);

        // --- SPA 1: Relax Spa ---
        Spa spa1 = new Spa(
            "Relax Spa & Wellness Center",
            "123 Main Street, Downtown",
            "Premium spa offering massage, facial, and body treatments",
            client1
        );
        spa1.setApprovalStatus(ApprovalStatus.APPROVED); // ADMIN approved
        spaRepository.save(spa1);

        // --- SPA 2: Zen Well ---
        Spa spa2 = new Spa(
            "Zen Well Spa",
            "456 Oak Avenue, Uptown",
            "Holistic wellness center with traditional and modern treatments",
            client2
        );
        spa2.setApprovalStatus(ApprovalStatus.APPROVED);
        spaRepository.save(spa2);

        // --- SPA 3: Serenity Springs (Pending Approval) ---
        Spa spa3 = new Spa(
            "Serenity Springs Spa",
            "789 Pine Road, Midtown",
            "Luxury spa resort with thermal springs and mud baths",
            client1
        );
        spa3.setApprovalStatus(ApprovalStatus.PENDING); // Awaiting admin approval
        spaRepository.save(spa3);

        logger.info(
            "✓ Created 3 spas: 2 APPROVED, 1 PENDING (awaiting admin approval)"
        );
    }

    /**
     * STEP 4: Seed Services
     * Creates services offered by spas
     * Dependencies: Spas
     */
    private void seedServices() {
        // Fetch spas
        Spa spa1 = spaRepository.findById(1L).orElse(null);
        Spa spa2 = spaRepository.findById(2L).orElse(null);
        Spa spa3 = spaRepository.findById(3L).orElse(null);

        // --- SERVICES FOR SPA 1: Relax Spa ---
        Service massage = new Service(
            "Swedish Massage",
            "Full body relaxation massage - 60 minutes",
            75.00,
            60,
            spa1
        );
        massage.setApprovalStatus(ApprovalStatus.APPROVED);
        massage.setServiceStatus(ServiceStatus.AVAILABLE);
        serviceRepository.save(massage);

        Service facial = new Service(
            "Hydrating Facial",
            "Deep cleansing and hydration facial - 45 minutes",
            65.00,
            45,
            spa1
        );
        facial.setApprovalStatus(ApprovalStatus.APPROVED);
        facial.setServiceStatus(ServiceStatus.AVAILABLE);
        serviceRepository.save(facial);

        Service manicure = new Service(
            "Gel Manicure",
            "Professional gel manicure with design - 30 minutes",
            40.00,
            30,
            spa1
        );
        manicure.setApprovalStatus(ApprovalStatus.APPROVED);
        manicure.setServiceStatus(ServiceStatus.UNAVAILABLE); // Currently unavailable
        serviceRepository.save(manicure);

        // --- SERVICES FOR SPA 2: Zen Well ---
        Service hotStoneTherapy = new Service(
            "Hot Stone Therapy",
            "Therapeutic massage with heated stones - 90 minutes",
            95.00,
            90,
            spa2
        );
        hotStoneTherapy.setApprovalStatus(ApprovalStatus.APPROVED);
        hotStoneTherapy.setServiceStatus(ServiceStatus.AVAILABLE);
        serviceRepository.save(hotStoneTherapy);

        Service aromatherapy = new Service(
            "Aromatherapy Treatment",
            "Relaxation with essential oils - 50 minutes",
            70.00,
            50,
            spa2
        );
        aromatherapy.setApprovalStatus(ApprovalStatus.APPROVED);
        aromatherapy.setServiceStatus(ServiceStatus.AVAILABLE);
        serviceRepository.save(aromatherapy);

        Service yogaClass = new Service(
            "Yoga Class",
            "Morning yoga session for relaxation - 60 minutes",
            30.00,
            60,
            spa2
        );
        yogaClass.setApprovalStatus(ApprovalStatus.PENDING); // Awaiting admin approval
        yogaClass.setServiceStatus(ServiceStatus.UNAVAILABLE);
        serviceRepository.save(yogaClass);

        // --- SERVICES FOR SPA 3: Serenity Springs (Pending Approval) ---
        Service mudBath = new Service(
            "Mud Bath Treatment",
            "Thermal mud bath with minerals - 45 minutes",
            85.00,
            45,
            spa3
        );
        mudBath.setApprovalStatus(ApprovalStatus.PENDING);
        mudBath.setServiceStatus(ServiceStatus.UNAVAILABLE);
        serviceRepository.save(mudBath);

        logger.info(
            "✓ Created 7 services: 5 APPROVED, 2 PENDING (awaiting admin approval)"
        );
        logger.info("  - Service status mix: 4 AVAILABLE, 3 UNAVAILABLE");
    }

    /**
     * STEP 5: Seed Bookings
     * Creates booking records for customers booking services at spas
     * Dependencies: Users (customers), Spas, Services
     */
    private void seedBookings() {
        // Fetch users and services
        User customer1 = userRepository
            .findByEmail("john.doe@gmail.com")
            .orElse(null);
        User customer2 = userRepository
            .findByEmail("jane.smith@gmail.com")
            .orElse(null);
        User customer3 = userRepository
            .findByEmail("robert.johnson@gmail.com")
            .orElse(null);
        User customer4 = userRepository
            .findByEmail("emily.brown@gmail.com")
            .orElse(null);

        Spa spa1 = spaRepository.findById(1L).orElse(null);
        Spa spa2 = spaRepository.findById(2L).orElse(null);

        Service massage = serviceRepository.findById(1L).orElse(null);
        Service facial = serviceRepository.findById(2L).orElse(null);
        Service hotStoneTherapy = serviceRepository.findById(4L).orElse(null);
        Service aromatherapy = serviceRepository.findById(5L).orElse(null);

        // --- BOOKING 1: John Doe books Swedish Massage ---
        Booking booking1 = new Booking(
            LocalDateTime.now().minusDays(5),
            customer1,
            spa1,
            massage,
            75.00 // With 15% membership discount: 75 * 0.85 = 63.75
        );
        booking1.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking1);

        // --- BOOKING 2: Jane Smith books Hydrating Facial ---
        Booking booking2 = new Booking(
            LocalDateTime.now().minusDays(3),
            customer2,
            spa1,
            facial,
            65.00 // With 5% membership discount: 65 * 0.95 = 61.75
        );
        booking2.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking2);

        // --- BOOKING 3: Robert Johnson books Hot Stone Therapy ---
        Booking booking3 = new Booking(
            LocalDateTime.now().plusDays(2),
            customer3,
            spa2,
            hotStoneTherapy,
            95.00 // With 25% membership discount: 95 * 0.75 = 71.25
        );
        booking3.setStatus(BookingStatus.PENDING); // Awaiting spa confirmation
        bookingRepository.save(booking3);

        // --- BOOKING 4: Emily Brown books Aromatherapy (No membership) ---
        Booking booking4 = new Booking(
            LocalDateTime.now().plusDays(5),
            customer4,
            spa2,
            aromatherapy,
            70.00 // No discount, full price
        );
        booking4.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking4);

        // --- BOOKING 5: David Lee books Aromatherapy ---
        User customer5 = userRepository
            .findByEmail("david.lee@gmail.com")
            .orElse(null);
        Booking booking5 = new Booking(
            LocalDateTime.now().minusDays(1),
            customer5,
            spa2,
            aromatherapy,
            70.00 // With 5% membership discount: 70 * 0.95 = 66.50
        );
        booking5.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking5);

        logger.info("✓ Created 5 bookings: 3 CONFIRMED, 2 PENDING");
        logger.info("  - Bookings demonstrate membership discount application");
    }

    /**
     * STEP 6: Seed Reviews
     * Creates reviews/ratings for spas by customers
     * Dependencies: Users, Spas
     */
    private void seedReviews() {
        // Fetch users and spas
        User user1 = userRepository
            .findByEmail("john.doe@gmail.com")
            .orElse(null);
        User user2 = userRepository
            .findByEmail("jane.smith@gmail.com")
            .orElse(null);
        User user3 = userRepository
            .findByEmail("david.lee@gmail.com")
            .orElse(null);

        Spa spa1 = spaRepository.findById(1L).orElse(null);
        Spa spa2 = spaRepository.findById(2L).orElse(null);

        // --- REVIEW 1: John Doe reviews Relax Spa ---
        Review review1 = new Review(
            5,
            "Excellent service! The massage therapist was very professional and the atmosphere was very relaxing.",
            user1,
            spa1
        );
        reviewRepository.save(review1);

        // --- REVIEW 2: Jane Smith reviews Relax Spa ---
        Review review2 = new Review(
            4,
            "Great facial treatment. The staff was friendly, but could have been a bit more personalized.",
            user2,
            spa1
        );
        reviewRepository.save(review2);

        // --- REVIEW 3: John Doe reviews Zen Well ---
        Review review3 = new Review(
            5,
            "Outstanding experience! Will definitely come back for the hot stone therapy.",
            user1,
            spa2
        );
        reviewRepository.save(review3);

        // --- REVIEW 4: David Lee reviews Zen Well ---
        Review review4 = new Review(
            4,
            "Very relaxing aromatherapy session. Prices are a bit high but quality justifies it.",
            user3,
            spa2
        );
        reviewRepository.save(review4);

        // --- REVIEW 5: Jane Smith reviews Zen Well ---
        Review review5 = new Review(
            3,
            "Good experience, but the facility could be cleaner. Services were good though.",
            user2,
            spa2
        );
        reviewRepository.save(review5);

        logger.info("✓ Created 5 reviews with ratings from 3 to 5 stars");
        logger.info("  - Spa 1: 2 reviews (avg: 4.5 stars)");
        logger.info("  - Spa 2: 3 reviews (avg: 4.0 stars)");
    }

    /**
     * STEP 7: Seed Wishlists (Many-to-Many User-Service relationships)
     * Users wishlist services for future booking consideration
     * Dependencies: Users, Services
     */
    private void seedWishlists() {
        // Fetch users
        User user1 = userRepository
            .findByEmail("john.doe@gmail.com")
            .orElse(null);
        User user2 = userRepository
            .findByEmail("jane.smith@gmail.com")
            .orElse(null);
        User user4 = userRepository
            .findByEmail("emily.brown@gmail.com")
            .orElse(null);

        // Fetch services
        Service manicure = serviceRepository.findById(3L).orElse(null);
        Service yogaClass = serviceRepository.findById(6L).orElse(null);
        Service mudBath = serviceRepository.findById(7L).orElse(null);

        // --- USER 1 (John) wishlists services ---
        user1.getWishlist().add(manicure); // Wants to try gel manicure
        user1.getWishlist().add(yogaClass); // Interested in yoga
        userRepository.save(user1);

        // --- USER 2 (Jane) wishlists services ---
        user2.getWishlist().add(mudBath); // Wants to experience mud bath
        userRepository.save(user2);

        // --- USER 4 (Emily) wishlists services ---
        user4.getWishlist().add(yogaClass);
        user4.getWishlist().add(manicure);
        userRepository.save(user4);

        logger.info(
            "✓ Created 5 wishlist entries (User-Service relationships)"
        );
        logger.info("  - John: 2 wishlisted services");
        logger.info("  - Jane: 1 wishlisted service");
        logger.info("  - Emily: 2 wishlisted services");
    }
}
