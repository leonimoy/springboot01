package com.global.account;

import com.global.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
  boolean existsByEmail(String email);
  boolean existsByNickName(String nickname);
}
