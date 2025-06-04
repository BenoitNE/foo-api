package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "foos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Foo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;
    // autres champs, createdDate, lastModifiedDate avec @CreatedDate, @LastModifiedDate si audit activé
}
