import data.Action;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel implements MouseListener, MouseMotionListener {

    private final Painter painter;
    private data.Action action;

    public void setAction(data.Action action) {
        this.action = action;
        action.setSecondClick(false);
    }


    public ImagePanel(Painter painter) {
        addMouseListener(this);
        addMouseMotionListener(this);
        this.painter = painter;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        int curWidth = getWidth();
        int curHeight = getHeight();

        if (curHeight > painter.getImage().getHeight() || curWidth > painter.getImage().getWidth()) {
            BufferedImage image = new BufferedImage(curWidth, curHeight, Image.SCALE_SMOOTH);
            Graphics2D graphics2D = (Graphics2D) image.getGraphics();
            graphics2D.setColor(Color.WHITE);
            graphics2D.fillRect(0, 0, getWidth(), getHeight());
            graphics2D.drawImage(painter.getImage(), 0, 0, null);
            painter.setImage(image);
        }
        g2d.drawImage(painter.getImage(), 0, 0, null);

    }

    public void clean() {
        painter.clearImage();
        getGraphics().drawImage(painter.getImage(), 0, 0, null);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (action == data.Action.DRAW_STARS) {
            painter.drawStar(e.getX(), e.getY());
        } else if (action == data.Action.DRAW_POLYGONS) {
            painter.drawRhomb(e.getX(), e.getY());
        } else if (action == data.Action.DRAW_LINE) {
            if (!action.isSecondClick()) {
                action.setStartX(e.getX());
                action.setStartY(e.getY());
                action.setSecondClick(true);
            } else {
                if (painter.getThickness() == 1) {
                    int dy = e.getY() - action.getStartY();
                    int dx = e.getX() - action.getStartX();
                    painter.drawLineWithBresenham(action.getStartX(), action.getStartY(), dx, dy);
                } else {
                    painter.drawLine(action.getStartX(), action.getStartY(), e.getX(), e.getY());
                }
                action.setSecondClick(false);
            }
        } else if (action == data.Action.FILL_AREA) {
            painter.spanFill(e.getX(), e.getY());
        }
        getGraphics().drawImage(painter.getImage(), 0, 0, null);
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        if (action == Action.CLEAR_AREA) {
            painter.clearPixelWithWhiteColor(e.getX(), e.getY());
            getGraphics().drawImage(painter.getImage(), 0, 0, null);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
