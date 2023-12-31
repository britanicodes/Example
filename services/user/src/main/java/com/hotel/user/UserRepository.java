package com.hotel.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    // User addFoodLogEntry(String email, String date, String foodName, int quantity);
}
