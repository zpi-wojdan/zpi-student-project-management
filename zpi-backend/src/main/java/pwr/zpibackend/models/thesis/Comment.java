package pwr.zpibackend.models.thesis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pwr.zpibackend.models.user.Employee;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Unique identifier of the comment.", example = "1")
    private Long id;

    @JoinColumn(name="author_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @Schema(description = "Employee who created the comment.")
    private Employee author;

    @Column(name = "content", nullable = false)
    @Schema(description = "Content of the comment.", example = "This is a comment.")
    private String content;

    @Column(name = "creation_time", nullable = false)
    @Schema(description = "Time when the comment was created.", example = "2024-01-01 12:00:00")
    private LocalDateTime creationTime;

    @JoinColumn(name="thesis_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    @JsonIgnore
    private Thesis thesis;
}
