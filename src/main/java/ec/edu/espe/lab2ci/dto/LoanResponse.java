package ec.edu.espe.lab2ci.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoanResponse {
    private String loanId;
    private String approvalCode;
}
