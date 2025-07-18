package br.edu.ifba.inf008.shell.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Integer userId;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "registered_at")
  private LocalDateTime registeredAt;

  public User() {}

  public User(String name, String email) {
      this();
      this.name = name;
      this.email = email;
      this.registeredAt = LocalDateTime.now();
  }

  public User(Integer userId, String name, String email, LocalDateTime registeredAt) {
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.registeredAt = registeredAt;
  }

  public Integer getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDateTime getRegisteredAt() {
    return registeredAt;
  }

  public void setRegisteredAt(LocalDateTime registeredAt) {
    this.registeredAt = registeredAt;
  }

  


  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    User user = (User) obj;
    return Objects.equals(userId, user.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId);
  }

  @Override
  public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("User{");
      sb.append("userId=").append(userId);
      sb.append(", name=").append(name);
      sb.append(", email=").append(email);
      sb.append(", registeredAt=").append(registeredAt);
      sb.append("}");
      return sb.toString();
  }

}
