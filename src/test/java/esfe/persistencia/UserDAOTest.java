package esfe.persistencia;

import esfe.dominio.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp()
    {
        userDAO = new UserDAO();
    }

    @Test
    void create() throws SQLException {
        Users user = new Users(0, "admin23", "123456",
                "admin2233@gmail.com", (byte) 1);

        Users resultado = userDAO.create(user);
        assertNotEquals(resultado, null);
    }

    @Test
    void update() throws SQLException {
        Users user = new Users(4, "admin prueb mod", "",
                "admin2233@gmail.com", (byte) 2);

        boolean resultado = userDAO.update(user);
        assertNotEquals(resultado, null);
    }

    @Test
    void delete() throws SQLException {
        Users user = new Users(4, "", "",
                "", (byte) 2);

        boolean resultado = userDAO.delete(user);
        assertNotEquals(resultado, null);
    }

    @Test
    void search() {
    }

    @Test
    void getById() {
    }

    @Test
    void authenticate() {
    }

    @Test
    void updatePassword() {
    }
}