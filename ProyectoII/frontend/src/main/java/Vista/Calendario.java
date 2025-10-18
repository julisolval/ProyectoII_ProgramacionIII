package main.java.Vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class Calendario extends JDialog {
    private JSpinner yearSpinner;
    private JComboBox<String> monthComboBox;
    private JPanel daysPanel;
    private Date selectedDate;
    private Calendar calendar;

    public Calendario(JFrame parent) {
        super(parent, "Seleccionar Fecha", true);
        calendar = Calendar.getInstance();
        initComponents();
        pack();
        setSize(400, 400);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setSelectedIndex(calendar.get(Calendar.MONTH));
        monthComboBox.addActionListener(e -> updateDaysPanel());
        topPanel.add(monthComboBox);

        SpinnerNumberModel yearModel = new SpinnerNumberModel(
                calendar.get(Calendar.YEAR),
                1900,
                2100,
                1
        );
        yearSpinner = new JSpinner(yearModel);

        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(yearSpinner, "####");
        yearSpinner.setEditor(editor);

        yearSpinner.addChangeListener(e -> updateDaysPanel());
        topPanel.add(yearSpinner);

        add(topPanel, BorderLayout.NORTH);

        daysPanel = new JPanel(new GridLayout(0, 7));
        daysPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        updateDaysPanel();
        add(daysPanel, BorderLayout.CENTER);

        JButton confirmButton = new JButton("Aceptar");
        confirmButton.addActionListener(e -> {
            if (selectedDate == null) {
                selectedDate = calendar.getTime();
            }
            setVisible(false);
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.add(confirmButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateDaysPanel() {
        int year = (Integer) yearSpinner.getValue();
        int month = monthComboBox.getSelectedIndex();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        daysPanel.removeAll();

        Calendar tempCalendar = (Calendar) calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        int offset = firstDayOfWeek - Calendar.MONDAY;
        if (offset < 0) offset += 7;

        for (int i = 0; i < offset; i++) {
            daysPanel.add(new JLabel(""));
        }

        for (int day = 1; day <= daysInMonth; day++) {
            final int currentDay = day;
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setMargin(new Insets(2, 2, 2, 2));
            dayButton.addActionListener(e -> {
                calendar.set(Calendar.DAY_OF_MONTH, currentDay);
                selectedDate = calendar.getTime();

                for (Component comp : daysPanel.getComponents()) {
                    if (comp instanceof JButton) {
                        comp.setBackground(null);
                        comp.setForeground(null);
                    }
                }
                dayButton.setBackground(Color.BLUE);
                dayButton.setForeground(Color.WHITE);
            });
            daysPanel.add(dayButton);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date date) {
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            yearSpinner.setValue(cal.get(Calendar.YEAR));
            monthComboBox.setSelectedIndex(cal.get(Calendar.MONTH));
            updateDaysPanel();

            for (Component comp : daysPanel.getComponents()) {
                if (comp instanceof JButton) {
                    JButton button = (JButton) comp;
                    if (button.getText().equals(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)))) {
                        button.setBackground(Color.BLUE);
                        button.setForeground(Color.WHITE);
                        break;
                    }
                }
            }
        }
    }


}