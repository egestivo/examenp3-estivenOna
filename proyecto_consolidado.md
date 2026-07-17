# 📂 ESTRUCTURA COMPLETA DEL PROYECTO

```text
.
├── .gitattributes
├── .github
│   └── workflows
│       └── ci.yml
├── .mvn
│   └── wrapper
│       └── maven-wrapper.properties
├── HELP.md
├── mvnw
├── mvnw.cmd
└── src
    ├── main
    │   ├── java
    │   │   └── ec
    │   │       └── edu
    │   │           └── espe
    │   │               └── lab2ci
    │   │                   ├── Lab2CiApplication.java
    │   │                   ├── dto
    │   │                   │   └── WalletResponse.java
    │   │                   ├── model
    │   │                   │   └── Wallet.java
    │   │                   ├── repository
    │   │                   │   └── WalletRepository.java
    │   │                   └── service
    │   │                       ├── RiskClient.java
    │   │                       └── WalletService.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── ec
                └── edu
                    └── espe
                        └── lab2ci
                            ├── Lab2CiApplicationTests.java
                            └── service
                                └── WalletServiceTest.java
```

---

# 📄 CONTENIDO DE LOS ARCHIVOS

## 📁 Archivo: `.github\workflows\ci.yml`

```yml
name: ci.yml
on:

jobs:

```

---

## 📁 Archivo: `.mvn\wrapper\maven-wrapper.properties`

```properties
wrapperVersion=3.3.4
distributionType=only-script
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.16/apache-maven-3.9.16-bin.zip

```

---

## 📁 Archivo: `src\main\java\ec\edu\espe\lab2ci\Lab2CiApplication.java`

```java
package ec.edu.espe.lab2ci;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Lab2CiApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lab2CiApplication.class, args);
    }

}

```

---

## 📁 Archivo: `src\main\java\ec\edu\espe\lab2ci\dto\WalletResponse.java`

```java
package ec.edu.espe.lab2ci.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WalletResponse {
    private final String walletId;
    private final double balance;
}

```

---

## 📁 Archivo: `src\main\java\ec\edu\espe\lab2ci\model\Wallet.java`

```java
package ec.edu.espe.lab2ci.model;


import lombok.Getter;

import java.util.UUID;

@Getter
public class Wallet {
    private final String id;
    private final String ownerEmail;
    private double balance;

    public Wallet(String ownerEmail, double balance) {
        this.id = UUID.randomUUID().toString();
        this.ownerEmail = ownerEmail;
        this.balance = balance;
    }

    // Deposirtas el dinero en la billetera
    public void deposit(double amount){
        this.balance += amount;
    }

    // Retirar dinero de la billetera
    public void withdraw(double amount){
        this.balance -= amount;
    }
}

```

---

## 📁 Archivo: `src\main\java\ec\edu\espe\lab2ci\repository\WalletRepository.java`

```java
package ec.edu.espe.lab2ci.repository;

import ec.edu.espe.lab2ci.model.Wallet;

import java.util.Optional;

public interface WalletRepository {
    Wallet save(Wallet wallet);
    Optional<Wallet> findById(String id);
    boolean existsByOwnerEmail(String ownerEmail);
}

```

---

## 📁 Archivo: `src\main\java\ec\edu\espe\lab2ci\service\RiskClient.java`

```java
package ec.edu.espe.lab2ci.service;

public interface RiskClient {
    boolean isBlocked(String ownerEmail);
}
```

---

## 📁 Archivo: `src\main\java\ec\edu\espe\lab2ci\service\WalletService.java`

```java
package ec.edu.espe.lab2ci.service;

import ec.edu.espe.lab2ci.dto.WalletResponse;
import ec.edu.espe.lab2ci.model.Wallet;
import ec.edu.espe.lab2ci.repository.WalletRepository;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final RiskClient riskClient;

    //Crear una billetera
    public WalletResponse createWallet(String ownerEmail, double initialBalance) {
        //Validaciones
        if (ownerEmail == null || !ownerEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance must be positive");
        }

        if (riskClient.isBlocked(ownerEmail)) {
            throw new IllegalArgumentException("User is blocked");
        }

        //Regla de negocio: no duplicar la billetera por email
        if (walletRepository.existsByOwnerEmail(ownerEmail)) {
            throw new IllegalArgumentException("Wallet already exists for this email");
        }

        Wallet wallet = new Wallet(ownerEmail, initialBalance);
        Wallet save = walletRepository.save(wallet);
        return new WalletResponse(save.getId(), save.getBalance());
    }

    // depositar dinero
    public double deposit(String walletId, double amount){
        if(amount <= 0){
            throw new IllegalArgumentException("Deposit ammount must be > 0");
        }

        Optional<Wallet> found = walletRepository.findById(walletId);
        if(found.isEmpty()){
            throw new IllegalStateException("Wallet not found");
        }

        Wallet wallet = found.get();
        wallet.deposit(amount);

        // persistencia
        walletRepository.save(wallet);

        return wallet.getBalance();

    }

    public double withdraw(String walletId, double amount){
        if(amount <= 0){
            throw new IllegalArgumentException("Withdraw amount must be > 0");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalStateException("Wallet not found"));

        if(wallet.getBalance() < amount ){
            throw new IllegalStateException("Insufficient funds");
        }

        wallet.withdraw(amount);
        walletRepository.save(wallet);

        return wallet.getBalance();
    }
}

```

