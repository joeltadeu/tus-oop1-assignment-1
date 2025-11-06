package com.lms.library.repository;

import com.lms.library.model.Member;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Repository class for managing Member entities. Provides data access operations for library
 * members using an in-memory store.
 *
 * @author Joel Silva
 * @version 1.0
 * @see Member
 * @since 2025
 */
@Repository
public class MemberRepository {
  private static final Logger log = LoggerFactory.getLogger(MemberRepository.class);
  private static final Map<Long, Member> STORE = new ConcurrentHashMap<>();
  private static final AtomicLong ID_SEQ = new AtomicLong(1);

  /** Default constructor for MemberRepository. */
  public MemberRepository() {}

  /**
   * Initializes the repository with sample members. Called automatically after dependency injection
   * is complete.
   */
  @PostConstruct
  public void init() {
    save(new Member("Alice", "Johnson", "alice@example.com"));
    save(new Member("Bob", "Williams", "bob@example.com"));
    save(new Member("Charlie", "Davis", "charlie@example.com"));

    log.info("MemberRepository initialized with {} members.", STORE.size());
  }

  /**
   * Saves a member to the repository. If the member has no ID, generates a new one automatically.
   *
   * @param member the member to save
   * @return the saved member with generated ID
   */
  public Member save(Member member) {
    if (member.getId() == null) {
      member.setId(ID_SEQ.getAndIncrement());
    }
    STORE.put(member.getId(), member);
    return member;
  }

  /**
   * Finds a member by their ID.
   *
   * @param id the ID of the member to find
   * @return an Optional containing the found member, or empty if not found
   */
  public Optional<Member> findById(Long id) {
    return Optional.ofNullable(id).map(STORE::get);
  }

  /**
   * Finds a member by their email address (case-insensitive).
   *
   * @param email the email address to search for
   * @return an Optional containing the found member, or empty if not found
   */
  public Optional<Member> findByEmail(String email) {
    return STORE.values().stream().filter(m -> m.getEmail().equalsIgnoreCase(email)).findFirst();
  }

  /**
   * Checks if a member exists with the given ID.
   *
   * @param id the ID to check
   * @return true if a member exists with the given ID, false otherwise
   */
  public boolean existsById(Long id) {
    return Optional.ofNullable(id).map(STORE::containsKey).orElse(false);
  }
}
