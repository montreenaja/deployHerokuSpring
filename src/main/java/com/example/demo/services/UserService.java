package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.User;
import com.example.demo.exceptions.UsernameAlreadyExistsException;
import com.example.demo.repositories.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCrypPasswordEncoder;
	
	public User saveUser(User newUser) {
		try {
			newUser.setPassword(bCrypPasswordEncoder.encode(newUser.getPassword()));
			newUser.setUsername(newUser.getUsername());
			newUser.setConfirmPassword("");
			return userRepository.save(newUser);
		}catch(Exception ex) {
			throw new UsernameAlreadyExistsException("Username'"+newUser.getUsername()+"' already exists");
		}		
	}
}
