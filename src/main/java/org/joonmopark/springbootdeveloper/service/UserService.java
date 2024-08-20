package org.joonmopark.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import org.joonmopark.springbootdeveloper.domain.User;
import org.joonmopark.springbootdeveloper.dto.AddUserRequest;
import org.joonmopark.springbootdeveloper.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("해당 이메일을 사용하는 사용자가 존재하지 않습니다."));
    }

    public User save(AddUserRequest dto){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build());
    }

    public User findByUserId(Long userId){
        return userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("존재하지 않는 유저아이디 입니다."));
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 입니다."));
    }
}
