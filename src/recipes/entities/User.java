package recipes.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id = null;
    private String email;
    private String password;
    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Recipe> recipes = new ArrayList<>();

    public User(String email, String encryptedPassword) {
        this.email = email;
        this.password = encryptedPassword;
    }
}
