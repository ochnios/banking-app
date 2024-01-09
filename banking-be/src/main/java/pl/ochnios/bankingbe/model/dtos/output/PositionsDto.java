package pl.ochnios.bankingbe.model.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PositionsDto {

    private int[] positions;
}
