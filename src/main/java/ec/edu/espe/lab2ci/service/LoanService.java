package ec.edu.espe.lab2ci.service;

import ec.edu.espe.lab2ci.dto.LoanResponse;
import ec.edu.espe.lab2ci.model.Loan;
import ec.edu.espe.lab2ci.repository.LoanRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoanService {
    // inyectar dependencias
    private final LoanRepository loanRepository;
    private final RiskClient riskClient;
    private final GenerateCode generateCode;

    LoanResponse createLoan (String equipmentCode, String borrowerEmail, int loanDays){

        // validar el código no nulo ni vacío
        if (equipmentCode.isEmpty() || equipmentCode == null || equipmentCode == ""){
            throw new IllegalArgumentException("El código no puede ser nulo");
        }
        // validar el correo con formato válido
        if(borrowerEmail == null || !borrowerEmail.contains("@")){
            throw new IllegalArgumentException("Email no válido");
        }

        // validar de 1 a 15 días
        if(loanDays < 1 || loanDays > 15){
            throw new IllegalArgumentException("Fuera del rango de días permitido");
        }

        // verificar que el equipo no esté prestado
        if(loanRepository.isBorrowed(equipmentCode)){
            throw new IllegalArgumentException("Equipo reservado");
        }

        // verificar que el estudiante no esté restringido
        if(riskClient.isBlocked(borrowerEmail)){
            throw new IllegalArgumentException("Estudiante restringido");
        }
        // Si pasa las validaciones, se crea el pedido
        Loan prestamo = new Loan(equipmentCode, borrowerEmail, loanDays);
        Loan save = loanRepository.save(prestamo);

        // código de confirmación
        String code = generateCode.generateCode();

        // respuesta del DTO
        return new LoanResponse(save.getId(), code);
    }
}
