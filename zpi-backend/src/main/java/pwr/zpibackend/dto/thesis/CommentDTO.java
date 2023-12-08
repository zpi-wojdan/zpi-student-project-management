package pwr.zpibackend.dto.thesis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private String content;
    private Long authorId;
    private Long thesisId;
}
