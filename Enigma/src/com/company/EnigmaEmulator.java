package com.company;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class EnigmaEmulator  extends JFrame {
    String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private JPanel rotorPanel = new JPanel();
    private JPanel ringPanel = new JPanel();
    private JPanel rotationPanel = new JPanel();
    private JPanel entryPanel = new JPanel();
    private JPanel outputPanel = new JPanel();

    private JComboBox rotor1 = new JComboBox();
    private JComboBox rotor2 = new JComboBox();
    private JComboBox rotor3 = new JComboBox();

    private JComboBox ring1 = new JComboBox();
    private JComboBox ring2 = new JComboBox();
    private JComboBox ring3 = new JComboBox();

    private JComboBox rotation1 = new JComboBox();
    private JComboBox rotation2 = new JComboBox();
    private JComboBox rotation3 = new JComboBox();

    private JComboBox reflectors = new JComboBox();

    private JLabel rotorLabel1 = new JLabel("Rotor 1", SwingConstants.CENTER);
    private JLabel rotorLabel2 = new JLabel("Rotor 2", SwingConstants.CENTER);
    private JLabel rotorLabel3 = new JLabel("Rotor 3", SwingConstants.CENTER);

    private JLabel entryLabel = new JLabel("Enter the message to be encoded", SwingConstants.CENTER);
    private JTextField messageEntry = new JTextField();
    private JButton encodeButton = new JButton("Encode");

    private JLabel outputLabel = new JLabel("", SwingConstants.CENTER);

    private DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
    private JComboBox[] comboBoxes = {rotor1, rotor2, rotor3, ring1, ring2, ring3, rotation1, rotation2, rotation3, reflectors};

    public EnigmaEmulator() {
        setTitle("Enigma Emulator");
        setBackground(new Color(60, 63, 65));
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items

        for(JComboBox comboBox: comboBoxes) {
            comboBox.setRenderer(listRenderer);
        }

        setRotorPanel();
        setEntryPanel();
        setOutputPanel();

        encodeButton.addActionListener(e -> {
            String rotor3Value = rotor3.getSelectedItem().toString();
            String rotation3Value = rotation3.getSelectedItem().toString();
            String ring3Value = ring3.getSelectedItem().toString();

            String rotor2Value = rotor2.getSelectedItem().toString();
            String rotation2Value = rotation2.getSelectedItem().toString();
            String ring2Value = ring2.getSelectedItem().toString();

            String rotor1Value = rotor1.getSelectedItem().toString();
            String rotation1Value = rotation1.getSelectedItem().toString();
            String ring1Value = ring1.getSelectedItem().toString();

            String reflectorValue = reflectors.getSelectedItem().toString();
            String message = messageEntry.getText();

            encode(rotor3Value, rotation3Value, ring3Value,
                   rotor2Value, rotation2Value, ring2Value,
                   rotor1Value, rotation1Value, ring1Value,
                   reflectorValue, message);
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setRotorPanel() {
        JPanel mainRotorPanel = new JPanel();
        mainRotorPanel.setBackground(new Color(179, 45, 0));
        mainRotorPanel.setLayout(new BorderLayout());

        rotorPanel.setLayout(new GridLayout(1, 5, 20, 20));
        rotorPanel.setBackground(new Color(179, 45, 0));

        for (String rotor: new String[]{"I", "II", "III", "IV", "V", "Beta", "Gamma"}) {
            rotor1.addItem(rotor);
            rotor2.addItem(rotor);
            rotor3.addItem(rotor);
        }

        for (String letter: new String[]{"A", "B", "C"}) {
            reflectors.addItem(letter);
        }

        JPanel rotor1Panel = new JPanel();
        rotor1Panel.setBackground(new Color(179, 45, 0));
        rotor1Panel.setLayout(new BorderLayout());
        rotorLabel1.setFont(new Font("Arial", Font.BOLD, 18));
        rotorLabel1.setForeground(new Color(255, 240, 179));
        rotor1Panel.add(rotorLabel1, BorderLayout.CENTER);
        rotor1Panel.add(rotor1, BorderLayout.SOUTH);

        JPanel reflectorPanel = new JPanel();
        reflectorPanel.setBackground(new Color(179, 45, 0));
        reflectorPanel.setLayout(new BorderLayout());
        JLabel reflectorLabel = new JLabel("Reflector", SwingConstants.CENTER);
        reflectorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        reflectorLabel.setForeground(new Color(255, 240, 179));
        reflectorPanel.add(reflectorLabel, BorderLayout.NORTH);
        reflectorPanel.add(reflectors, BorderLayout.CENTER);

        JPanel rotor2Panel = new JPanel();
        rotor2Panel.setBackground(new Color(179, 45, 0));
        rotor2Panel.setLayout(new BorderLayout());
        rotorLabel2.setFont(new Font("Arial", Font.BOLD, 18));
        rotorLabel2.setForeground(new Color(255, 240, 179));
        rotor2Panel.add(rotorLabel2, BorderLayout.CENTER);
        rotor2Panel.add(rotor2, BorderLayout.SOUTH);

        JPanel rotor3Panel = new JPanel();
        rotor3Panel.setBackground(new Color(179, 45, 0));
        rotor3Panel.setLayout(new BorderLayout());
        rotorLabel3.setFont(new Font("Arial", Font.BOLD, 18));
        rotorLabel3.setForeground(new Color(255, 240, 179));
        rotor3Panel.add(rotorLabel3, BorderLayout.CENTER);
        rotor3Panel.add(rotor3, BorderLayout.SOUTH);

        setRotationPanel();

        JPanel rotorMainPanel = new JPanel();
        rotorMainPanel.setBackground(new Color(179, 45, 0));
        rotorMainPanel.setLayout(new GridLayout(1, 3, 20, 20));



        rotorPanel.add(rotor1Panel);
        rotorPanel.add(rotor2Panel);
        rotorPanel.add(rotor3Panel);

        setRingPanel();

        mainRotorPanel.add(rotorPanel, BorderLayout.CENTER);
        mainRotorPanel.add(reflectorPanel, BorderLayout.SOUTH);
        add(mainRotorPanel);
    }

    private void setRingPanel() {
        ringPanel.setLayout(new GridLayout(2, 1));
        ringPanel.setBackground(new Color(179, 45, 0));
        setRings();

        JPanel ringLabelPanel = new JPanel();
        ringLabelPanel.setBackground(new Color(179, 45, 0));
        ringLabelPanel.setLayout(new BorderLayout());
        JLabel ringLabel = new JLabel("Ring Settings", SwingConstants.CENTER);
        ringLabel.setFont(new Font("Arial",Font.BOLD, 14));
        ringLabel.setForeground(new Color(255, 240, 179));
        ringLabelPanel.add(ringLabel, BorderLayout.CENTER);

        JPanel ringSubPanel = new JPanel();
        ringSubPanel.setBackground(new Color(179, 45, 0));
        ringSubPanel.setLayout(new GridLayout(1, 3));
        ringSubPanel.add(ring1);
        ringSubPanel.add(ring2);
        ringSubPanel.add(ring3);

        ringPanel.add(ringLabelPanel);
        ringPanel.add(ringSubPanel);

        rotorPanel.add(ringPanel);
    }

    private void setRotationPanel() {
        rotationPanel.setLayout(new GridLayout(2, 1));
        rotationPanel.setBackground(new Color(179, 45, 0));
        setRotations();

        JPanel rotationLabelPanel = new JPanel();
        rotationLabelPanel.setBackground(new Color(179, 45, 0));
        rotationLabelPanel.setLayout(new BorderLayout());
        JLabel rotationLabel = new JLabel("Positions", SwingConstants.CENTER);
        rotationLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rotationLabel.setForeground(new Color(255, 240, 179));
        rotationLabelPanel.add(rotationLabel, BorderLayout.CENTER);

        JPanel rotationSubPanel = new JPanel();
        rotationSubPanel.setBackground(new Color(179, 45, 0));
        rotationSubPanel.setLayout(new GridLayout(1, 3));
        rotationSubPanel.add(rotation1);
        rotationSubPanel.add(rotation2);
        rotationSubPanel.add(rotation3);

        rotationPanel.add(rotationLabelPanel);
        rotationPanel.add(rotationSubPanel);

        rotorPanel.add(rotationPanel);
    }

    private void setRings() {
        for (int i=1; i < 27; i++) {
            ring1.addItem(i);
            ring2.addItem(i);
            ring3.addItem(i);
        }
    }

    private void setRotations() {
        for (char c: ALPHABET.toCharArray()) {
            String letter = String.valueOf(c);

            rotation1.addItem(letter);
            rotation2.addItem(letter);
            rotation3.addItem(letter);
        }
    }

    private void setEntryPanel() {
        entryPanel.setLayout(new GridLayout(2, 1));
        entryPanel.setBackground(new Color(187, 187, 187));

        JPanel entrySubPanel = new JPanel();
        entrySubPanel.setLayout(new BorderLayout());
        entrySubPanel.setBackground(new Color(187, 187, 187));
        entrySubPanel.add(messageEntry, BorderLayout.CENTER);
        entrySubPanel.add(encodeButton, BorderLayout.SOUTH);

        entryPanel.add(entryLabel);
        entryPanel.add(entrySubPanel);

        add(entryPanel);
    }

    private void setOutputPanel() {
        outputPanel.setBackground(new Color(187, 187, 187));
        outputPanel.setLayout(new BorderLayout());

        outputLabel.setFont(new Font("Arial", Font.BOLD, 16));

        outputPanel.add(outputLabel, BorderLayout.CENTER);

        add(outputPanel);
    }

    private void encode (String rotor3, String rotation3, String ring3,
                        String rotor2, String rotation2, String ring2,
                        String rotor1, String rotation1, String  ring1,
                        String reflector, String message) {

        String command = String.format("python enigma.py %s %s %s %s %s %s %s %s %s %s %s", rotor3, rotation3, ring3,
                                                                                            rotor2, rotation2, ring2,
                                                                                            rotor1, rotation1, ring1,
                                                                                            reflector, message);

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String output = "";

            while((output = reader.readLine()) != null) {
                outputLabel.setText(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EnigmaEmulator();
    }
}
