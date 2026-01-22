package com.tms.JwtSecurity.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.tms.JwtSecurity.bean.UserBean;
import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.otp.repository.UserOtpRepository;  
import com.tms.branch.entity.BranchEntity;
import com.tms.branch.repository.BranchRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.service.FilterCriteriaService;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepo;
    
    @Autowired
    private BranchRepository branchRepository;
    
    @Autowired
    private UserOtpRepository userOtpRepository;  
    
    @Autowired
    private FilterCriteriaService<User> filterCriteriaService;

    public UserServiceImpl(UserRepository userRepo) {
        super();
        this.userRepo = userRepo;
    }

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                UserDetails user = userRepo.findByEmail(username).orElse(null);
                if (user != null) {
                    return user;
                }

                user = userOtpRepository.findByMobileNumber(username).orElse(null);
                if (user != null) {
                    return user;
                }
                
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
        };
    }

    @Override
    public List<UserBean> filterUsers(List<FilterCriteriaBean> filters, int limit) {
        try {
            @SuppressWarnings("unchecked")
            List<User> filteredUsers = (List<User>) filterCriteriaService.getListOfFilteredData(User.class, filters, limit);
            return filteredUsers.stream().map(this::convertToBean).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error filtering users: " + e.getMessage(), e);
        }
    }

    @Override
    public UserBean updateUser(UserBean bean) {
        User existingUser = userRepo.findByUserId(bean.getUserId());
        existingUser.setFirstName(bean.getFirstName());
        existingUser.setSecondName(bean.getSecondName());
        existingUser.setEmail(bean.getEmail());
        existingUser.setBranchIds(bean.getBranchIds());
        existingUser.setRole(bean.getRole());
        if (bean.getPassword() != null && !bean.getPassword().isEmpty()) {
            existingUser.setPassword(new BCryptPasswordEncoder().encode(bean.getPassword()));
        }
        User updated = userRepo.save(existingUser);
        return convertToBean(updated);
    }

    @Override
    public void deleteUser(String userId) {
        User existingUser = userRepo.findByUserId(userId);
        if (existingUser == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        userRepo.delete(existingUser);
    }

    private UserBean convertToBean(User user) {
        UserBean bean = new UserBean();
        String branchName = null;
        try {
            BranchEntity branchEntity = branchRepository.findById(user.getBranchIds()).orElse(null); 
            if (branchEntity != null) {
                branchName = branchEntity.getBranchName();
            }
        } catch (Exception e) {
            branchName = "Unknown Branch";
        }
        bean.setUserId(user.getUserId());
        bean.setFirstName(user.getFirstName());
        bean.setSecondName(user.getSecondName());
        bean.setEmail(user.getEmail());
        bean.setRole(user.getRole());
        bean.setBranchIds(user.getBranchIds());
        bean.setPassword(user.getPassword());
        bean.setBranchName(branchName);
        return bean;
    }

    @Override
    public UserBean getUserById(String userId) {
        User user = userRepo.findByUserId(userId);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        return convertToBean(user);
    }
}