package settings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SettingsPanel extends JPanel {
    private final JSlider radiusSlider;
    private final JSlider rotationSlider;
    private final JTextField verticesField;
    private final JTextField radiusField;
    private final JTextField rotationField;
    private final JLabel radiusLabel;
    private final JLabel rotationLabel;
    private final JLabel verticesLabel;


    public JSlider getRadiusSlider() {
        return radiusSlider;
    }

    public JSlider getRotationSlider() {
        return rotationSlider;
    }

    public JTextField getVerticesField() {
        return verticesField;
    }

    public void setVerticesField(String text) {
        verticesField.setText(text);
    }

    public SettingsPanel(int curRadius, int curRotation, int curVertices) {
        super(new GridBagLayout());

        radiusField = new JTextField(String.valueOf(curRadius), 3);
        radiusLabel = new JLabel("radius");

        radiusSlider = new JSlider(0, 200, curRadius);
        radiusSlider.setMajorTickSpacing(50);
        radiusSlider.setMinorTickSpacing(5);
        radiusSlider.setPaintTicks(true);
        radiusSlider.setPaintLabels(true);

        rotationSlider = new JSlider(0, 360, curRotation);
        rotationSlider.setMajorTickSpacing(90);
        rotationSlider.setMinorTickSpacing(15);
        rotationSlider.setPaintTicks(true);
        rotationSlider.setPaintLabels(true);


        radiusSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int value = source.getValue();
                radiusField.setText(String.valueOf(value));
            }
        });

        radiusField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField source = (JTextField) e.getSource();
                String text = source.getText();
                try {
                    radiusSlider.setValue(Integer.parseInt(text));
                }
                catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null,"Value must be integer","Error",JOptionPane.ERROR_MESSAGE);
                    source.setText(String.valueOf(radiusSlider.getValue()));
                }
            }
        });

        verticesLabel = new JLabel("vertices number");
        verticesField = new JTextField(String.valueOf(curVertices), 3);

        rotationLabel = new JLabel("rotation");
        rotationField = new JTextField(String.valueOf(curRotation), 3);
        rotationField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField source = (JTextField) e.getSource();
                String text = source.getText();
                try {
                    rotationSlider.setValue(Integer.parseInt(text));
                }
                catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null,"Value must be integer","Error",JOptionPane.ERROR_MESSAGE);
                    source.setText(String.valueOf(rotationSlider.getValue()));
                }
            }
        });

        rotationSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int value = source.getValue();
                rotationField.setText(Integer.toString(value));
            }
        });

        makeLayout();
    }

    private void makeLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 10, 10, 5);

        add(radiusLabel, gbc);
        gbc.gridx++;

        add(radiusSlider, gbc);
        gbc.gridx++;
        add(radiusField, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        add(rotationLabel, gbc);
        gbc.gridx++;
        add(rotationSlider, gbc);
        gbc.gridx++;
        add(rotationField, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        add(verticesLabel, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.LINE_START;
        add(verticesField, gbc);
    }

}
