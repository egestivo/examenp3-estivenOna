package ec.edu.espe.lab2ci.service;

import ec.edu.espe.lab2ci.dto.LoanResponse;
import ec.edu.espe.lab2ci.model.Loan;
import ec.edu.espe.lab2ci.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.junit.jupiter.api.Assertions;

public class LoanServiceTest {
    private LoanRepository loanRepository;
    private RiskClient riskClient;
    private GenerateCode generateCode;
    // Clase real a probar
    private LoanService loanService;
    @BeforeEach
    public void setUp() {
        riskClient = Mockito.mock(RiskClient.class);
        generateCode = Mockito.mock(GenerateCode.class);
        loanRepository = Mockito.mock(LoanRepository.class);

        // CREAMOS EL SERVICIO
        loanService = new LoanService(loanRepository, riskClient, generateCode);
    }

    @Test
    @DisplayName("UT 1 - ESTIVEN OÑA")
    void createLoanValidDataShouldCreateSaveAndReturnCode(){
        System.out.println("UT 1- ESTIVEN OÑA");
        // ARRANGE ------
        String email = "estivenona@espe.edu.ec";
        String codigoEquipo = "COMP-123";
        Integer dias = 3;

        // Simular que el cliente no está bloqueado
        Mockito.when(riskClient.isBlocked(email)).thenReturn(false);

        // Simular que el servicio de confirmación genera un código
        Mockito.when(generateCode.generateCode()).thenReturn("COMP-123");

        // Simular el comportamiento del repository
        Mockito.when(loanRepository.save(ArgumentMatchers.any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT -------
        LoanResponse response = loanService.createLoan(codigoEquipo, email, dias);

        // ASSERT ------
        Assertions.assertNotNull(response.getLoanId(), "El orderId no debe ser nulo");
        Assertions.assertEquals(codigoEquipo, response.getApprovalCode(), "El código debe coincidir");

        // ASSERT (interacciones)
        // Verificar que se consulto el fraude
        Mockito.verify(riskClient).isBlocked(email);

        // Verificar que se guardo el pedido
        Mockito.verify(loanRepository).save(ArgumentMatchers.any(Loan.class));

        // Verificar que se genero el codigo de confirmacion
        Mockito.verify(generateCode).generateCode();
    }
    @Test
    @DisplayName("UT 2 - ESTIVEN OÑA")
    void createLoaninvalidEmailshouldThrowExceptionandNotCallDependencies() {
        System.out.println("UT 2- ESTIVEN OÑA");
        // ARRANGE
        String invalidEmail = "estiven.ona.espe.edu.ec";
        String codigoEquipo = "COMP-123";
        Integer dias = 3;

        // ACT + ASSERT
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> loanService.createLoan(codigoEquipo, invalidEmail, dias), "Debe lanzar IllegalArgumentException por email invalido");
        Assertions.assertEquals("Email no válido", exception.getMessage());

        // Asegurarse de no llamar dependencias
        Mockito.verifyNoInteractions(riskClient, generateCode, loanRepository);
    }

    @Test
    @DisplayName("UT3 - ESTIVEN OÑA")
    void createLoaDaysOutOfBoundsShouldThrowExceptionAndNoInteractions() {
        System.out.println("UT 4- ESTIVEN OÑA");
        // Arrange
        String email = "estiven.ona@espe.edu.ec";
        String equipo = "COMP-123";
        Integer dias = 16;

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> loanService.createLoan(equipo, email, dias), "Debe lanzar IllegalArgumentException por equipo ya prestado");

        Assertions.assertEquals("Fuera del rango de días permitido", exception.getMessage());

        // Si está bloqueado no debe guardar ni generar confirmación, ni verificar si el quipo está prestado
        Mockito.verifyNoInteractions(riskClient, generateCode, loanRepository);
    }

    @Test
    @DisplayName("UT4 - ESTIVEN OÑA")
    void createLoanBorrowedDeviceShouldThrowExceptionAndNoSave() {
        System.out.println("UT 4- ESTIVEN OÑA");
        // Arrange
        String email = "blocked.estivenona@espe.edu.ec";
        String equipo = "COMP-123";
        Integer dias = 3;

        // Simular que el cliente no está bloqueado
        Mockito.when(loanRepository.isBorrowed(equipo)).thenReturn(true);

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> loanService.createLoan(equipo, email, dias), "Debe lanzar IllegalArgumentException por equipo ya prestado");

        Assertions.assertEquals("Equipo reservado", exception.getMessage());

        // Assert Interacciones
        Mockito.verify(loanRepository).isBorrowed(equipo);

        // Si está bloqueado no debe guardar ni generar confirmación
        Mockito.verify(riskClient, Mockito.never()).isBlocked(email);
        Mockito.verify(generateCode, Mockito.never()).generateCode();
    }
}
