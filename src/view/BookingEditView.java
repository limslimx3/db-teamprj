package view;

import dao.BookingDAO;
import dao.MovieDAO;
import dao.TheaterDAO;
import dao.impl.BookingDAOImpl;
import dao.impl.MovieDAOImpl;
import dao.impl.TheaterDAOImpl;
import domain.Booking;
import domain.Movie;
import domain.Schedule;
import domain.Seat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BookingEditView extends JFrame {
    private JComboBox<Movie> movieComboBox;
    private JComboBox<String> theaterComboBox;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> timeComboBox;
    private JComboBox<Integer> peopleComboBox;
    private JPanel seatPanel;
    private List<String> selectedSeats;
    private int selectedSeatsCount = 0;
    private int maxSeats;
    private List<JButton> seatButtons = new ArrayList<>();
    private MovieDAO movieDAO;
    private TheaterDAO theaterDAO;
    private BookingDAO bookingDAO;
    private int bookId;
    private int memberId;
    private int scheduleId;

    public BookingEditView(Booking booking) {
        this.bookId = booking.getId();
        this.memberId = booking.getMemberId();
        this.movieDAO = new MovieDAOImpl();
        this.theaterDAO = new TheaterDAOImpl();
        this.bookingDAO = new BookingDAOImpl();
        selectedSeats = new ArrayList<>();

        initializeUI();
        loadMovies();
    }

    private void initializeUI() {
        setTitle("영화 예매 수정");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel selectionPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        selectionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(selectionPanel, BorderLayout.NORTH);

        movieComboBox = new JComboBox<>();
        theaterComboBox = new JComboBox<>();
        dayComboBox = new JComboBox<>();
        timeComboBox = new JComboBox<>();
        peopleComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});

        selectionPanel.add(new JLabel("영화:"));
        selectionPanel.add(movieComboBox);
        movieComboBox.addActionListener(this::onMovieSelected);

        selectionPanel.add(new JLabel("상영관:"));
        selectionPanel.add(theaterComboBox);
        theaterComboBox.addActionListener(this::onTheaterSelected);

        selectionPanel.add(new JLabel("날짜:"));
        selectionPanel.add(dayComboBox);

        selectionPanel.add(new JLabel("시간:"));
        selectionPanel.add(timeComboBox);

        selectionPanel.add(new JLabel("인원수:"));
        selectionPanel.add(peopleComboBox);
        peopleComboBox.addActionListener(e -> {
            maxSeats = (Integer) peopleComboBox.getSelectedItem();
            resetSeats();
            if (theaterComboBox.getSelectedItem() != null && dayComboBox.getSelectedItem() != null && timeComboBox.getSelectedItem() != null) {
                loadSeats(Integer.parseInt(theaterComboBox.getSelectedItem().toString()), scheduleId);
            }
        });

        seatPanel = new JPanel(new GridLayout(5, 5, 5, 5));
        add(new JScrollPane(seatPanel), BorderLayout.CENTER);

        JButton updateButton = new JButton("예매 변경하기");
        updateButton.addActionListener(this::updateBooking);
        add(updateButton, BorderLayout.SOUTH);
    }

    private void loadMovies() {
        List<Movie> movies = movieDAO.getAllMovies();
        movieComboBox.removeAllItems();
        movies.forEach(movieComboBox::addItem);
    }

    private void onMovieSelected(ActionEvent e) {
        if (movieComboBox.getSelectedItem() != null) {
            Movie selectedMovie = (Movie) movieComboBox.getSelectedItem();
            loadTheaters(selectedMovie.getId());
        }
    }

    private void loadTheaters(Long movieId) {
        List<String> theaters = theaterDAO.getTheaters(movieId);
        theaterComboBox.removeAllItems();
        theaters.forEach(theaterComboBox::addItem);
    }

    private void onTheaterSelected(ActionEvent e) {
        if (theaterComboBox.getSelectedItem() != null) {
            loadSchedules();
        }
    }

    private void loadSchedules() {
        if (theaterComboBox.getSelectedItem() != null) {
            String theaterId = (String) theaterComboBox.getSelectedItem();
            List<Schedule> schedules = theaterDAO.getSchedules(((Movie) movieComboBox.getSelectedItem()).getId(), theaterId);
            dayComboBox.removeAllItems();
            timeComboBox.removeAllItems();
            schedules.forEach(schedule -> {
                dayComboBox.addItem(schedule.getDay());
                timeComboBox.addItem(schedule.getTime());
            });
            if (!schedules.isEmpty()) {
                scheduleId = schedules.get(0).getScheduleId();
                loadSeats(Integer.parseInt(theaterId), scheduleId);
            }
        }
    }

    private void loadSeats(int theaterId, int scheduleId) {
        List<Seat> seats = theaterDAO.getSeats(theaterId, scheduleId);
        seatPanel.removeAll();
        seatButtons.clear();
        seats.forEach(seat -> {
            JButton seatButton = new JButton(seat.getSeatNumber());
            seatButton.setEnabled(seat.isAvailable());
            seatButton.addActionListener(this::onSeatSelected);
            seatButtons.add(seatButton);
            seatPanel.add(seatButton);
        });
        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private void onSeatSelected(ActionEvent e) {
        JButton seatButton = (JButton) e.getSource();
        if (selectedSeatsCount < maxSeats) {
            seatButton.setBackground(Color.GREEN);
            seatButton.setEnabled(false);
            selectedSeats.add(seatButton.getText());
            selectedSeatsCount++;
        }
    }

    private void resetSeats() {
        selectedSeatsCount = 0;
        selectedSeats.clear();
        seatButtons.forEach(button -> {
            button.setBackground(null);
            button.setEnabled(true);
        });
    }

    private void updateBooking(ActionEvent e) {
        String theaterId = (String) theaterComboBox.getSelectedItem();
        List<Schedule> schedules = theaterDAO.getSchedules(((Movie) movieComboBox.getSelectedItem()).getId(), theaterId);

        String selectedDay = (String) dayComboBox.getSelectedItem();
        String selectedTime = (String) timeComboBox.getSelectedItem();

        Schedule schedule = schedules.stream()
                .filter(s -> s.getDay().equals(selectedDay) && s.getTime().equals(selectedTime))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("일치하는 상영일정이 없습니다."));
        int scheduleId = schedule.getScheduleId();

        System.out.println("theaterId = " + theaterId);
        System.out.println("scheduleId = " + scheduleId);

        String paymentMethod = "Credit Card";
        String paymentStatus = "Paid";
        double amount = 150.00;

        bookingDAO.updateBooking(bookId, theaterId, scheduleId, selectedSeats, paymentMethod, paymentStatus, amount);

        JOptionPane.showMessageDialog(this, "예매가 변경되었습니다.");
        dispose();
    }
}
