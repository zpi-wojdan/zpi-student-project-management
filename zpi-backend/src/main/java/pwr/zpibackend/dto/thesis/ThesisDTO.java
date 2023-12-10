package pwr.zpibackend.dto.thesis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThesisDTO {
    @Schema(description = "Name of the thesis in Polish.", example = "Temat pracy")
    private String namePL;

    @Schema(description = "Name of the thesis in English.", example = "Thesis topic")
    private String nameEN;

    @Schema(description = "Description of the thesis in Polish.", example = "Opis pracy")
    private String descriptionPL;

    @Schema(description = "Description of the thesis in English.", example = "Thesis description")
    private String descriptionEN;

    @Schema(description = "Maximum number of people that can work on the thesis.", example = "4")
    private Integer numPeople;

    @Schema(description = "Employee who is the supervisor of the thesis.")
    private Long supervisorId;

    @Schema(description = "List of programs for which the thesis is available.", example = "[1, 2]")
    private List<Long> programIds;

    @Schema(description = "Study cycle for which the thesis is available.", example = "1")
    private Optional<Long> studyCycleId;

    @Schema(description = "Status of the thesis.", example = "1")
    private Long statusId;

    @Schema(description = "List of students working on the thesis.", example = "[123456, 234567]")
    private List<String> studentIndexes;
}
