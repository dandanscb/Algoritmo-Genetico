/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PercursoDroneJDTC;

import java.util.LinkedList;

/**
 *
 * @author glauc
 */
public class Ponto {

    public double x, y;
    public LinkedList<Integer> idTorres;

    public Ponto(double x, double y, LinkedList<Integer> idTorres) {
        this.x = x;
        this.y = y;
        this.idTorres = idTorres;

    }

}
