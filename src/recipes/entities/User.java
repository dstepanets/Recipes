package recipes.entities;

import lombok.*;

import javax.persistence.*;

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

    public User(String email, String encryptedPassword) {
        this.email = email;
        this.password = encryptedPassword;
    }
}
