package esfe.presentacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import esfe.dominio.Users;
import esfe.persistencia.UserDAO;
import esfe.utils.CUD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Objects;

public class UserReadingForm {
    private JTextField txtNombre;
    private JButton btnCrear;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JTable jTbUsers;
    private JPanel frmReadingForm;

    private UserDAO userDAO;

    public UserReadingForm() {

        userDAO = new UserDAO();
        txtNombre.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                search(txtNombre.getText());
            }
        });

        btnModificar.addActionListener( s -> {
            Users user = getUserFromTable();
            if(user != null)
            {
                UserWriteFrom userWriteFrom = new UserWriteFrom(CUD.UPDATE, user);
                userWriteFrom.setVisible(true);

                DefaultTableModel emptyModel = new DefaultTableModel();
                jTbUsers.setModel(emptyModel);
            }
        });
        btnCrear.addActionListener(s -> {
            UserWriteFrom userWriteFrom = new UserWriteFrom(CUD.CREATE, new Users());
            userWriteFrom.setVisible(true);

            DefaultTableModel emptyModel = new DefaultTableModel();
            jTbUsers.setModel(emptyModel);
        });


        btnEliminar.addActionListener(s -> {
            Users user = getUserFromTable();
            UserWriteFrom userWriteFrom = new UserWriteFrom(CUD.DELETE, user);
            userWriteFrom.setVisible(true);

            DefaultTableModel emptyModel = new DefaultTableModel();
            jTbUsers.setModel(emptyModel);
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("UserReadingForm");
        frame.setContentPane(new UserReadingForm().frmReadingForm);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private void search(String query)
    {
        try
        {
            Users user = new Users();
            user.setName(query);
            ArrayList<Users> users = userDAO.search(user);
            crearTable(users);
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void crearTable(ArrayList<Users> pUsers)
    {
        DefaultTableModel model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };

        model.addColumn("Id");//0
        model.addColumn("Mombre");//1
        model.addColumn("Email");//2
        model.addColumn("Status");//3

        this.jTbUsers.setModel(model);

        Object row[] = null;

        for (int i = 0; i < pUsers.size(); i++)
        {
            Users user = pUsers.get(i);

            model.addRow(row);

            model.setValueAt(user.getId(), i, 0);
            model.setValueAt(user.getName(), i, 1);
            model.setValueAt(user.getEmail(), i, 2);
            model.setValueAt(user.getStrStatus(), i, 3);
        }
        hideCol(0);
    }

    private void hideCol(int pColumna)
    {
        this.jTbUsers.getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.jTbUsers.getColumnModel().getColumn(pColumna).setMinWidth(0);

        this.jTbUsers.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(0);
        this.jTbUsers.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(0);
    }

    private Users getUserFromTable()
    {
        Users user = null;
        try
        {
            int filaSelect = this.jTbUsers.getSelectedRow();
            int id = 0;

            if(filaSelect != -1)
            {
                id = (int)this.jTbUsers.getValueAt(filaSelect, 0);
            }
            else
            {
                JOptionPane.showMessageDialog(null,
                        "Seleccione una fila de la tabla", "Validacion",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }

            user = userDAO.getById(id);

            if(user.getId() == 0)
            {
                JOptionPane.showMessageDialog(null,
                        "No se encontro ningun usuario.", "Validacion",
                        JOptionPane.WARNING_MESSAGE);
            }

            return user;
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error"
                    , JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
