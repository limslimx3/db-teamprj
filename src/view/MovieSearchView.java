package view;

import dao.impl.MovieDAOImpl;
import domain.Movie;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MovieSearchView extends JFrame {
    private JTextField titleField;
    private JTextField directorField;
    private JTextField actorField;
    private JTextField genreField;
    private JButton searchButton;
    private JPanel moviePanel;
    private JScrollPane scrollPane;
    private int columns = 3;  // 초기 열 개수

    public MovieSearchView() {
        // 검색 패널 생성
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridBagLayout());
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        searchPanel.add(new JLabel("Title:"), gbc);

        titleField = new JTextField(15);
        gbc.gridx = 1;
        searchPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        searchPanel.add(new JLabel("Director:"), gbc);

        directorField = new JTextField(15);
        gbc.gridx = 1;
        searchPanel.add(directorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        searchPanel.add(new JLabel("Actor:"), gbc);

        actorField = new JTextField(15);
        gbc.gridx = 1;
        searchPanel.add(actorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        searchPanel.add(new JLabel("Genre:"), gbc);

        genreField = new JTextField(15);
        gbc.gridx = 1;
        searchPanel.add(genreField, gbc);

        searchButton = new JButton("Search");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        searchPanel.add(searchButton, gbc);

        add(searchPanel, BorderLayout.NORTH);

        // 영화 목록 패널 생성
        moviePanel = new JPanel();
        moviePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        moviePanel.setBackground(new Color(245, 245, 245));

        scrollPane = new JScrollPane(moviePanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(245, 245, 245));
        add(scrollPane, BorderLayout.CENTER);

        // 검색 버튼 클릭 이벤트 설정
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String director = directorField.getText();
                String actor = actorField.getText();
                String genre = genreField.getText();
                searchMovies(title, director, actor, genre);
            }
        });

        // 윈도우 리스너 추가
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                adjustGridLayout();
            }
        });

        // 초기 레이아웃 설정
        adjustGridLayout();

        // 기본 설정
        setTitle("Movie Search");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void adjustGridLayout() {
        int width = scrollPane.getViewport().getWidth();
        int newColumns = Math.max(1, width / 350);  // 350은 각 패널의 예상 너비와 간격을 포함한 값
        if (newColumns != columns) {
            columns = newColumns;
            updateMoviePanelLayout();
        }
    }

    private void updateMoviePanelLayout() {
        moviePanel.setLayout(new GridLayout(0, columns, 10, 10));
        moviePanel.revalidate();
    }

    private void searchMovies(String title, String director, String actor, String genre) {
        MovieDAOImpl movieDAO = new MovieDAOImpl();
        List<Movie> movies = movieDAO.searchMovies(title, director, actor, genre);
        moviePanel.removeAll();

        for (Movie movie : movies) {
            moviePanel.add(createMoviePanel(movie));
        }

        // 빈 패널로 남은 공간을 채우기
        int totalMovies = movies.size();
        int remainder = totalMovies % columns;
        if (remainder != 0) {
            int fillCount = columns - remainder;
            for (int i = 0; i < fillCount; i++) {
                moviePanel.add(createEmptyPanel());
            }
        }

        moviePanel.revalidate();
        moviePanel.repaint();
    }

    private JPanel createMoviePanel(Movie movie) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));
        panel.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        panel.setBackground(Color.WHITE);

        // 이미지 로드 및 설정
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            URL url = new URL(movie.getImageUrl());
            BufferedImage img = ImageIO.read(url);
            Image scaledImg = img.getScaledInstance(150, 225, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImg));
        } catch (IOException e) {
            e.printStackTrace();
            imageLabel.setIcon(null);
        }

        // 이미지와 텍스트 사이의 간격을 넓히기 위해 빈 패널 추가
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        imagePanel.setBackground(Color.WHITE);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        panel.add(imagePanel, BorderLayout.NORTH);

        // 텍스트 설정
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        textPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(titleLabel);

        JLabel directorLabel = new JLabel("Director: " + movie.getDirector());
        directorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        directorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(directorLabel);

        JLabel genreLabel = new JLabel("Genre: " + movie.getGenre());
        genreLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        genreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(genreLabel);

        JLabel actorLabel = new JLabel("Actors: " + movie.getActor());
        actorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        actorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(actorLabel);

        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEmptyPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }
}
