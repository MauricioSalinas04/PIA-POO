/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Classes.PanelRoundPersonalizedO;
import com.sun.awt.AWTUtilities;
import Main.TextPrompt;
import java.awt.CardLayout;
import java.awt.Frame;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import org.apache.commons.validator.routines.EmailValidator;

public class Login extends javax.swing.JFrame {
    
    //Coordenadas Mouse
    int x, y;
    
    //Variable Controlador Cards
    CardLayout cardLayout;
    
    public Login() {
        initComponents();
        setLocationRelativeTo(null);
        AWTUtilities.setWindowOpaque(this, false);

        //Login Text
        new TextPrompt("Correo Electronico", fieldEmailLogin);
        new TextPrompt("Contraseña", fieldPassword);
        
        //Sign Up Text
        new TextPrompt("Usuario", fieldNewUser);
        new TextPrompt("Correo Electronico", fieldEmail);
        new TextPrompt("Contraseña", fieldNewPassword);

        change(loginCard);
    }
    
    /* JFRAME FUNCTIONS */
    private void change(PanelRoundPersonalizedO targetCard){
        cleanFields();
        Cards.removeAll();
        Cards.add(targetCard);
        Cards.repaint();
        Cards.revalidate();
    }
    
    private void cleanFields(){
        //Login
        fieldEmailLogin.setText("");
        fieldPassword.setText("");
        
        //Sign up
        fieldNewUser.setText("");
        fieldEmail.setText("");
        fieldNewPassword.setText("");
    }
    
    /* LOGIN FUNCTIONS */
    private boolean checkEmailLogin(){
        String userInput = fieldEmailLogin.getText();
        
        if(userInput.isEmpty()){
            JOptionPane.showMessageDialog(null, "Correo Electronico vacio");
            return false; // Por lo tanto es invalido. primero llenar campo.
        }
        
        EmailValidator validator = EmailValidator.getInstance();
        if (!validator.isValid(userInput)) {           
            JOptionPane.showMessageDialog(null, "La dirección de correo tiene un formato inválido.");
            return false;
        }
        
        return true;
    }
    
    private boolean checkPassword(){
        String password = fieldPassword.getText();
        
        if(password.isEmpty()){
            System.out.println("Campo Password vacio");
            JOptionPane.showMessageDialog(null, "Contraseña vacia");
            return false; // Por lo tanto es invalido. primero llenar campo.
        }
        
        return true;
    }
    
