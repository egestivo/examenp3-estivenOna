package ec.edu.espe.lab2ci.repository;

import ec.edu.espe.lab2ci.model.Loan;

public interface LoanRepository {
    Loan save(Loan reserva);
    // verificar si el equipo está bloqueado
    boolean isBorrowed(String equipmentCode);
}
