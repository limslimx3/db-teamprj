import javax.swing.*;

import view.MovieSearchView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel);

        usernameField = new JTextField();
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton adminButton = new JButton("Admin Login");
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginAsAdmin();
            }
        });
        panel.add(adminButton);

        JButton userButton = new JButton("User Login");
        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginAsUser();
            }
        });
        panel.add(userButton);

        add(panel);
    }

    private void loginAsAdmin() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        try {
            // DB 연결
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db1", "root","1234"); // JDBC 연결
            System.out.println("DB 연결 완료");

            // 관리자 로그인 처리
            if (username.equals("root") && password.equals("1234")) {
                JOptionPane.showMessageDialog(this, "Admin login successful");
                new AdminFrame(conn).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password for admin");
                conn.close(); // 연결 종료
            }

     
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
            e.printStackTrace();
        }
    }

    private void loginAsUser() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        try {
            // DB 연결
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db1", "user1","user1"); // JDBC 연결
            System.out.println("DB 연결 완료");

            // 사용자 로그인 처리
            if (username.equals("user1") && password.equals("user1")) {
                JOptionPane.showMessageDialog(this, "User login successful");
                
                SwingUtilities.invokeLater(() -> new MovieSearchView().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password for user");
            }

            conn.close(); // 연결 종료
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
            e.printStackTrace();
        }
    }
}