    private void login(){
        if(!checkEmailLogin()){ // Si no tiene permiso para registar, retorna.
            return;
        }
        
        if(!checkPassword()){ // Si no cumple contraseña, retorna 
            return;
        }
        
        try {
            Connection con = Conexion.getConexion();
            
            // Consulta para buscar un usuario por su nombre de usuario y contraseña
            String consulta = "SELECT * FROM administradores WHERE email = ? AND contraseña = ?";
            PreparedStatement ps = con.prepareStatement(consulta);
            
            
            ps.setString(1, fieldEmailLogin.getText());
            ps.setString(2, fieldPassword.getText());
        
            ResultSet resultado = ps.executeQuery();
            
            if (resultado.next()) {
                // Usuario encontrado, puede continuar con la lógica de inicio de sesión
                JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso");
                
                String nombre = resultado.getString("nombre");
                int matricula = resultado.getInt("id");
                
                Home.matriculaUsuario = matricula;
                Home.nombreUsuario = nombre;
                
                //Continuar
                Home home = new Home();
                home.setVisible(true);
                this.dispose();
            } else {
                // Usuario no encontrado, mostrar mensaje de error o tomar una acción correspondiente
                JOptionPane.showMessageDialog(null, "Datos incorrectos");
            }
            
            resultado.close();
            ps.close();
            con.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }
    //
    
    /* SIGN UP FUNCTIONS */
    private boolean checkNewUserName(){
        String userInput = fieldNewUser.getText();
        
        if(userInput.isEmpty()){
            System.out.println("Campo Username vacio");
            JOptionPane.showMessageDialog(null, "Nombre de usuario vacio");
            return false; // Por lo tanto es invalido. primero llenar campo.
        }
        
        return true;
    }
    
    private boolean checkEmail(){
        String emailAddress = fieldEmail.getText();
        
        if(emailAddress.isEmpty()){
            JOptionPane.showMessageDialog(null, "Correo electronico vacio");
            return false; // Por lo tanto es invalido. primero llenar campo.
        }
        
        EmailValidator validator = EmailValidator.getInstance();
        if (!validator.isValid(emailAddress)) {           
            JOptionPane.showMessageDialog(null, "La dirección de correo tiene un formato inválido.");
            return false;
        }
        
        // Consulta a la base de datos
        if (verifyExistsInDatabase(emailAddress)) {
            JOptionPane.showMessageDialog(null, "Correo Electronico existente.");
            return false; // Por lo tanto, no esta permitido registraar
        } 
        
        return true;
    }
    
    private boolean checkNewPassword(){
        String password = fieldNewPassword.getText();
        
        if(password.isEmpty()){
            JOptionPane.showMessageDialog(null, "Contraseña vacia.");
            return false; // Por lo tanto es invalido. primero llenar campo.
        }
        
        return true;
    }
    
    private boolean verifyExistsInDatabase(String inputElement){
        
        try {
            Connection con = Conexion.getConexion();
            
            // Consulta para buscar un usuario por su nombre de usuario y contraseña
            String consulta = "SELECT * FROM administradores WHERE email = ?";
            PreparedStatement ps = con.prepareStatement(consulta);
                      
            ps.setString(1, inputElement);            
        
            ResultSet resultado = ps.executeQuery();
            
            if (resultado.next()) {
                // Email existente
                return true;
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
        
        return false;
    }
    
    private void signUp(){
        if(!checkNewUserName()){ // Si no tiene permiso para registar, retorna.
            return;
        }
        
        if(!checkEmail()){ // Si no es un email valido, retorna.
            return;
        }
        
        if(!checkNewPassword()){ // Si no cumple contraseña, retorna 
            return;
        }
        
        // Encriptar contraseña
        
        // Dar de alta en base de datos
        String nombre = fieldNewUser.getText();
        String email = fieldEmail.getText();
        String contraseña = fieldNewPassword.getText();                   
       
        try {
            Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement("INSERT INTO administradores (nombre, email, contraseña, activo) VALUES (?,?,?,?)");
            ps.setString(1, nombre);
            ps.setString(2, email);
            ps.setString(3, contraseña);
            ps.setInt(4, 1);
            ps.executeUpdate();
      
            JOptionPane.showMessageDialog(null, "Registro Exitoso");
            change(loginCard);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRound2 = new Classes.PanelRound();
        jLabel1 = new javax.swing.JLabel();
        panelRoundPersonalizedO4 = new Classes.PanelRoundPersonalizedO();
        button1 = new Classes.Button();
        button2 = new Classes.Button();
        Cards = new Classes.PanelRoundPersonalizedO();
        loginCard = new Classes.PanelRoundPersonalizedO();
        panelRound1 = new Classes.PanelRound();
        fieldEmailLogin = new javax.swing.JTextField();
        panelRoundPersonalizedO1 = new Classes.PanelRoundPersonalizedO();
        jPanel3 = new javax.swing.JPanel();
        panelRound3 = new Classes.PanelRound();
        fieldPassword = new javax.swing.JPasswordField();
        panelRoundPersonalizedO3 = new Classes.PanelRoundPersonalizedO();
        jPanel5 = new javax.swing.JPanel();
        loginButton = new Classes.Button();
        textButtonLogin = new javax.swing.JLabel();
        panelRound5 = new Classes.PanelRound();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        button5 = new Classes.Button();
        jLabel3 = new javax.swing.JLabel();
        signUpCard = new Classes.PanelRoundPersonalizedO();
        panelRound4 = new Classes.PanelRound();
        fieldNewUser = new javax.swing.JTextField();
        checkNewUsername = new Classes.PanelRoundPersonalizedO();
        jPanel6 = new javax.swing.JPanel();
        panelRound6 = new Classes.PanelRound();
        fieldNewPassword = new javax.swing.JPasswordField();
        checkNewPassword = new Classes.PanelRoundPersonalizedO();
        jPanel8 = new javax.swing.JPanel();
        registerData = new Classes.Button();
        jLabel5 = new javax.swing.JLabel();
        panelRound7 = new Classes.PanelRound();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        panelRound8 = new Classes.PanelRound();
        fieldEmail = new javax.swing.JTextField();
        checkEmail = new Classes.PanelRoundPersonalizedO();
        jPanel7 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(520, 450));
        setUndecorated(true);
        setResizable(false);

        panelRound2.setBackground(new java.awt.Color(33, 33, 33));
        panelRound2.setBorderThickness(0.0);

        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jLabel1MouseDragged(evt);
            }
        });
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel1MousePressed(evt);
            }
        });

        panelRoundPersonalizedO4.setBackground(new java.awt.Color(205, 217, 115));
        panelRoundPersonalizedO4.setPreferredSize(new java.awt.Dimension(78, 37));
        panelRoundPersonalizedO4.setRoundTopLeft(25);
        panelRoundPersonalizedO4.setRoundTopRight(25);

        button1.setBackground(new java.awt.Color(242, 5, 25));
        button1.setPreferredSize(new java.awt.Dimension(25, 25));
        button1.setSelectedColor(new java.awt.Color(89, 2, 2));
        button1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout button1Layout = new javax.swing.GroupLayout(button1);
        button1.setLayout(button1Layout);
        button1Layout.setHorizontalGroup(
            button1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );
        button1Layout.setVerticalGroup(
            button1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        button2.setBackground(new java.awt.Color(44, 217, 17));
        button2.setPreferredSize(new java.awt.Dimension(25, 25));
        button2.setSelectedColor(new java.awt.Color(15, 80, 6));
        button2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout button2Layout = new javax.swing.GroupLayout(button2);
        button2.setLayout(button2Layout);
        button2Layout.setHorizontalGroup(
            button2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );
        button2Layout.setVerticalGroup(
            button2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelRoundPersonalizedO4Layout = new javax.swing.GroupLayout(panelRoundPersonalizedO4);
        panelRoundPersonalizedO4.setLayout(panelRoundPersonalizedO4Layout);
        panelRoundPersonalizedO4Layout.setHorizontalGroup(
            panelRoundPersonalizedO4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRoundPersonalizedO4Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        panelRoundPersonalizedO4Layout.setVerticalGroup(
            panelRoundPersonalizedO4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRoundPersonalizedO4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelRoundPersonalizedO4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Cards.setBackground(new java.awt.Color(33, 33, 33));
        Cards.setRoundBottomLeft(25);
        Cards.setRoundBottomRight(25);
        Cards.setLayout(new java.awt.CardLayout());

        loginCard.setBackground(new java.awt.Color(242, 238, 216));
        loginCard.setRoundBottomLeft(25);
        loginCard.setRoundBottomRight(25);

        panelRound1.setBackground(new java.awt.Color(255, 255, 255));
        panelRound1.setBorderThickness(6.0);
        panelRound1.setCornerRadius(40.0);
        panelRound1.setPreferredSize(new java.awt.Dimension(338, 48));

        fieldEmailLogin.setFont(new java.awt.Font("Century Gothic", 1, 22)); // NOI18N
        fieldEmailLogin.setBorder(null);
        fieldEmailLogin.setPreferredSize(new java.awt.Dimension(340, 30));
        fieldEmailLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldEmailLoginActionPerformed(evt);
            }
        });

        panelRoundPersonalizedO1.setRoundBottomRight(25);
        panelRoundPersonalizedO1.setRoundTopRight(25);

        jPanel3.setBackground(new java.awt.Color(33, 33, 33));
        jPanel3.setPreferredSize(new java.awt.Dimension(5, 30));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelRoundPersonalizedO1Layout = new javax.swing.GroupLayout(panelRoundPersonalizedO1);
        panelRoundPersonalizedO1.setLayout(panelRoundPersonalizedO1Layout);
        panelRoundPersonalizedO1Layout.setHorizontalGroup(
            panelRoundPersonalizedO1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRoundPersonalizedO1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 27, Short.MAX_VALUE))
        );
        panelRoundPersonalizedO1Layout.setVerticalGroup(
            panelRoundPersonalizedO1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(panelRound1);
        panelRound1.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(fieldEmailLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelRoundPersonalizedO1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fieldEmailLogin, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(panelRoundPersonalizedO1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelRound3.setBackground(new java.awt.Color(255, 255, 255));
        panelRound3.setBorderThickness(6.0);
        panelRound3.setCornerRadius(40.0);
        panelRound3.setPreferredSize(new java.awt.Dimension(338, 48));

        fieldPassword.setFont(new java.awt.Font("Century Gothic", 1, 22)); // NOI18N
        fieldPassword.setBorder(null);
        fieldPassword.setPreferredSize(new java.awt.Dimension(340, 30));

        panelRoundPersonalizedO3.setRoundBottomRight(25);
        panelRoundPersonalizedO3.setRoundTopRight(25);

        jPanel5.setBackground(new java.awt.Color(33, 33, 33));
        jPanel5.setPreferredSize(new java.awt.Dimension(5, 30));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelRoundPersonalizedO3Layout = new javax.swing.GroupLayout(panelRoundPersonalizedO3);
        panelRoundPersonalizedO3.setLayout(panelRoundPersonalizedO3Layout);
        panelRoundPersonalizedO3Layout.setHorizontalGroup(
            panelRoundPersonalizedO3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRoundPersonalizedO3Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 27, Short.MAX_VALUE))
        );
        panelRoundPersonalizedO3Layout.setVerticalGroup(
            panelRoundPersonalizedO3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelRound3Layout = new javax.swing.GroupLayout(panelRound3);
        panelRound3.setLayout(panelRound3Layout);
        panelRound3Layout.setHorizontalGroup(
            panelRound3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound3Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(fieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelRoundPersonalizedO3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound3Layout.setVerticalGroup(
            panelRound3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRound3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelRoundPersonalizedO3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fieldPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addContainerGap())
        );

        loginButton.setBackground(new java.awt.Color(255, 242, 0));
        loginButton.setBorderThickness(5.0);
        loginButton.setCornerRadius(45.0);
        loginButton.setPreferredSize(new java.awt.Dimension(100, 50));
        loginButton.setSelectedColor(new java.awt.Color(193, 183, 0));
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loginButtonMouseClicked(evt);
            }
        });

        textButtonLogin.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        textButtonLogin.setForeground(new java.awt.Color(27, 7, 56));
        textButtonLogin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        textButtonLogin.setText("Continuar");

        javax.swing.GroupLayout loginButtonLayout = new javax.swing.GroupLayout(loginButton);
        loginButton.setLayout(loginButtonLayout);
        loginButtonLayout.setHorizontalGroup(
            loginButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textButtonLogin, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addContainerGap())
        );
        loginButtonLayout.setVerticalGroup(
            loginButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textButtonLogin, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelRound5.setBackground(new java.awt.Color(242, 99, 204));
        panelRound5.setBorderThickness(0.0);
        panelRound5.setCornerRadius(0.0);

        jPanel1.setBackground(new java.awt.Color(33, 33, 33));
        jPanel1.setPreferredSize(new java.awt.Dimension(0, 6));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );

        jLabel4.setFont(new java.awt.Font("Century Gothic", 1, 48)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(27, 7, 56));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Iniciar Sesion");

        javax.swing.GroupLayout panelRound5Layout = new javax.swing.GroupLayout(panelRound5);
        panelRound5.setLayout(panelRound5Layout);
        panelRound5Layout.setHorizontalGroup(
            panelRound5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelRound5Layout.setVerticalGroup(
            panelRound5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        button5.setBackground(new java.awt.Color(102, 153, 255));
        button5.setBorderThickness(5.0);
        button5.setCornerRadius(35.0);
        button5.setPreferredSize(new java.awt.Dimension(100, 50));
        button5.setSelectedColor(new java.awt.Color(15, 95, 255));
        button5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button5MouseClicked(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(27, 7, 56));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("No tienes cuenta?");

        javax.swing.GroupLayout button5Layout = new javax.swing.GroupLayout(button5);
        button5.setLayout(button5Layout);
        button5Layout.setHorizontalGroup(
            button5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(button5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                .addContainerGap())
        );
        button5Layout.setVerticalGroup(
            button5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(button5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout loginCardLayout = new javax.swing.GroupLayout(loginCard);
        loginCard.setLayout(loginCardLayout);
        loginCardLayout.setHorizontalGroup(
            loginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRound5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginCardLayout.createSequentialGroup()
                .addContainerGap(162, Short.MAX_VALUE)
                .addGroup(loginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginCardLayout.createSequentialGroup()
                        .addComponent(loginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(163, 163, 163))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginCardLayout.createSequentialGroup()
                        .addComponent(button5, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(147, 147, 147))))
            .addGroup(loginCardLayout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addGroup(loginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelRound3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        loginCardLayout.setVerticalGroup(
            loginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginCardLayout.createSequentialGroup()
                .addComponent(panelRound5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelRound3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(loginButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button5, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        Cards.add(loginCard, "card2");

        signUpCard.setBackground(new java.awt.Color(242, 238, 216));
        signUpCard.setRoundBottomLeft(25);
        signUpCard.setRoundBottomRight(25);

        panelRound4.setBackground(new java.awt.Color(255, 255, 255));
        panelRound4.setBorderThickness(6.0);
        panelRound4.setCornerRadius(40.0);
        panelRound4.setPreferredSize(new java.awt.Dimension(338, 48));

        fieldNewUser.setFont(new java.awt.Font("Century Gothic", 1, 22)); // NOI18N
        fieldNewUser.setBorder(null);
        fieldNewUser.setPreferredSize(new java.awt.Dimension(340, 30));
        fieldNewUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldNewUserActionPerformed(evt);
            }
        });

        checkNewUsername.setRoundBottomRight(25);
        checkNewUsername.setRoundTopRight(25);

        jPanel6.setBackground(new java.awt.Color(33, 33, 33));
        jPanel6.setPreferredSize(new java.awt.Dimension(5, 30));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout checkNewUsernameLayout = new javax.swing.GroupLayout(checkNewUsername);
        checkNewUsername.setLayout(checkNewUsernameLayout);
        checkNewUsernameLayout.setHorizontalGroup(
            checkNewUsernameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkNewUsernameLayout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 27, Short.MAX_VALUE))
        );
        checkNewUsernameLayout.setVerticalGroup(
            checkNewUsernameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelRound4Layout = new javax.swing.GroupLayout(panelRound4);
        panelRound4.setLayout(panelRound4Layout);
        panelRound4Layout.setHorizontalGroup(
            panelRound4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(fieldNewUser, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkNewUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound4Layout.setVerticalGroup(
            panelRound4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRound4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkNewUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fieldNewUser, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelRound6.setBackground(new java.awt.Color(255, 255, 255));
        panelRound6.setBorderThickness(6.0);
        panelRound6.setCornerRadius(40.0);
        panelRound6.setPreferredSize(new java.awt.Dimension(338, 48));

        fieldNewPassword.setFont(new java.awt.Font("Century Gothic", 1, 22)); // NOI18N
        fieldNewPassword.setBorder(null);
        fieldNewPassword.setPreferredSize(new java.awt.Dimension(340, 30));

        checkNewPassword.setRoundBottomRight(25);
        checkNewPassword.setRoundTopRight(25);

        jPanel8.setBackground(new java.awt.Color(33, 33, 33));
        jPanel8.setPreferredSize(new java.awt.Dimension(5, 30));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout checkNewPasswordLayout = new javax.swing.GroupLayout(checkNewPassword);
        checkNewPassword.setLayout(checkNewPasswordLayout);
        checkNewPasswordLayout.setHorizontalGroup(
            checkNewPasswordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkNewPasswordLayout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 27, Short.MAX_VALUE))
        );
        checkNewPasswordLayout.setVerticalGroup(
            checkNewPasswordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelRound6Layout = new javax.swing.GroupLayout(panelRound6);
        panelRound6.setLayout(panelRound6Layout);
        panelRound6Layout.setHorizontalGroup(
            panelRound6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound6Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(fieldNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkNewPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound6Layout.setVerticalGroup(
            panelRound6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRound6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(checkNewPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fieldNewPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addContainerGap())
        );

        registerData.setBackground(new java.awt.Color(255, 242, 0));
        registerData.setBorderThickness(5.0);
        registerData.setCornerRadius(45.0);
        registerData.setPreferredSize(new java.awt.Dimension(100, 50));
        registerData.setSelectedColor(new java.awt.Color(204, 192, 0));
        registerData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                registerDataMouseClicked(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(27, 7, 56));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Continuar");

        javax.swing.GroupLayout registerDataLayout = new javax.swing.GroupLayout(registerData);
        registerData.setLayout(registerDataLayout);
        registerDataLayout.setHorizontalGroup(
            registerDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registerDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addContainerGap())
        );
        registerDataLayout.setVerticalGroup(
            registerDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registerDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelRound7.setBackground(new java.awt.Color(242, 99, 204));
        panelRound7.setBorderThickness(0.0);
        panelRound7.setCornerRadius(0.0);
        panelRound7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(33, 33, 33));
        jPanel2.setPreferredSize(new java.awt.Dimension(0, 6));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );

        panelRound7.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 94, 530, -1));

        jLabel6.setFont(new java.awt.Font("Century Gothic", 1, 48)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(27, 7, 56));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Registrate!");
        panelRound7.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 280, 88));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/iconmonstr-arrow-left-alt-filled-48.png"))); // NOI18N
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        panelRound7.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, -1, 30));

        panelRound8.setBackground(new java.awt.Color(255, 255, 255));
        panelRound8.setBorderThickness(6.0);
        panelRound8.setCornerRadius(40.0);
        panelRound8.setPreferredSize(new java.awt.Dimension(338, 48));

        fieldEmail.setFont(new java.awt.Font("Century Gothic", 1, 22)); // NOI18N
        fieldEmail.setBorder(null);
        fieldEmail.setPreferredSize(new java.awt.Dimension(340, 30));
        fieldEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldEmailActionPerformed(evt);
            }
        });

        checkEmail.setRoundBottomRight(25);
        checkEmail.setRoundTopRight(25);

        jPanel7.setBackground(new java.awt.Color(33, 33, 33));
        jPanel7.setPreferredSize(new java.awt.Dimension(5, 30));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout checkEmailLayout = new javax.swing.GroupLayout(checkEmail);
        checkEmail.setLayout(checkEmailLayout);
        checkEmailLayout.setHorizontalGroup(
            checkEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkEmailLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 27, Short.MAX_VALUE))
        );
        checkEmailLayout.setVerticalGroup(
            checkEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelRound8Layout = new javax.swing.GroupLayout(panelRound8);
        panelRound8.setLayout(panelRound8Layout);
        panelRound8Layout.setHorizontalGroup(
            panelRound8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound8Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(fieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound8Layout.setVerticalGroup(
            panelRound8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRound8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(checkEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fieldEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout signUpCardLayout = new javax.swing.GroupLayout(signUpCard);
        signUpCard.setLayout(signUpCardLayout);
        signUpCardLayout.setHorizontalGroup(
            signUpCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRound7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(signUpCardLayout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addGroup(signUpCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(signUpCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(signUpCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(panelRound6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelRound8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(signUpCardLayout.createSequentialGroup()
                            .addGap(85, 85, 85)
                            .addComponent(registerData, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(panelRound4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        signUpCardLayout.setVerticalGroup(
            signUpCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(signUpCardLayout.createSequentialGroup()
                .addComponent(panelRound7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(panelRound4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelRound8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelRound6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(registerData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        Cards.add(signUpCard, "card2");

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Cards, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(panelRound2Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelRoundPersonalizedO4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelRoundPersonalizedO4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Cards, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRound2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRound2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MousePressed
        x = evt.getX();
        y = evt.getY();
    }//GEN-LAST:event_jLabel1MousePressed

    private void jLabel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseDragged
        this.setLocation(this.getLocation().x + evt.getX() - x, this.getLocation().y + evt.getY() - y);
    }//GEN-LAST:event_jLabel1MouseDragged

    //Cerrar ventana
    private void button1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button1MouseClicked
        dispose();
    }//GEN-LAST:event_button1MouseClicked

    //Minimizar ventana
    private void button2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button2MouseClicked
        setState(Frame.ICONIFIED);
    }//GEN-LAST:event_button2MouseClicked

    private void fieldNewUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldNewUserActionPerformed
        checkNewUserName();
    }//GEN-LAST:event_fieldNewUserActionPerformed

    
    private void fieldEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldEmailActionPerformed

    private void registerDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_registerDataMouseClicked
        signUp();
    }//GEN-LAST:event_registerDataMouseClicked

    private void button5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button5MouseClicked
        change(signUpCard);
    }//GEN-LAST:event_button5MouseClicked

    private void loginButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginButtonMouseClicked
        login();
    }//GEN-LAST:event_loginButtonMouseClicked

    private void fieldEmailLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldEmailLoginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldEmailLoginActionPerformed

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        change(loginCard);
    }//GEN-LAST:event_jLabel2MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Classes.PanelRoundPersonalizedO Cards;
    private Classes.Button button1;
    private Classes.Button button2;
    private Classes.Button button5;
    private Classes.PanelRoundPersonalizedO checkEmail;
    private Classes.PanelRoundPersonalizedO checkNewPassword;
    private Classes.PanelRoundPersonalizedO checkNewUsername;
    private javax.swing.JTextField fieldEmail;
    private javax.swing.JTextField fieldEmailLogin;
    private javax.swing.JPasswordField fieldNewPassword;
    private javax.swing.JTextField fieldNewUser;
    private javax.swing.JPasswordField fieldPassword;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private Classes.Button loginButton;
    private Classes.PanelRoundPersonalizedO loginCard;
    private Classes.PanelRound panelRound1;
    private Classes.PanelRound panelRound2;
    private Classes.PanelRound panelRound3;
    private Classes.PanelRound panelRound4;
    private Classes.PanelRound panelRound5;
    private Classes.PanelRound panelRound6;
    private Classes.PanelRound panelRound7;
    private Classes.PanelRound panelRound8;
    private Classes.PanelRoundPersonalizedO panelRoundPersonalizedO1;
    private Classes.PanelRoundPersonalizedO panelRoundPersonalizedO3;
    private Classes.PanelRoundPersonalizedO panelRoundPersonalizedO4;
    private Classes.Button registerData;
    private Classes.PanelRoundPersonalizedO signUpCard;
    private javax.swing.JLabel textButtonLogin;
    // End of variables declaration//GEN-END:variables
}
