package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginView() {

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

        if (username.equals("root") && password.equals("1234")) {
            JOptionPane.showMessageDialog(this, "관리자 로그인 성공!");
            // 이후에 관리자 화면으로 이동하는 코드를 추가할 수 있습니다.
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password for admin");
        }
    }

    private void loginAsUser() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (username.equals("user1") && password.equals("user1")) {
            JOptionPane.showMessageDialog(this, "일반 사용자 로그인 성공! 영화 조회 화면으로 이동합니다");
            SwingUtilities.invokeLater(() -> new MovieSearchView().setVisible(true));
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password for user");
        }
    }
}
