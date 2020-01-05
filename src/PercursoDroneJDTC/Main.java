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
public class Main {

    public static void main(String[] args) {
        LeituraEInstancia leitura = new LeituraEInstancia();
        LinkedList<Torre> torres = leitura.ler("instancia_drone_1000.csv", 10.0);
        Drone a = new Drone(torres);
        a.calculaRota(0);

    }
}
