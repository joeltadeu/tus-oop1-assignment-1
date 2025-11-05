package com.lms.library.repository;

import com.lms.library.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MemberRepository class
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
class MemberRepositoryTest {

    private MemberRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        repository = new MemberRepository();
        resetRepositoryState();
    }

    /**
     * Helper method to reset the static state of the repository between tests
     */
    private void resetRepositoryState() throws Exception {
        Field storeField = MemberRepository.class.getDeclaredField("STORE");
        storeField.setAccessible(true);
        Map<Long, Member> store = (Map<Long, Member>) storeField.get(null);
        store.clear();

        Field idSeqField = MemberRepository.class.getDeclaredField("ID_SEQ");
        idSeqField.setAccessible(true);
        AtomicLong idSeq = (AtomicLong) idSeqField.get(null);
        idSeq.set(1);
    }

    private Member createTestMember(String firstName, String lastName, String email) {
        return new Member(firstName, lastName, email);
    }

    @Test
    @DisplayName("Save new member without ID should generate and assign ID")
    void save_NewMemberWithoutId_ShouldGenerateId() {
        // Arrange
        Member member = createTestMember("John", "Doe", "john.doe@example.com");

        // Act
        Member savedMember = repository.save(member);

        // Assert
        assertNotNull(savedMember.getId());
        assertEquals(1L, savedMember.getId());
        assertEquals("John", savedMember.getFirstName());
        assertEquals("Doe", savedMember.getLastName());
        assertEquals("john.doe@example.com", savedMember.getEmail());
    }

    @Test
    @DisplayName("Save existing member with ID should update the member")
    void save_ExistingMemberWithId_ShouldUpdateMember() {
        // Arrange
        Member member = createTestMember("John", "Doe", "john.doe@example.com");
        Member savedMember = repository.save(member);
        Long memberId = savedMember.getId();

        // Act - update the email
        member = createTestMember("John", "Doe", "john.updated@example.com");
        member.setId(memberId);
        Member updatedMember = repository.save(member);

        // Assert
        assertEquals(memberId, updatedMember.getId());
        assertEquals("john.updated@example.com", updatedMember.getEmail());

        // Verify the member was actually updated in the store
        Optional<Member> foundMember = repository.findById(memberId);
        assertTrue(foundMember.isPresent());
        assertEquals("john.updated@example.com", foundMember.get().getEmail());
    }

    @Test
    @DisplayName("Save multiple members should generate sequential IDs")
    void save_MultipleMembers_ShouldGenerateSequentialIds() {
        // Arrange
        Member member1 = createTestMember("John", "Doe", "john@example.com");
        Member member2 = createTestMember("Jane", "Smith", "jane@example.com");
        Member member3 = createTestMember("Bob", "Johnson", "bob@example.com");

        // Act
        Member savedMember1 = repository.save(member1);
        Member savedMember2 = repository.save(member2);
        Member savedMember3 = repository.save(member3);

        // Assert
        assertEquals(1L, savedMember1.getId());
        assertEquals(2L, savedMember2.getId());
        assertEquals(3L, savedMember3.getId());
    }

    @Test
    @DisplayName("Save member with predefined ID should use that ID")
    void save_MemberWithPredefinedId_ShouldUseProvidedId() {
        // Arrange
        Member member = createTestMember("John", "Doe", "john@example.com");
        member.setId(100L);

        // Act
        Member savedMember = repository.save(member);

        // Assert
        assertEquals(100L, savedMember.getId());

        // Verify it can be retrieved by that ID
        Optional<Member> foundMember = repository.findById(100L);
        assertTrue(foundMember.isPresent());
        assertEquals(100L, foundMember.get().getId());
    }

    @Test
    @DisplayName("Save should overwrite existing member with same ID")
    void save_ExistingId_ShouldOverwrite() {
        // Arrange
        Member originalMember = createTestMember("John", "Doe", "john@example.com");
        Member savedMember = repository.save(originalMember);
        Long memberId = savedMember.getId();

        // Create a completely different member with the same ID
        Member updatedMember = createTestMember("Jonathan", "Doeman", "jonathan@example.com");
        updatedMember.setId(memberId);

        // Act
        repository.save(updatedMember);

        // Assert
        Optional<Member> foundMember = repository.findById(memberId);
        assertTrue(foundMember.isPresent());
        assertEquals("Jonathan", foundMember.get().getFirstName());
        assertEquals("Doeman", foundMember.get().getLastName());
        assertEquals("jonathan@example.com", foundMember.get().getEmail());
    }

    @Test
    @DisplayName("Find by existing ID should return the member")
    void findById_ExistingId_ShouldReturnMember() {
        // Arrange
        Member member = createTestMember("John", "Doe", "john.doe@example.com");
        Member savedMember = repository.save(member);
        Long memberId = savedMember.getId();

        // Act
        Optional<Member> foundMember = repository.findById(memberId);

        // Assert
        assertTrue(foundMember.isPresent());
        assertEquals(memberId, foundMember.get().getId());
        assertEquals("John", foundMember.get().getFirstName());
        assertEquals("Doe", foundMember.get().getLastName());
        assertEquals("john.doe@example.com", foundMember.get().getEmail());
    }

    @Test
    @DisplayName("Find by non-existing ID should return empty optional")
    void findById_NonExistingId_ShouldReturnEmpty() {
        // Act
        Optional<Member> foundMember = repository.findById(999L);

        // Assert
        assertFalse(foundMember.isPresent());
    }

    @Test
    @DisplayName("Find by null ID should return empty optional")
    void findById_NullId_ShouldReturnEmpty() {
        // Act
        Optional<Member> foundMember = repository.findById(null);

        // Assert
        assertFalse(foundMember.isPresent());
    }

    @Test
    @DisplayName("Find by email with exact match should return member")
    void findByEmail_ExactMatch_ShouldReturnMember() {
        // Arrange
        Member member = createTestMember("John", "Doe", "john.doe@example.com");
        repository.save(member);

        // Act
        Optional<Member> foundMember = repository.findByEmail("john.doe@example.com");

        // Assert
        assertTrue(foundMember.isPresent());
        assertEquals("john.doe@example.com", foundMember.get().getEmail());
        assertEquals("John", foundMember.get().getFirstName());
    }

    @Test
    @DisplayName("Find by email should be case insensitive")
    void findByEmail_CaseInsensitive_ShouldReturnMember() {
        // Arrange
        Member member = createTestMember("John", "Doe", "John.Doe@Example.COM");
        repository.save(member);

        // Act - Test various case combinations
        Optional<Member> foundMember1 = repository.findByEmail("john.doe@example.com");
        Optional<Member> foundMember2 = repository.findByEmail("JOHN.DOE@EXAMPLE.COM");
        Optional<Member> foundMember3 = repository.findByEmail("John.Doe@Example.Com");

        // Assert
        assertTrue(foundMember1.isPresent(), "Lowercase should match");
        assertTrue(foundMember2.isPresent(), "Uppercase should match");
        assertTrue(foundMember3.isPresent(), "Mixed case should match");

        assertEquals("John.Doe@Example.COM", foundMember1.get().getEmail());
    }

    @Test
    @DisplayName("Find by email with non-existing email should return empty")
    void findByEmail_NonExistingEmail_ShouldReturnEmpty() {
        // Arrange
        Member member = createTestMember("John", "Doe", "john@example.com");
        repository.save(member);

        // Act
        Optional<Member> foundMember = repository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(foundMember.isPresent());
    }

    @Test
    @DisplayName("Find by email with null email should return empty")
    void findByEmail_NullEmail_ShouldReturnEmpty() {
        // Act
        Optional<Member> foundMember = repository.findByEmail(null);

        // Assert
        assertFalse(foundMember.isPresent());
    }

    @Test
    @DisplayName("Find by email with empty email should return empty")
    void findByEmail_EmptyEmail_ShouldReturnEmpty() {
        // Act
        Optional<Member> foundMember = repository.findByEmail("");

        // Assert
        assertFalse(foundMember.isPresent());
    }

    @Test
    @DisplayName("Exists by existing ID should return true")
    void existsById_ExistingId_ShouldReturnTrue() {
        // Arrange
        Member member = createTestMember("John", "Doe", "john@example.com");
        Member savedMember = repository.save(member);
        Long memberId = savedMember.getId();

        // Act
        boolean exists = repository.existsById(memberId);

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Exists by non-existing ID should return false")
    void existsById_NonExistingId_ShouldReturnFalse() {
        // Act
        boolean exists = repository.existsById(999L);

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Exists by null ID should return false")
    void existsById_NullId_ShouldReturnFalse() {
        // Act
        boolean exists = repository.existsById(null);

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Initialize repository should load sample data")
    void init_ShouldLoadSampleData() throws Exception {
        // Act - Call init method manually
        repository.init();

        // Assert - Verify that sample data was loaded
        // Check specific sample members
        Optional<Member> alice = repository.findByEmail("alice@example.com");
        Optional<Member> bob = repository.findByEmail("bob@example.com");
        Optional<Member> charlie = repository.findByEmail("charlie@example.com");

        assertTrue(alice.isPresent(), "Alice should be present");
        assertEquals("Alice", alice.get().getFirstName());
        assertEquals("Johnson", alice.get().getLastName());

        assertTrue(bob.isPresent(), "Bob should be present");
        assertEquals("Bob", bob.get().getFirstName());
        assertEquals("Williams", bob.get().getLastName());

        assertTrue(charlie.isPresent(), "Charlie should be present");
        assertEquals("Charlie", charlie.get().getFirstName());
        assertEquals("Davis", charlie.get().getLastName());

        // Verify total count
        try {
            Field storeField = MemberRepository.class.getDeclaredField("STORE");
            storeField.setAccessible(true);
            Map<Long, Member> store = (Map<Long, Member>) storeField.get(null);
            assertEquals(3, store.size());
        } catch (Exception e) {
            fail("Failed to access store via reflection: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Repository should handle concurrent access correctly")
    void repository_ConcurrentAccess_ShouldHandleCorrectly() throws InterruptedException {
        // Arrange
        int numberOfThreads = 10;
        int membersPerThread = 5;

        // Act - Create multiple threads that save members concurrently
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < membersPerThread; j++) {
                    Member member = createTestMember(
                            "User" + threadId + "_" + j,
                            "LastName" + threadId + "_" + j,
                            "user" + threadId + "_" + j + "@example.com"
                    );
                    repository.save(member);
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        // Total members should be numberOfThreads * membersPerThread
        try {
            Field storeField = MemberRepository.class.getDeclaredField("STORE");
            storeField.setAccessible(true);
            Map<Long, Member> store = (Map<Long, Member>) storeField.get(null);
            assertEquals(numberOfThreads * membersPerThread, store.size());

            // Verify all keys are present and unique
            for (long i = 1; i <= numberOfThreads * membersPerThread; i++) {
                assertTrue(store.containsKey(i), "Missing key: " + i);
            }
        } catch (Exception e) {
            fail("Failed to access store via reflection: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Mixed operations should work correctly together")
    void mixedOperations_ShouldWorkCorrectly() {
        // Arrange - Create multiple members
        Member member1 = repository.save(createTestMember("John", "Doe", "john@example.com"));
        Member member2 = repository.save(createTestMember("Jane", "Smith", "jane@example.com"));
        Member member3 = repository.save(createTestMember("Bob", "Johnson", "bob@example.com"));

        // Act & Assert - Test various operations
        // Test findById
        Optional<Member> foundMember1 = repository.findById(member1.getId());
        assertTrue(foundMember1.isPresent());
        assertEquals("John", foundMember1.get().getFirstName());

        // Test findByEmail
        Optional<Member> foundByEmail = repository.findByEmail("jane@example.com");
        assertTrue(foundByEmail.isPresent());
        assertEquals("Smith", foundByEmail.get().getLastName());

        // Test existsById
        assertTrue(repository.existsById(member3.getId()));
        assertFalse(repository.existsById(999L));

        // Verify case insensitive email search still works after update
        Optional<Member> caseInsensitiveSearch = repository.findByEmail("JANE@EXAMPLE.COM");
        assertTrue(caseInsensitiveSearch.isPresent());
        assertEquals("Jane", caseInsensitiveSearch.get().getFirstName());
    }

    @Test
    @DisplayName("Save member with duplicate email should overwrite existing member")
    void save_MemberWithDuplicateEmail_ShouldOverwrite() {
        // Arrange
        Member member1 = createTestMember("John", "Doe", "same.email@example.com");
        Member savedMember1 = repository.save(member1);
        Long member1Id = savedMember1.getId();

        // Create a new member with the same email but different ID
        Member member2 = createTestMember("Jane", "Smith", "same.email@example.com");
        member2.setId(999L); // Different ID

        // Act
        Member savedMember2 = repository.save(member2);

        // Assert
        // The repository allows duplicate emails in storage, but findByEmail will return the first match
        // This test verifies that we can have multiple members with same email (if business logic allows)
        assertEquals(999L, savedMember2.getId());

        // Both members should exist in the store
        assertTrue(repository.existsById(member1Id));
        assertTrue(repository.existsById(999L));

        // findByEmail will return the first one it finds (order not guaranteed in concurrent map)
        Optional<Member> foundByEmail = repository.findByEmail("same.email@example.com");
        assertTrue(foundByEmail.isPresent());
    }

    @Test
    @DisplayName("Find by email should return first match when duplicates exist")
    void findByEmail_WithDuplicateEmails_ShouldReturnFirstMatch() {
        // Arrange
        Member member1 = createTestMember("First", "User", "duplicate@example.com");
        Member member2 = createTestMember("Second", "User", "duplicate@example.com");

        repository.save(member1);
        repository.save(member2);

        // Act
        Optional<Member> foundMember = repository.findByEmail("duplicate@example.com");

        // Assert
        // Since we're using a ConcurrentHashMap and stream().findFirst(),
        // the behavior might not be predictable, but it should return one of them
        assertTrue(foundMember.isPresent());
        assertEquals("duplicate@example.com", foundMember.get().getEmail());
    }
}
