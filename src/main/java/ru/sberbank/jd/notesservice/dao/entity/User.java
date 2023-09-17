package ru.sberbank.jd.notesservice.dao.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.UUID;

/**
 * User - POJO-class для таблицы T_USER.
 */
@Entity
@Table(name = "T_USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "telegram_login")
    private String telegramLogin;

    @Column(name = "admin_flag")
    private Boolean adminFlag;

    @Transient
    private String roles;

    @Column(name = "blocked_flag")
    private Boolean blocked;

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public User setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public User setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getTelegramLogin() {
        return telegramLogin;
    }

    public User setTelegramLogin(String telegramLogin) {
        this.telegramLogin = telegramLogin;
        return this;
    }

    public Boolean getAdminFlag() {
        return adminFlag;
    }

    public User setAdminFlag(Boolean adminFlag) {
        this.adminFlag = adminFlag;
        return this;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public String toString() {
        return "User{"
                + "userId=" + id
                + ", login='" + login + '\''
                + ", password='" + password + '\''
                + ", userName='" + name + '\''
                + ", telegramLogin='" + telegramLogin + '\''
                + ", adminFlag=" + adminFlag
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;
        if (id != null ? !id.equals(user.id) : user.id != null) {
            return false;
        } else {
            return true;
        }
    }

}