---

## 📁 Archivo: `src\main\resources\application.properties`

```properties
spring.application.name=Lab2-CI

```

---

## 📁 Archivo: `src\test\java\ec\edu\espe\lab2ci\Lab2CiApplicationTests.java`

```java
package ec.edu.espe.lab2ci;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Lab2CiApplicationTests {

    @Test
    void contextLoads() {
    }

}

```

---

## 📁 Archivo: `src\test\java\ec\edu\espe\lab2ci\service\WalletServiceTest.java`

```java
package ec.edu.espe.lab2ci.service;

import ec.edu.espe.lab2ci.dto.WalletResponse;
import ec.edu.espe.lab2ci.model.Wallet;
import ec.edu.espe.lab2ci.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.util.AssertionErrors;

import java.util.Optional;

public class WalletServiceTest {
    private WalletService walletService;
    private RiskClient riskClient;
    private WalletRepository walletRepository;

    // arrange común de cada prueba
    @BeforeEach
    public void setUp(){
        walletRepository = Mockito.mock(WalletRepository.class);
        riskClient = Mockito.mock(RiskClient.class);
        walletService = new WalletService(walletRepository, riskClient);
    }

    // crear wallet valida y devolver respuesta
    @Test
    void createWalletWithValidDataShouldSaveAndReturnResponse(){
        // Arrange
        String email = "estiven.ona@espe.edu.ec";
        double balance = 150.00;

        Mockito.when(riskClient.isBlocked(email)).thenReturn(false);
        Mockito.when(walletRepository.existsByOwnerEmail(email)).thenReturn(false);
        Mockito.when(walletRepository.save(ArgumentMatchers.any(Wallet.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        //Act
        WalletResponse response = walletService.createWallet(email, balance);

        //Assert
        AssertionErrors.assertNotNull("Id Wallet no Debe Ser Null",response.getWalletId());
        Assertions.assertEquals(balance, response.getBalance());

        Mockito.verify(riskClient).isBlocked(email);
        Mockito.verify(walletRepository).existsByOwnerEmail(email);
        Mockito.verify(walletRepository).save(ArgumentMatchers.any(Wallet.class));
    }

    @Test
    void createWalletWithInvalidDataShouldThrowExceptionAndNotCallDependencies(){
        // Arrange
        String invalid = "estiven.ona-espe.edu.ec";
        double balance = 15.00;

        // Act + Assert
        Assertions.assertThrows(IllegalArgumentException.class, () ->
            walletService.createWallet(invalid, balance));

        //Verificar no interacción
        Mockito.verifyNoInteractions(riskClient, walletRepository);
    }

    @Test
    void depositWalletNotFoundShouldThrowException(){
        // Arrange
        String walletId = "no-existe";

        Mockito.when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act + Assert
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () ->
                walletService.deposit(walletId, 10.00));
        Assertions.assertEquals("Wallet not found", ex.getMessage());
        Mockito.verify(walletRepository).findById(walletId);
        Mockito.verify(walletRepository, Mockito.never()).save(ArgumentMatchers.any(Wallet.class));
    }

    @Test
    void depositShouldUpdateBalanceAndSaveUsingCaptor(){
        // Arrange
        Wallet wallet = new Wallet("estiven.ona@espe.edu.ec", 150.00);
        String walletId = wallet.getId();

        Mockito.when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        Mockito.when(walletRepository.save(ArgumentMatchers.any(Wallet.class))).thenAnswer(i ->
                i.getArguments()[0]);
        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);

        // Act
        double newBalance = walletService.deposit(walletId, 30.00);

        Mockito.verify(walletRepository).save(captor.capture());
        Wallet saved = captor.getValue();
        Assertions.assertEquals(newBalance, saved.getBalance());
    }

//    @Test
//    void () {
//
//    }

    @Test
    void withdrawWithInsufficientFundsShouldThrowExceptionAndNotSave(){
        // Arrange
        Wallet wallet = new Wallet("estiven.ona@espe.edu.ec", 150.00);
        String walletId = wallet.getId();

        Mockito.when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Act + Assert
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () ->
                walletService.withdraw(walletId, 1000.00));

        Assertions.assertEquals("Insufficient funds", ex.getMessage());
        Mockito.verify(walletRepository, Mockito.never()).save(ArgumentMatchers.any(Wallet.class));
    }
}

```

---

