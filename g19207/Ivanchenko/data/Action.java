package data;

public enum Action {
    DRAW_LINE,
    DRAW_STARS,
    DRAW_POLYGONS,
    FILL_AREA,
    CLEAR_AREA;

    private boolean secondClick = false;
    private Integer startX;
    private Integer startY;

    public Integer getStartX() {
        return startX;
    }

    public void setStartX(Integer startX) {
        this.startX = startX;
    }

    public Integer getStartY() {
        return startY;
    }

    public void setStartY(Integer startY) {
        this.startY = startY;
    }

    public boolean isSecondClick() {
        return secondClick;
    }

    public void setSecondClick(boolean secondClick) {
        this.secondClick = secondClick;
    }
}
