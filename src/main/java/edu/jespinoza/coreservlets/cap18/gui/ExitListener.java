package edu.jespinoza.coreservlets.cap18.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ExitListener extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }
}
