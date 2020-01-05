/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PercursoDroneJDTC;

import java.awt.Graphics;
import java.util.LinkedList;
import javax.swing.JFrame;

/**
 *
 * @author glauc
 */
public class Desenha1 extends JFrame {

    Graphics x = null;

    @Override

    public void paint(Graphics g) {
        x = g;

        g.drawRect(400, 100, 300, 300);//objeto retangular, x e y inicial e dps a largura
        g.drawOval(430, 130, 260, 260);//circulo, mesmos parametros de cima
        g.fillRect(420, 120, 260, 260);

    }

    public Desenha1() {
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

}
