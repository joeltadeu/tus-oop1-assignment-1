package com.lms.library.repository;

import com.lms.library.model.Member;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemberRepository {
    private static final Logger log = LoggerFactory.getLogger(MemberRepository.class);
    private static final Map<Long, Member> STORE = new ConcurrentHashMap<>();
    private static final AtomicLong ID_SEQ = new AtomicLong(1);

    @PostConstruct
    public void init() {
        save(new Member("Alice", "Johnson", "alice@example.com"));
        save(new Member("Bob", "Williams", "bob@example.com"));
        save(new Member("Charlie", "Davis", "charlie@example.com"));

        log.info("MemberRepository initialized with {} members.", STORE.size());
    }

    public Member save(Member member) {
        if (member.getId() == null) {
            member.setId(ID_SEQ.getAndIncrement());
        }
        STORE.put(member.getId(), member);
        return member;
    }

    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(STORE.get(id));
    }

    public Optional<Member> findByEmail(String email) {
        return STORE.values().stream()
                .filter(m -> m.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public boolean existsById(Long id) {
        return STORE.containsKey(id);
    }
}
