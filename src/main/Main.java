package main;

import view.LoginUI;

import javax.swing.*;

/**
 * Main entry point of the game.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}
