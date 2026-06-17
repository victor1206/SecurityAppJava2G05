package esfe.presentacion;

import esfe.dominio.Users;
import esfe.persistencia.UserDAO;
import esfe.utils.CBOption;
import esfe.utils.CUD;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserWriteFrom extends JDialog {
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JComboBox cboStatus;
    private JButton btnOK;
    private JButton btnCancelar;
    private JPanel frmWriteForm;
    private JLabel lbPassword;

    private UserDAO userDAO;
    private CUD cud;
    private Users en;


    public UserWriteFrom(CUD cud, Users user) {
        this.cud = cud;
        this.en = user;
        userDAO = new UserDAO();
        setContentPane(frmWriteForm);
        setModal(true);
        init();
        pack();
        setLocationRelativeTo(null);

        btnCancelar.addActionListener(s-> this.dispose());
        btnOK.addActionListener(s -> ok());
    }

    private void init() {
        initCBStatus();

        switch (this.cud) {
            case CREATE:
                setTitle("Crear Usuario");
                btnOK.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Usuario");
                btnOK.setText("Guardar");
                break;
            case DELETE:
                setTitle("Eliminar Usuario");
                btnOK.setText("Eliminar");
                break;
        }
        setValuesControls(this.en);
    }

    private void initCBStatus() {
        DefaultComboBoxModel<CBOption> model =
                (DefaultComboBoxModel<CBOption>) cboStatus.getModel();
        model.addElement(new CBOption("ACTIVO", (byte) 1));
        model.addElement(new CBOption("INACTIVO", (byte) 2));
    }

    private void setValuesControls(Users users) {
        txtNombre.setText(users.getName());
        txtEmail.setText(users.getEmail());
        cboStatus.setSelectedItem(new CBOption(null, users.getStatus()));

        if (this.cud == CUD.CREATE) {
            cboStatus.setSelectedItem(new CBOption(null, 1));
        }

        if (this.cud == CUD.DELETE) {
            txtNombre.setEditable(false);//readonly
            txtEmail.setEditable(false);
            cboStatus.setEnabled(false);//bloqueado
        }

        if (this.cud != CUD.CREATE) {
            txtPassword.setVisible(false);
            lbPassword.setVisible(false);
        }
    }

    private boolean getValuesControl() {
        boolean res = false;

        CBOption selectedOption = (CBOption) cboStatus.getSelectedItem();
        byte status = selectedOption != null ? (byte) (selectedOption.getValue()) : (byte) 0;

        if (txtNombre.getText().trim().isEmpty()) {
            return res;
        } else if (txtEmail.getText().trim().isEmpty()) {
            return res;
        } else if (status == (byte) 0) {
            return res;
        } else if (this.cud != CUD.CREATE && this.en.getId() == 0) {
            return res;
        }

        res = true;
        this.en.setName(txtNombre.getText());
        this.en.setEmail(txtEmail.getText());
        this.en.setStatus(status);

        if (this.cud == CUD.CREATE) {
            this.en.setPasswordHash(new String(txtPassword.getPassword()));
            if (this.en.getPasswordHash().trim().isEmpty()) {
                return false;
            }
        }
        return res;
    }

    private void ok()
    {
        try
        {
            boolean res = getValuesControl();

            if(res)
            {
                boolean r = false;
                switch (this.cud)
                {
                    case CREATE:
                        Users user = userDAO.create(this.en);
                        if(user.getId() > 0)
                        {
                            r = true;
                        }
                        break;
                    case UPDATE:
                        r = userDAO.update(this.en);
                        break;
                    case DELETE:
                        r = userDAO.delete(this.en);
                        break;
                }
                if(r)
                {
                    JOptionPane.showMessageDialog(null,
                            "Transaccion realizada exitosamente",
                            "Informacion", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                }
                else
                {
                    JOptionPane.showMessageDialog(null,
                            "No se logro realizar ningun accion",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            else
            {
                JOptionPane.showMessageDialog(null,
                        "Los campos en * son obligatorios",
                        "Validacion", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}
