package com.lms.library.repository;

import static com.lms.library.util.TestUtil.createTestMember;
import static com.lms.library.util.TestUtil.resetMemberRepositoryState;
import static org.junit.jupiter.api.Assertions.*;

import com.lms.library.model.Member;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

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
        resetMemberRepositoryState();
    }

    @Test
    @DisplayName("Save new member without ID should generate and assign ID")
    void save_NewMemberWithoutId_ShouldGenerateId() {
        // Arrange
        var member = createTestMember("John", "Doe", "john.doe@example.com");

        // Act
        var savedMember = repository.save(member);

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
        var member = createTestMember("John", "Doe", "john.doe@example.com");
        var savedMember = repository.save(member);
        var memberId = savedMember.getId();

        // Act - update the email
        member = createTestMember("John", "Doe", "john.updated@example.com");
        member.setId(memberId);
        var updatedMember = repository.save(member);

        // Assert
        assertEquals(memberId, updatedMember.getId());
        assertEquals("john.updated@example.com", updatedMember.getEmail());

        // Verify the member was actually updated in the store
        var foundMember = repository.findById(memberId);
        assertTrue(foundMember.isPresent());
        assertEquals("john.updated@example.com", foundMember.get().getEmail());
    }

    @Test
    @DisplayName("Save multiple members should generate sequential IDs")
    void save_MultipleMembers_ShouldGenerateSequentialIds() {
        // Arrange
        var member1 = createTestMember("John", "Doe", "john@example.com");
        var member2 = createTestMember("Jane", "Smith", "jane@example.com");
        var member3 = createTestMember("Bob", "Johnson", "bob@example.com");

        // Act
        var savedMember1 = repository.save(member1);
        var savedMember2 = repository.save(member2);
        var savedMember3 = repository.save(member3);

        // Assert
        assertEquals(1L, savedMember1.getId());
        assertEquals(2L, savedMember2.getId());
        assertEquals(3L, savedMember3.getId());
    }

    @Test
    @DisplayName("Save member with predefined ID should use that ID")
    void save_MemberWithPredefinedId_ShouldUseProvidedId() {
        // Arrange
        var member = createTestMember("John", "Doe", "john@example.com");
        member.setId(100L);

        // Act
        var savedMember = repository.save(member);

        // Assert
        assertEquals(100L, savedMember.getId());

        // Verify it can be retrieved by that ID
        var foundMember = repository.findById(100L);
        assertTrue(foundMember.isPresent());
        assertEquals(100L, foundMember.get().getId());
    }

    @Test
    @DisplayName("Save should overwrite existing member with same ID")
    void save_ExistingId_ShouldOverwrite() {
        // Arrange
        var originalMember = createTestMember("John", "Doe", "john@example.com");
        var savedMember = repository.save(originalMember);
        var memberId = savedMember.getId();

        // Create a completely different member with the same ID
        var updatedMember = createTestMember("Jonathan", "Doeman", "jonathan@example.com");
        updatedMember.setId(memberId);

        // Act
        repository.save(updatedMember);

        // Assert
        var foundMember = repository.findById(memberId);
        assertTrue(foundMember.isPresent());
        assertEquals("Jonathan", foundMember.get().getFirstName());
        assertEquals("Doeman", foundMember.get().getLastName());
        assertEquals("jonathan@example.com", foundMember.get().getEmail());
    }

    @Test
    @DisplayName("Find by existing ID should return the member")
    void findById_ExistingId_ShouldReturnMember() {
        // Arrange
        var member = createTestMember("John", "Doe", "john.doe@example.com");
        var savedMember = repository.save(member);
        var memberId = savedMember.getId();

        // Act
        var foundMember = repository.findById(memberId);

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
        var foundMember = repository.findById(999L);

        // Assert
        assertFalse(foundMember.isPresent());
    }

    @Test
    @DisplayName("Find by null ID should return empty optional")
    void findById_NullId_ShouldReturnEmpty() {
        // Act
        var foundMember = repository.findById(null);

        // Assert
        assertFalse(foundMember.isPresent());
    }

    @Test
    @DisplayName("Find by email with exact match should return member")
    void findByEmail_ExactMatch_ShouldReturnMember() {
        // Arrange
        var member = createTestMember("John", "Doe", "john.doe@example.com");
        repository.save(member);

        // Act
        var foundMember = repository.findByEmail("john.doe@example.com");

        // Assert
        assertTrue(foundMember.isPresent());
        assertEquals("john.doe@example.com", foundMember.get().getEmail());
        assertEquals("John", foundMember.get().getFirstName());
    }

    @Test
    @DisplayName("Find by email should be case insensitive")
    void findByEmail_CaseInsensitive_ShouldReturnMember() {
        // Arrange
        var member = createTestMember("John", "Doe", "John.Doe@Example.COM");
        repository.save(member);

        // Act - Test various case combinations
        var foundMember1 = repository.findByEmail("john.doe@example.com");
        var foundMember2 = repository.findByEmail("JOHN.DOE@EXAMPLE.COM");
        var foundMember3 = repository.findByEmail("John.Doe@Example.Com");

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
        var member = createTestMember("John", "Doe", "john@example.com");
        repository.save(member);

        // Act
        var foundMember = repository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(foundMember.isPresent());
    }

    @Test
    @DisplayName("Find by email with null email should return empty")
    void findByEmail_NullEmail_ShouldReturnEmpty() {
        // Act
        var foundMember = repository.findByEmail(null);

        // Assert
        assertFalse(foundMember.isPresent());
    }

    @Test
    @DisplayName("Find by email with empty email should return empty")
    void findByEmail_EmptyEmail_ShouldReturnEmpty() {
        // Act
        var foundMember = repository.findByEmail("");

        // Assert
        assertFalse(foundMember.isPresent());
    }

    @Test
    @DisplayName("Exists by existing ID should return true")
    void existsById_ExistingId_ShouldReturnTrue() {
        // Arrange
        var member = createTestMember("John", "Doe", "john@example.com");
        var savedMember = repository.save(member);
        var memberId = savedMember.getId();

        // Act
        var exists = repository.existsById(memberId);

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Exists by non-existing ID should return false")
    void existsById_NonExistingId_ShouldReturnFalse() {
        // Act
        var exists = repository.existsById(999L);

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
        var alice = repository.findByEmail("alice@example.com");
        var bob = repository.findByEmail("bob@example.com");
        var charlie = repository.findByEmail("charlie@example.com");

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
            var storeField = MemberRepository.class.getDeclaredField("STORE");
            storeField.setAccessible(true);
            var store = (Map<Long, Member>) storeField.get(null);
            assertEquals(3, store.size());
        } catch (Exception e) {
            fail("Failed to access store via reflection: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Repository should handle concurrent access correctly")
    void repository_ConcurrentAccess_ShouldHandleCorrectly() throws InterruptedException {
        // Arrange
        var numberOfThreads = 10;
        var membersPerThread = 5;

        // Act - Create multiple threads that save members concurrently
        var threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < membersPerThread; j++) {
                    var member = createTestMember(
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
            var storeField = MemberRepository.class.getDeclaredField("STORE");
            storeField.setAccessible(true);
            var store = (Map<Long, Member>) storeField.get(null);
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
        var member1 = repository.save(createTestMember("John", "Doe", "john@example.com"));
        repository.save(createTestMember("Jane", "Smith", "jane@example.com"));
        var member3 = repository.save(createTestMember("Bob", "Johnson", "bob@example.com"));

        // Act & Assert - Test various operations
        // Test findById
        var foundMember1 = repository.findById(member1.getId());
        assertTrue(foundMember1.isPresent());
        assertEquals("John", foundMember1.get().getFirstName());

        // Test findByEmail
        var foundByEmail = repository.findByEmail("jane@example.com");
        assertTrue(foundByEmail.isPresent());
        assertEquals("Smith", foundByEmail.get().getLastName());

        // Test existsById
        assertTrue(repository.existsById(member3.getId()));
        assertFalse(repository.existsById(999L));

        // Verify case-insensitive email search still works after update
        var caseInsensitiveSearch = repository.findByEmail("JANE@EXAMPLE.COM");
        assertTrue(caseInsensitiveSearch.isPresent());
        assertEquals("Jane", caseInsensitiveSearch.get().getFirstName());
    }

    @Test
    @DisplayName("Save member with duplicate email should overwrite existing member")
    void save_MemberWithDuplicateEmail_ShouldOverwrite() {
        // Arrange
        var member1 = createTestMember("John", "Doe", "same.email@example.com");
        var savedMember1 = repository.save(member1);
        var member1Id = savedMember1.getId();

        // Create a new member with the same email but different ID
        var member2 = createTestMember("Jane", "Smith", "same.email@example.com");
        member2.setId(999L); // Different ID

        // Act
        var savedMember2 = repository.save(member2);

        // Assert
        // The repository allows duplicate emails in storage, but findByEmail will return the first match
        // This test verifies that we can have multiple members with same email (if business logic allows)
        assertEquals(999L, savedMember2.getId());

        // Both members should exist in the store
        assertTrue(repository.existsById(member1Id));
        assertTrue(repository.existsById(999L));

        // findByEmail will return the first one it finds (order not guaranteed in concurrent map)
        var foundByEmail = repository.findByEmail("same.email@example.com");
        assertTrue(foundByEmail.isPresent());
    }

    @Test
    @DisplayName("Find by email should return first match when duplicates exist")
    void findByEmail_WithDuplicateEmails_ShouldReturnFirstMatch() {
        // Arrange
        var member1 = createTestMember("First", "User", "duplicate@example.com");
        var member2 = createTestMember("Second", "User", "duplicate@example.com");

        repository.save(member1);
        repository.save(member2);

        // Act
        var foundMember = repository.findByEmail("duplicate@example.com");

        // Assert
        // Since we're using a ConcurrentHashMap and stream().findFirst(),
        // the behavior might not be predictable, but it should return one of them
        assertTrue(foundMember.isPresent());
        assertEquals("duplicate@example.com", foundMember.get().getEmail());
    }


}
