package ec.edu.espe.lab2ci.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Loan {
    private String id;
    private String equipmentCode;
    private String borrowerEmail;
    private Integer loanDays;
    private String status;

    public Loan(String equipmentCode, String borrowerEmail, Integer loanDays) {
        this.id = UUID.randomUUID().toString();;
        this.equipmentCode = equipmentCode;
        this.borrowerEmail = borrowerEmail;
        this.loanDays = loanDays;
        this.status = status;
    }
}
