/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PercursoDroneJDTC;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author glauc
 */
public class LeituraEInstancia {

    public LinkedList ler(String endereco, double raio) {

        BufferedReader conteudoCSV = null; //BufferedReader lê o arquivo inteiro
        String linha = ""; //nossa linha por linha
        String separador = ",";
        String[] linhassplitada;
        LinkedList<Torre> torres = new LinkedList();

        //leitura para guardarmos todos nossos Pontos com as coordenadas =)
        try {
            conteudoCSV = new BufferedReader(new FileReader(endereco));//se der erro na leitura, vai diretao para o catch =)

            linha = conteudoCSV.readLine(); //Primeira linha sendo ignorada "x,y"
            int i = 0;
            while ((linha = conteudoCSV.readLine()) != null && !linha.contentEquals("")) {
                linhassplitada = linha.split(separador);
                torres.add(new Torre(i, Double.parseDouble(linhassplitada[0]), Double.parseDouble(linhassplitada[1]), 100 * Double.parseDouble(linhassplitada[2])));
                i++;
//                if (i == 100) {;
//                    break;
//                }
            }
        } catch (FileNotFoundException e) { //arquivo nao existente
            System.out.println("Arquivo nao encontrado: \n" + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) { //posicao invalida array
            System.out.println("IndexOfBounds: \n" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Erro: \n" + e.getMessage());
        } finally {
            if (conteudoCSV != null) {
                try {
                    conteudoCSV.close();
                } catch (IOException e) {
                    System.out.println("IO Erro: \n" + e.getMessage());

                }
            }
        }
        return torres;
    }
}
