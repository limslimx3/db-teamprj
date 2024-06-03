package view;

import dao.BookingDAO;
import dao.TheaterDAO;
import dao.impl.BookingDAOImpl;
import dao.impl.TheaterDAOImpl;
import domain.Schedule;
import domain.Seat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BookingView extends JFrame {
    private Long movieId;
    private JComboBox<String> theaterComboBox;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> timeComboBox;
    private JComboBox<Integer> peopleComboBox;
    private JPanel seatPanel;
    private int selectedSeatsCount = 0;
    private int maxSeats;
    private List<JButton> seatButtons;
    private List<String> selectedSeats;
    private TheaterDAO theaterDAO;
    private BookingDAO bookingDAO;
    private int memberId;
    private int scheduleId;

    public BookingView(Long movieId, int memberId) {
        this.movieId = movieId;
        this.memberId = memberId;
        theaterDAO = new TheaterDAOImpl();
        bookingDAO = new BookingDAOImpl();
        selectedSeats = new ArrayList<>();

        // UI 구성
        setTitle("영화 예매");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        JPanel selectionPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        selectionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        selectionPanel.add(new JLabel("상영관:"));
        theaterComboBox = new JComboBox<>();
        loadTheaters();
        theaterComboBox.addActionListener(e -> loadSchedules());
        selectionPanel.add(theaterComboBox);

        selectionPanel.add(new JLabel("날짜:"));
        dayComboBox = new JComboBox<>();
        selectionPanel.add(dayComboBox);

        selectionPanel.add(new JLabel("시간:"));
        timeComboBox = new JComboBox<>();
        selectionPanel.add(timeComboBox);

        selectionPanel.add(new JLabel("인원수:"));
        peopleComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        peopleComboBox.addActionListener(e -> {
            maxSeats = (Integer) peopleComboBox.getSelectedItem();
            loadSeats(Integer.parseInt(theaterComboBox.getSelectedItem().toString()), scheduleId);  // 리로드하여 예약된 좌석을 비활성화
        });
        selectionPanel.add(peopleComboBox);

        add(selectionPanel, BorderLayout.NORTH);

        seatPanel = new JPanel();
        seatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(seatPanel, BorderLayout.CENTER);
        seatButtons = new ArrayList<>();

        JButton bookButton = new JButton("예매하기");
        bookButton.addActionListener(e -> createBooking());
        add(bookButton, BorderLayout.SOUTH);

        maxSeats = (Integer) peopleComboBox.getSelectedItem();
    }

    private void loadTheaters() {
        List<String> theaters = theaterDAO.getTheaters(movieId);
        theaterComboBox.removeAllItems();
        for (String theater : theaters) {
            theaterComboBox.addItem(theater);
        }
    }

    private void loadSchedules() {
        String theaterId = (String) theaterComboBox.getSelectedItem();
        List<Schedule> schedules = theaterDAO.getSchedules(movieId, theaterId);
        dayComboBox.removeAllItems();
        timeComboBox.removeAllItems();
        for (Schedule schedule : schedules) {
            dayComboBox.addItem(schedule.getDay());
            timeComboBox.addItem(schedule.getTime());
        }
        if (!schedules.isEmpty()) {
            scheduleId = schedules.get(0).getScheduleId();
            loadSeats(Integer.parseInt(theaterId), scheduleId);
        }
    }

    private void loadSeats(int theaterId, int scheduleId) {
        List<Seat> seats = theaterDAO.getSeats(theaterId, scheduleId);
        seatPanel.removeAll();
        seatButtons.clear();
        seatPanel.setLayout(new GridLayout(5, 5, 5, 5));
        for (Seat seat : seats) {
            JButton seatButton = new JButton(seat.getSeatNumber());
            seatButton.setEnabled(seat.isAvailable());
            seatButton.addActionListener(new SeatButtonActionListener());
            seatButtons.add(seatButton);
            seatPanel.add(seatButton);
        }
        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private void createBooking() {
        if (selectedSeatsCount == maxSeats) {
            int theaterId = Integer.parseInt((String) theaterComboBox.getSelectedItem());
            bookingDAO.createBooking(selectedSeats, theaterId, scheduleId, "Credit Card", "Paid", 150.00, memberId);
            JOptionPane.showMessageDialog(this, "예매완료!!!");
            loadSeats(theaterId, scheduleId);
        } else {
            JOptionPane.showMessageDialog(this, "Please select " + maxSeats + " seats.");
        }
    }

    private class SeatButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton seatButton = (JButton) e.getSource();
            if (selectedSeatsCount < maxSeats && seatButton.isEnabled()) {
                seatButton.setBackground(Color.GREEN);
                seatButton.setEnabled(false);
                selectedSeats.add(seatButton.getText());
                selectedSeatsCount++;
            }
        }
    }
}
