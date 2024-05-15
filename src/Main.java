import view.MovieSearchView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MovieSearchView().setVisible(true));
    }
}