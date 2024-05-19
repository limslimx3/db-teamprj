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
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // 상영관, 상영 요일, 상영 시간, 인원 선택 패널
        JPanel selectionPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        selectionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 상영관 선택
        selectionPanel.add(new JLabel("Theater:"));
        theaterComboBox = new JComboBox<>();
        loadTheaters();
        theaterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSchedules();
            }
        });
        selectionPanel.add(theaterComboBox);

        // 상영 요일 선택
        selectionPanel.add(new JLabel("Day:"));
        dayComboBox = new JComboBox<>();
        selectionPanel.add(dayComboBox);

        // 상영 시간 선택
        selectionPanel.add(new JLabel("Time:"));
        timeComboBox = new JComboBox<>();
        selectionPanel.add(timeComboBox);

        // 인원 선택
        selectionPanel.add(new JLabel("People:"));
        peopleComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        peopleComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maxSeats = (Integer) peopleComboBox.getSelectedItem();
                resetSeats();
            }
        });
        selectionPanel.add(peopleComboBox);

        add(selectionPanel, BorderLayout.NORTH);

        // 좌석 선택 패널
        seatPanel = new JPanel();
        seatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(seatPanel, BorderLayout.CENTER);

        // 좌석 버튼 초기화
        seatButtons = new ArrayList<>();

        // 예매 버튼
        JButton bookButton = new JButton("Book Now");
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookSeats();
            }
        });
        add(bookButton, BorderLayout.SOUTH);

        // 초기 설정
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
        seatPanel.setLayout(new GridLayout(5, 5, 5, 5)); // 임의의 크기. 실제 좌석 배치에 맞게 변경 가능
        seatButtons.clear();
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

    private void resetSeats() {
        selectedSeatsCount = 0;
        selectedSeats.clear();
        for (JButton seatButton : seatButtons) {
            if (seatButton.isEnabled()) {
                seatButton.setBackground(null);
            }
        }
    }

    private void bookSeats() {
        if (selectedSeatsCount == maxSeats) {
            int theaterId = Integer.parseInt((String) theaterComboBox.getSelectedItem());
            bookingDAO.bookSeats(selectedSeats, theaterId, scheduleId, "Credit Card", "Paid", 150.00, memberId); // 결제 정보를 예시로 삽입
            JOptionPane.showMessageDialog(this, "Booking confirmed!");
            loadSeats(theaterId, scheduleId); // 좌석 상태를 갱신
        } else {
            JOptionPane.showMessageDialog(this, "Please select " + maxSeats + " seats.");
        }
    }

    private class SeatButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedSeatsCount < maxSeats) {
                JButton seatButton = (JButton) e.getSource();
                seatButton.setBackground(Color.GREEN);
                seatButton.setEnabled(false);
                selectedSeats.add(seatButton.getText());
                selectedSeatsCount++;
            }
        }
    }
}
