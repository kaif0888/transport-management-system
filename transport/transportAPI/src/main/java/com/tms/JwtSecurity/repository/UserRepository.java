package com.tms.JwtSecurity.repository;

import java.util.List;
import java.util.Optional;

//import org.apache.el.stream.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.JwtSecurity.entity.Role;
import com.tms.JwtSecurity.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	public Optional<User> findByEmail(String email);

	public User findByRole(Role role);

	public List<User> findByUserIdStartingWith(String fullPrefix);

	public User findByUserId(String userId);
	


	Optional<User> findByPasswordResetCode(String token);
}
