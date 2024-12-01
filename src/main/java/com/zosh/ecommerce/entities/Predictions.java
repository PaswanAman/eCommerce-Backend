package com.zosh.ecommerce.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "predictions")
public class Predictions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "file_path")
    private String filePath;
    @Column (name = "person_id")
    private int personId;
    private float confidence;
    private String predicted_class;
    private String email;
}
