package com.madimadica.voidrelics.account;

import com.madimadica.jdbc.web.PostgresJdbc;
import com.madimadica.jdbc.web.TypedJdbc;
import com.madimadica.voidrelics.auth.AuthRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserAccountDao {
    private final PostgresJdbc jdbc;
    private final TypedJdbc<UserAccount> typedJdbc;

    public UserAccountDao(PostgresJdbc jdbc) {
        this.jdbc = jdbc;
        this.typedJdbc = new TypedJdbc<>(jdbc, UserAccount.MAPPER);
    }

    public List<UserAccount> findAll() {
        return typedJdbc.query("SELECT * FROM user_account");
    }

    public Optional<UserAccount> findByUsername(String username) {
        return typedJdbc.queryOne("SELECT * FROM user_account WHERE LOWER(username) = LOWER(?)", username);
    }

    public Optional<UserAccount> findByEmail(String username) {
        return typedJdbc.queryOne("SELECT * FROM user_account WHERE LOWER(email) = LOWER(?)", username);
    }

    public List<UserAccount> findAllByUsernameOrEmail(String username, String email) {
        if (email == null) {
            return typedJdbc.query("SELECT * FROM user_account WHERE LOWER(username) = LOWER(?)", username);
        } else {
            return typedJdbc.query("SELECT * FROM user_account WHERE LOWER(username) = LOWER(?) OR LOWER(email) = LOWER(?)", username, email);
        }
    }

    public record Insert(String username, String email, String passwordHash, List<AuthRole> roles) {}

    @Transactional
    public UserAccount insert(Insert row) {
        return jdbc.insertInto("user_account")
                .value("username", row.username())
                .value("email", row.email())
                .value("password_hash", row.passwordHash())
                .value("roles_bitmask", AuthRole.writeToBitmask(row.roles()))
                .insertReturning(UserAccount.MAPPER);
    }
}
