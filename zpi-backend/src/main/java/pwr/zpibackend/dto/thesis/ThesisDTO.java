package pwr.zpibackend.dto.thesis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThesisDTO {
    private String namePL;
    private String nameEN;
    private String descriptionPL;
    private String descriptionEN;
    private Integer numPeople;
    private Long supervisorId;
    private List<Long> programIds;
    private Optional<Long> studyCycleId;
    private Long statusId;
    private List<String> studentIndexes;
}
