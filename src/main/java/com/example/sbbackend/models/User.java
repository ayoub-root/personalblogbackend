package com.example.sbbackend.models;

import com.example.sbbackend.dto.Role;
import com.example.sbbackend.dto.SavedItemDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstname;

    private String lastname;

    private String avatar;
    @Column(unique = true)
    private String email;
    private String mobile;

    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    private String birthday;

    private String address;

    private Boolean confirmed;

    private String passwordToken;

    private LocalDateTime resetTokenExpiry;

    private String confirmationToken;

    private LocalDateTime confirmationTokenExpiry;

    private String bio;

    private Date createdAt;

    private Date updatedAt;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "saved_items",joinColumns = @JoinColumn(name = "user_id"))
    private List<SavedItems> savedItems;


    // Utility methods for manipulating saved items
    public void addSavedItem(SavedItems savedItem) {
//      SavedItems dd=new SavedItems();
//      dd.setType("ssss");
//      dd.setId(1);
//      dd.setValue("ddddd");
//      dd.setTitle("dddddfffff");
        savedItems.add(savedItem);
    }

    public void removeSavedItem(SavedItems savedItem) {
        savedItems.remove(savedItem);
    }
    public SavedItems getSavedItemByType(String type) {
        return savedItems.stream()
                .filter(item -> item.getType().equals(type))
                .findFirst()
                .orElse(null);
    }
    public SavedItems getSavedItemByValue(String value) {
        return savedItems.stream()
                .filter(item -> item.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
