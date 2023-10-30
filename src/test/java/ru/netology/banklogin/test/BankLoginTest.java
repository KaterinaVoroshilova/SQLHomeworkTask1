package ru.netology.banklogin.test;

import org.junit.jupiter.api.*;
import ru.netology.banklogin.data.DataHelper;
import ru.netology.banklogin.data.SQLHelper;
import ru.netology.banklogin.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static ru.netology.banklogin.data.SQLHelper.cleanAuthCodes;
import static ru.netology.banklogin.data.SQLHelper.cleanDatabase;

public class BankLoginTest {
    LoginPage loginPage;

    @AfterEach
    void tearDown() {
        cleanAuthCodes();
    }
    /*@AfterAll
    static void tearDownAll() {
        cleanDatabase();
    }*/
    @BeforeEach
    void setUp() {
        loginPage = open("http://localhost:9999", LoginPage.class);
    }
    @Test
    @DisplayName("Should successfully login to dashboard with exist login and password from SUT test data")
    void shouldSuccessfulLogin() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    @Test
    @DisplayName("Should get error message if user is not exist in database")
    void shouldGetErrorMessageWithRandomUserIsNotExistInDatabase() {
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotification("Ошибка! \nНеверно указан логин или пароль");
    }

    @Test
    @DisplayName("Should get error message if user exist in database and active and random verification code")
    void shouldGetErrorMessageWithExistUserAndRandomVerificationCode() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotification("Ошибка! \nНеверно указан код! Попробуйте ещё раз.");
    }

    @Test
    @DisplayName("Should get error message of blocked user if login exist in database and random password 3 times")
    void shouldGetErrorMessageBlockedUsersWithExistLoginRandomPasswordThreeTimes() {
        var login = DataHelper.getAuthInfoWithTestData().getLogin();
        var password = DataHelper.generateRandomUser().getPassword();
        var authInfo = new DataHelper.AuthInfo(login, password);
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotification("Ошибка! \nНеверно указан логин или пароль");
        loginPage.clean();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotification("Ошибка! \nНеверно указан логин или пароль");
        loginPage.clean();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotification("Ошибка! Пользователь заблокирован");
    }
}
