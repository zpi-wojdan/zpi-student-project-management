package pwr.zpibackend.dto.thesis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    @Schema(description = "Content of the comment.", example = "This is a comment.")
    private String content;

    @Schema(description = "Employee who created the comment.", example = "1")
    private Long authorId;

    @Schema(description = "Thesis to which the comment belongs.", example = "1")
    private Long thesisId;
}
