package com.tms.JwtSecurity.repository;

import java.util.Optional;

//import org.apache.el.stream.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.JwtSecurity.entity.Role;
import com.tms.JwtSecurity.entity.User;



@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	public Optional<User> findByEmail(String email);
	public User findByRole(Role role);
}
