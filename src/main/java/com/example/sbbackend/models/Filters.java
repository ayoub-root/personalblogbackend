package com.example.sbbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"label", "type"})})
public class Filters {
    @Id
    @GeneratedValue
    private Long id;

    private String label;
    private  String type;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;
    private Date createdAt;
    private Date updatedAt;


}
