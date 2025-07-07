package com.madimadica.voidrelics.auth;

import com.madimadica.voidrelics.account.UserAccount;
import com.madimadica.voidrelics.account.UserAccountDao;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserService implements UserDetailsService {
    private final UserAccountDao userAccountDao;

    public CustomUserService(UserAccountDao userAccountDao) {
        this.userAccountDao = userAccountDao;
    }

    @Override
    public CustomUser loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount user = (username.contains("@")
                ? userAccountDao.findByEmail(username)
                : userAccountDao.findByUsername(username)
        ).orElseThrow(() -> new UsernameNotFoundException("No user found with username [" + username + "]"));
        return user.toCustomUser();
    }
}
