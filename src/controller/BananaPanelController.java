package controller;

import view.BananaPanel;
import model.BananaPanelModel;
import java.awt.Point;

public class BananaPanelController {
    private BananaPanel view;
    private BananaPanelModel model;

    public BananaPanelController(BananaPanel view) {
        this.view = view;
        this.model = new BananaPanelModel();
    }

    public void setPanelDimensions(int width, int height) {
        model.setPanelDimensions(width, height);
    }

    public void generateNewBanana() {
        model.generateNewPosition();
        view.repaint();
    }

    public void checkCollision(Point snakeHead) {
        if (model.isCollision(snakeHead)) {
            model.incrementScore();
            model.hideBanana();
            view.updateScore(model.getScore());
            view.repaint();
        }
    }

    public void reset() {
        model.reset();
        view.updateScore(0);
        view.repaint();
    }

    public void setBananaSize(int size) {
        model.setBananaSize(size);
        view.repaint();
    }

    public BananaPanelModel getModel() {
        return model;
    }

    public void dispose() {
        model.reset();
    }
} 