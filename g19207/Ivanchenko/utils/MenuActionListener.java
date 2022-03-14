package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;

public class MenuActionListener implements ActionListener {
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private final static String[] instrumentsToIgnore = {"Save as", "Open", "Set thickness",
            "Current color", "Choose color", "About"} ;

    public MenuActionListener(JMenuBar menuBar, JToolBar toolBar) {
        this.menuBar = menuBar;
        this.toolBar = toolBar;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        try {
            Object source = evt.getSource();
            if (Arrays.asList(instrumentsToIgnore).contains(((AbstractButton) source).getName())) {
                ((AbstractButton) source).setSelected(false);
                return;
            }

            Component[] toolbarComponents = toolBar.getComponents();
            Arrays.stream(toolbarComponents).peek(c -> {
                if (c instanceof JButton) {
                    ((JButton) c).setSelected(false);
                    if (source instanceof JMenuItem) {
                        if (Objects.equals(((JMenuItem) source).getName(), c.getName())) {
                            ((JButton) c).setSelected(true);
                        }
                    }
                }
            }).toArray();

            //1 - index of View Jmenu
            JPopupMenu popupMenu = ((JMenu) (menuBar.getComponent(1))).getPopupMenu();
            Component[] popupMenuComponents = popupMenu.getComponents();
            Arrays.stream(popupMenuComponents).peek(c -> {
                if (c instanceof JRadioButtonMenuItem) {
                    ((JRadioButtonMenuItem) c).setSelected(false);
                    if (source instanceof JButton) {
                        if (Objects.equals(((JButton) source).getName(), c.getName())) {
                            ((JRadioButtonMenuItem) c).setSelected(true);
                        }
                    }
                }
            }).toArray();

            ((AbstractButton) source).setSelected(true);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
