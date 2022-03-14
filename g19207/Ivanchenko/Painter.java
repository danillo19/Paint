import data.ShapeInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class Painter {
    private BufferedImage image;
    private Graphics2D g2d;
    private Color color;

    private ShapeInfo starInfo;
    private ShapeInfo polygonInfo;

    private Integer thickness;

    private final static Integer DEFAULT_RADIUS = 150;
    private final static Integer DEFAULT_ROTATION = 0;
    private final static Integer DEFAULT_STAR_VERTICES_NUMBER = 5;
    private final static Integer DEFAULT_POLYGON_VERTICES_NUMBER = 4;
    private final static Integer ERASER_WIDTH = 4, ERASER_HEIGHT = 4;
    private final static Integer DEFAULT_THICKNESS = 1;

    public Painter() {
        starInfo = new ShapeInfo(DEFAULT_STAR_VERTICES_NUMBER, DEFAULT_RADIUS, DEFAULT_ROTATION);
        polygonInfo = new ShapeInfo(DEFAULT_POLYGON_VERTICES_NUMBER, DEFAULT_RADIUS, DEFAULT_ROTATION);
        thickness = DEFAULT_THICKNESS;
        color = Color.BLACK;
        setImage(new BufferedImage(1, 1, Image.SCALE_SMOOTH));
    }

    public ShapeInfo getStarInfo() {
        return starInfo;
    }

    public void setStarInfo(ShapeInfo starInfo) {
        this.starInfo = starInfo;
    }

    public ShapeInfo getPolygonInfo() {
        return polygonInfo;
    }

    public void setPolygonInfo(ShapeInfo shapeInfo) {
        this.polygonInfo = shapeInfo;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {

        this.color = color;
        g2d.setColor(color);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        this.g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thickness));
    }

    public BufferedImage getImage() {
        return image;
    }

    public Integer getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
        g2d.setStroke(new BasicStroke(thickness));
    }

    private static class Span {
        private final int leftX;
        private final int rightX;
        private final int y;

        public Span(int leftX, int rightX, int y) {
            this.leftX = leftX;
            this.rightX = rightX;
            this.y = y;
        }
    }

    boolean isInside(int x, int y, int seedColorRGB) {
        if (x < 0 || x >= image.getWidth() || y <= 0 || y >= image.getHeight()) return false;
        return image.getRGB(x, y) == seedColorRGB && image.getRGB(x, y) != color.getRGB();
    }

    private void scanForSpans(int leftX, int rightX, int y, Stack<Span> stack, int seedColorRGB) {
        int leftBound = leftX;
        int rightBound = leftX;
        if (isInside(leftX, y, seedColorRGB)) {
            while (isInside(leftBound - 1, y, seedColorRGB)) leftBound--;
        } else {
            while (!isInside(leftBound, y, seedColorRGB)) {
                leftBound++;
                if (leftBound > rightX) return;
            }
            rightBound = leftBound;
        }

        while (isInside(rightBound + 1, y, seedColorRGB)) rightBound++;
        stack.push(new Span(leftBound, rightBound, y));

        if (rightBound < rightX) scanForSpans(rightBound + 1, rightX, y, stack, seedColorRGB);
    }

    public void spanFill(int x, int y) {
        int seedColorRGB = image.getRGB(x, y);
        Stack<Span> stack = new Stack<>();
        int leftBound = x;
        int rightBound = x;
        while (isInside(leftBound, y, seedColorRGB)) leftBound--;
        while (isInside(rightBound, y, seedColorRGB)) rightBound++;
        stack.push(new Span(leftBound + 1, rightBound - 1, y));

        while (!stack.empty()) {
            Span span = stack.pop();
            for (int i = span.leftX; i <= span.rightX; i++) image.setRGB(i, span.y, color.getRGB());

            scanForSpans(span.leftX, span.rightX, span.y + 1, stack, seedColorRGB);
            scanForSpans(span.leftX, span.rightX, span.y - 1, stack, seedColorRGB);
        }

    }

    private double[] calcRotation(double x, double y, double rotation) {
        double[] res = new double[2];
        res[0] = Math.cos(rotation) * x - Math.sin(rotation) * y;
        res[1] = Math.sin(rotation) * x + Math.cos(rotation) * y;
        return res;
    }


    public void drawStar(int centerX, int centerY) {
        int[] x = new int[2 * starInfo.getVertices()];
        int[] y = new int[2 * starInfo.getVertices()];

        double rotationRadian = starInfo.getRotation() * Math.PI / 180;

        double alpha = 2 * Math.PI / (2 * starInfo.getVertices());
        double inner_radius = 1.5 * (starInfo.getRadius() / (1 + Math.tan(alpha) / Math.tan(alpha / 2))) / Math.cos(alpha);

        for (int i = 0; i < starInfo.getVertices() * 2; i++) {
            double[] add_rotation;
            if (i % 2 == 0) {
                add_rotation = calcRotation(Math.cos(i * alpha) * starInfo.getRadius(),
                        Math.sin(i * alpha) * starInfo.getRadius(), rotationRadian);
            } else {
                add_rotation = calcRotation(Math.cos(i * alpha) * inner_radius, Math.sin(i * alpha) * inner_radius, rotationRadian);
            }
            x[i] = (int) (add_rotation[0]) + centerX;
            y[i] = (int) (add_rotation[1]) + centerY;
        }

        drawShape(x, y);
    }

    public void drawRhomb(int centerX, int centerY) {
        int[] x = new int[polygonInfo.getVertices()];
        int[] y = new int[polygonInfo.getVertices()];

        double rotationRadian = Math.PI * polygonInfo.getRotation() / 180.0;

        double alpha = 2 * Math.PI / polygonInfo.getVertices();
        for (int i = 0; i < polygonInfo.getVertices(); i++) {
            double xBeforeRotation = Math.round(Math.cos(alpha * (i + 1)) * polygonInfo.getRadius());
            double yBeforeRotation = Math.round(Math.sin(alpha * (i + 1)) * polygonInfo.getRadius());
            double[] addRotation = calcRotation(xBeforeRotation, yBeforeRotation, rotationRadian);
            x[i] = (int) addRotation[0] + centerX;
            y[i] = (int) addRotation[1] + centerY;
        }

        drawShape(x, y);
    }

    private void drawShape(int[] x, int[] y) {
        Polygon polygon = new Polygon(x, y, x.length);
        g2d.setColor(Color.black);
        g2d.draw(polygon);
        g2d.setColor(color);
    }

    public void clearPixelWithWhiteColor(int x, int y) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x - ERASER_WIDTH / 2, y - ERASER_HEIGHT / 2, ERASER_WIDTH, ERASER_HEIGHT);
        g2d.setColor(color);
    }

    public void clearImage() {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.setColor(color);

    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        g2d.drawLine(x1, y1, x2, y2);
    }

    public void drawLineWithBresenham(int x, int y, int dx, int dy) {
        int stepX = dx > 0 ? 1 : -1;
        int stepY = dy > 0 ? 1 : -1;

        if (dy < 0) dy = -dy;
        if (dx < 0) dx = -dx;

        int steps = Math.max(dy, dx);
        int k = Math.min(dx, dy);

        int error = -steps;

        for (int i = 0; i < steps; i++) {
            error += 2 * k;
            if (dx > dy) x += stepX;
            else y += stepY;

            if (error > 0) {
                error -= 2 * steps;
                if (dx > dy) y += stepY;
                else x += stepX;
            }
            image.setRGB(x, y, color.getRGB());

        }
    }

}
