import data.Action;
import utils.ExtensionFileFilter;
import data.ShapeInfo;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Scene;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.embed.swing.JFXPanel;
import settings.SettingsPanel;

import javax.annotation.processing.FilerException;
import javax.imageio.ImageIO;

import javafx.scene.media.*;
import utils.MainFrame;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Frame extends MainFrame {
    private ImagePanel panel;
    private Painter painter;
    private static final Integer CURRENT_COLOR_INDEX_IN_TOOLBAR = 11;
    private static final Integer MAX_VERTICES_NUMBER = 16;
    private static final Integer MIN_STAR_VERTICES_NUMBER = 4;
    private static final Integer MIN_POLYGON_VERTICES_NUMBER = 3;
    private static final Integer MAX_THICKNESS = 20;
    private static final Integer MIN_THICKNESS = 1;

    public Frame() {
        super(600, 480, "PAINT");
        try {
            addSubMenu("File", KeyEvent.VK_C);
            addMenuItem("File/Save as", "Save image", KeyEvent.VK_S, "save.png", "onSave");
            addMenuItem("File/Open", "Open image", KeyEvent.VK_C, "open-folder.png", "onOpen");
            addMenuItem("File/Exit", "Close app", KeyEvent.VK_C, "power.png", "onExit");

            addSubMenu("View", KeyEvent.VK_S);
            addMenuItem("View/Draw line", "Draw line", 0, "diagonal-line.png", "onDrawLine");
            addMenuItem("View/Draw star", "Draw star", 0, "star.png", "onDrawStar");
            addMenuItem("View/Draw polygon", "Draw polygon", 0, "polygon.png", "onDrawPolygon");
            addMenuItem("View/Fill area", "Fill area", 0, "paint-spray.png", "onFillArea");
            addMenuItem("View/Clear area", "Clear area with white", 0, "eraser.png", "onClearArea");
            addMenuSeparator("View");
            addMenuItem("View/Choose color", "Choose colour", 0, "color-wheel.png", "onChooseColor");
            addMenuItem("View/Set thickness", "Set thickness of line", 0, "width.png", "onSetThickness");
            addMenuItem("View/Current color", "Current color to draw", 0, "square.png", "onCurrentColor");

            addSubMenu("Help", KeyEvent.VK_S);
            addMenuItem("Help/About", "About app", 0, "about.png", "onAbout");

            addToolBarButton("File/Open");
            addToolBarButton("File/Save as");
            addToolBarSeparator();
            addToolBarButton("View/Draw line");
            addToolBarButton("View/Draw star");
            addToolBarButton("View/Draw polygon");
            addToolBarButton("View/Fill area");
            addToolBarButton("View/Clear area");
            addToolBarSeparator();
            addToolBarButton("View/Choose color");
            addToolBarButton("View/Set thickness");
            addToolBarButton("View/Current color");

            painter = new Painter();
            panel = new ImagePanel(painter);
            add(panel);

            JButton button = new JButton("Clean");
            button.addActionListener(ev -> panel.clean());
            add(button, BorderLayout.SOUTH);

            pack();
            setVisible(true);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void onClearArea() {
        panel.setAction(data.Action.CLEAR_AREA);
    }

    public void onExit() {
        System.exit(0);
    }

    public void onSave() throws FilerException {
        String[] extensions = {"png", "gif", "jpeg", "bmp"};
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        for (String extension : extensions) {
            ExtensionFileFilter fileFilter = new ExtensionFileFilter(extension, "");
            fileChooser.addChoosableFileFilter(fileFilter);
        }
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            FileFilter fileFilter = fileChooser.getFileFilter();
            String format = "png";
            if (!fileFilter.accept(file)) {
                ExtensionFileFilter extendedFileFilter = (ExtensionFileFilter) fileFilter;
                file = new File(file.getAbsolutePath() + "." + extendedFileFilter.getExtension());
                format = extendedFileFilter.getExtension();
            }
            try {
                ImageIO.write(painter.getImage(), format, file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void onOpen() throws IOException {
        String[] extensions = {"png", "gif", "jpeg", "bmp"};
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        for (String extension : extensions) {
            ExtensionFileFilter fileFilter = new ExtensionFileFilter(extension, "");
            fileChooser.addChoosableFileFilter(fileFilter);
        }
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            painter.setImage(ImageIO.read(file));
            panel.repaint();
        }

    }

    public void onAbout() {
        getVideo();
    }

    public void onDrawLine() {
        panel.setAction(data.Action.DRAW_LINE);
    }

    public void onDrawPolygon() {
        panel.setAction(data.Action.DRAW_POLYGONS);
        ShapeInfo info = getCurrentShapeInfo(data.Action.DRAW_POLYGONS);
        SettingsPanel settingsPanel = new SettingsPanel(info.getRadius(), info.getRotation(), info.getVertices());
        setSettingsParams(data.Action.DRAW_POLYGONS, settingsPanel, info);

    }

    public void onDrawStar() {
        panel.setAction(data.Action.DRAW_STARS);
        ShapeInfo info = getCurrentShapeInfo(data.Action.DRAW_STARS);
        SettingsPanel settingsPanel = new SettingsPanel(info.getRadius(), info.getRotation(), info.getVertices());
        setSettingsParams(data.Action.DRAW_STARS, settingsPanel, info);
    }

    public void onChooseColor() {
        Color color = JColorChooser.showDialog(this, "Choose color", null);
        painter.setColor(color);

        //Changing current color icon
        Component component = toolBar.getComponent(CURRENT_COLOR_INDEX_IN_TOOLBAR);
        ImageIcon icon = (ImageIcon) ((JButton) component).getIcon();
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.createGraphics();
        graphics.setColor(color);
        graphics.fillRect(2, 2, 20, 20);
        icon.setImage(image);
        repaint();

    }

    public void onFillArea() {
        panel.setAction(data.Action.FILL_AREA);
    }

    public void onSetThickness() {
        JTextField thicknessTextField = new JTextField(painter.getThickness().toString());
        while (true) {
            JOptionPane.showMessageDialog(this, thicknessTextField, "Enter thickness", JOptionPane.PLAIN_MESSAGE, null);
            try {
                int thickness = Integer.parseInt(thicknessTextField.getText());
                if (thickness > MAX_THICKNESS || thickness < MIN_THICKNESS) {
                    JOptionPane.showMessageDialog(this, "Thickness must be in range from 1 to 20 pixels",
                            "Warning", JOptionPane.ERROR_MESSAGE);
                    thicknessTextField.setText(painter.getThickness().toString());
                    continue;
                } else {
                    painter.setThickness(thickness);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Thickness must be integer value",
                        "Warning",JOptionPane.ERROR_MESSAGE);
                thicknessTextField.setText(painter.getThickness().toString());
                continue;
            }
            break;
        }
    }

    private ShapeInfo getCurrentShapeInfo(data.Action action) {
        ShapeInfo info;
        switch (action) {
            case DRAW_POLYGONS -> info = painter.getPolygonInfo();
            case DRAW_STARS -> info = painter.getStarInfo();
            default -> throw new IllegalArgumentException();
        }
        return info;
    }

    private void setSettingsParams(data.Action action, SettingsPanel settingsPanel, ShapeInfo info) {
        while (true) {
            int ret = JOptionPane.showConfirmDialog(this, settingsPanel, "Settings",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (ret == JOptionPane.OK_OPTION) {
                try {
                    int vertices = Integer.parseInt(settingsPanel.getVerticesField().getText());
                    if (vertices > MAX_VERTICES_NUMBER
                            || (vertices < MIN_STAR_VERTICES_NUMBER && action == data.Action.DRAW_STARS)
                            || (vertices < MIN_POLYGON_VERTICES_NUMBER)
                    ) {
                        throw new IllegalArgumentException();
                    }
                    info.setVertices(Integer.parseInt(settingsPanel.getVerticesField().getText()));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Value must be integer",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    settingsPanel.setVerticesField(String.valueOf(info.getVertices()));
                    continue;
                } catch (IllegalArgumentException ex) {
                    if (action == Action.DRAW_STARS) {
                        JOptionPane.showMessageDialog(null, "Vertices number must be from 4 to 16 range",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Vertices number must be from 3 to 16 range",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    settingsPanel.setVerticesField(String.valueOf(info.getVertices()));
                    continue;
                }

                info.setRotation(settingsPanel.getRotationSlider().getValue());
                info.setRadius(settingsPanel.getRadiusSlider().getValue());
                switch (action) {
                    case DRAW_POLYGONS -> painter.setPolygonInfo(info);
                    case DRAW_STARS -> painter.setStarInfo(info);
                }
            }
            break;
        }
    }

    public void onCurrentColor() {
    }

    private void getVideo() {
        final JFXPanel VFXPanel = new JFXPanel();

        File videoSource = new File("src/main/resources/" + "about_vid.mp4");
        Media m = new Media(videoSource.toURI().toString());
        MediaPlayer player = new MediaPlayer(m);
        MediaView viewer = new MediaView(player);

        StackPane root = new StackPane();

        Scene scene = new Scene(root);

        JFrame videoPanel = new JFrame();

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        viewer.setX((screen.getWidth() - videoPanel.getWidth()) / 2);
        viewer.setY((screen.getHeight() - videoPanel.getHeight()) / 2);

        DoubleProperty width = viewer.fitWidthProperty();
        DoubleProperty height = viewer.fitHeightProperty();
        width.bind(Bindings.selectDouble(viewer.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(viewer.sceneProperty(), "height"));
        viewer.setPreserveRatio(true);

        root.getChildren().add(viewer);

        VFXPanel.setScene(scene);
        player.play();

        JLabel label = new JLabel("Paint by Danila Ivanchenko, 19207. FIT NSU, 2022");
        videoPanel.setTitle("About app");
        videoPanel.setPreferredSize(new Dimension(600, 480));
        videoPanel.setLayout(new BorderLayout());

        videoPanel.add(label, BorderLayout.NORTH);
        videoPanel.add(VFXPanel, BorderLayout.CENTER);
        videoPanel.pack();

        videoPanel.setVisible(true);
    }

    public static void main(String[] args) {
        Frame frame = new Frame();
    }

}
