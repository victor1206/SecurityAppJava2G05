package esfe.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList; //List

import esfe.dominio.Users;
import esfe.utils.PasswordHasher;
//import esfe.utils.PasswordHash;

public class UserDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public UserDAO() {conn = ConnectionManager.getInstance();}

    public Users create(Users users) throws SQLException
    {
        Users user = null;
        try
        {
           PreparedStatement ps = conn.connect().prepareStatement(
                   "Insert into Users(name, passwordHash, email, status) " +
                           "values(?, ?, ?, ?)",
                   Statement.RETURN_GENERATED_KEYS
           );

           ps.setString(1, users.getName());
           ps.setString(2, PasswordHasher.hashPassword(users.getPasswordHash()));
           ps.setString(3, users.getEmail());
           ps.setByte(4, users.getStatus());

           int affectRows = ps.executeUpdate();

           if(affectRows != 0)
           {
               ResultSet generatedKeys = ps.getGeneratedKeys();

               if(generatedKeys.next())
               {
                   int idGenerado = generatedKeys.getInt(1);

                   user = getById(idGenerado);
               }
               else
               {
                   throw new SQLException("Error al crear el usuario, no se obtuvo el id");
               }
           }
           ps.close();
        }
        catch (SQLException ex)
        {
            throw new SQLException("Error al crear el usuario: "
                    + ex.getMessage(), ex);
        }
        finally {
            ps = null;
            conn.disconnect();
        }
        return user;
    }

    public boolean update(Users user) throws SQLException
    {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "Update Users " +
                    "set name = ?, email = ?, status = ? " +
                            "Where id = ?"

            );

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setByte(3, user.getStatus());
            ps.setInt(4, user.getId());

            if(ps.executeUpdate() > 0)
            {
                res = true;
            }
            ps.close();
        }
        catch (SQLException ex)
        {
            throw new SQLException("Error al modificar el usuario: "
                    + ex.getMessage(), ex);
        }
        finally {
            ps = null;
            conn.disconnect();
        }
        return res;
    }

    public boolean delete(Users user) throws SQLException
    {
        boolean res = false;
        try
        {
            ps = conn.connect().prepareStatement(
              "Delete From Users Where id = ?"
            );

            ps.setInt(1, user.getId());

            if (ps.executeUpdate() > 0)
            {
                res = true;
            }
            ps.close();
        }
        catch (SQLException ex)
        {
            throw new SQLException("Error al eliminar el usuario: "
                    + ex.getMessage(), ex);
        }
        finally {
            ps = null;
            conn.disconnect();
        }
        return res;
    }

    public ArrayList<Users> search(Users user) throws SQLException
    {
        ArrayList<Users> list = new ArrayList<>();
        try
        {
            ps = conn.connect().prepareStatement(
                  "Select id, name, email, status " +
                          "From Users " +
                          "Where name like ?"
            );

            ps.setString(1, "%" + user.getName() + "%");

            rs = ps.executeQuery();

            while (rs.next())
            {
                Users us = new Users();
                us.setId(rs.getInt(1));
                us.setName(rs.getString(2));
                us.setEmail(rs.getString(3));
                us.setStatus(rs.getByte(4));

                list.add(us);
            }
            ps.close();
            rs.close();
        }
        catch (SQLException ex)
        {
            throw new SQLException("Error al buscar el usuario: "
                    + ex.getMessage(), ex);
        }
        finally {
            ps = null;
            rs = null;
            conn.disconnect();
        }
        return list;
    }

    public Users getById(int id) throws SQLException
    {
        Users user = new Users();
        try
        {
          ps = conn.connect().prepareStatement(
                  "Select id, name, email, status " +
                          "From Users " +
                          "Where id = ?"
          );

          ps.setInt(1, id);

          rs = ps.executeQuery();

          if(rs.next())
          {
              user.setId(rs.getInt(1));
              user.setName(rs.getString(2));
              user.setEmail(rs.getString(3));
              user.setStatus(rs.getByte(4));
          }
          else
          {
              user = null;
          }

          ps.close();
          rs.close();
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener el usuario por id: "
                    + ex.getMessage(), ex);
        }
        finally {
            ps = null;
            rs = null;
            conn.disconnect();
        }
        return user;
    }

    public Users authenticate(Users user) throws SQLException
    {
        Users userAuthenticate = new Users();

        try
        {
            ps = conn.connect().prepareStatement(
              "Select id, name, email, status " +
                      "From Users " +
                      "Where email = ? And passwordHash = ? And status = 1"
            );

            ps.setString(1, user.getEmail());
            ps.setString(2, PasswordHasher.hashPassword(user.getPasswordHash()));

            rs = ps.executeQuery();

            if(rs.next())
            {
                userAuthenticate.setId(rs.getInt(1));
                userAuthenticate.setName(rs.getString(2));
                userAuthenticate.setEmail(rs.getString(3));
                userAuthenticate.setStatus(rs.getByte(4));
            }
            else
            {
                userAuthenticate = null;
            }
            ps.close();
            rs.close();
        }
        catch (SQLException ex)
        {
            throw new SQLException("Error al iniciar sesion del usuario: "
                    + ex.getMessage(), ex);
        }
        finally {
            ps = null;
            rs = null;
            conn.disconnect();
        }
        return userAuthenticate;
    }

    public boolean updatePassword(Users user) throws SQLException
    {
        boolean res = false;
        try
        {
            ps = conn.connect().prepareStatement(
                    "Update Users " +
                            "Set passwordHash = ? " +
                    "Where id = ?"
            );
            ps.setString(1, PasswordHasher.hashPassword(user.getPasswordHash()));
            ps.setInt(2, user.getId());

            if(ps.executeUpdate() > 0)
            {
                res = true;
            }
            ps.close();
        }
        catch (SQLException ex)
        {
            throw new SQLException("Error al modificar el password del usuario: "
                    + ex.getMessage(), ex);
        }
        finally {
            ps = null;
            conn.disconnect();
        }
        return res;
    }
}
