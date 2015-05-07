import javax.swing.*;

/**
 * Copyright (C) 2015  Benjamin Edward Oliver
 */
public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Controller();
            }
        });

    }
}
