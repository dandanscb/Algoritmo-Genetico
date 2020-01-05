/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PercursoDroneJDTC;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;
import javafx.scene.shape.Ellipse;
import javax.swing.JFrame;

/**
 *
 * @author glauc
 */
public class Drone extends JFrame {

    LinkedList<Torre> torres;
    LinkedList<Ponto> percursoFinal = new LinkedList();
    LinkedList<LinkedList<Ponto>> populacao = new LinkedList();
    int qtdTorres;
    int auxDraw = 20;
    int auxDraw2 = 80;

    @Override

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        //DESENHAR OS CIRCULOS E PONTOS CENTRAIS DAS TORRES
        this.torres.forEach((p)
                -> {
            double x = p.x - (p.raio);
            double y = p.y - (p.raio);
            g2d.setColor(Color.LIGHT_GRAY);
            Shape circulo = new Ellipse2D.Double((x / auxDraw) + auxDraw2, (y / auxDraw) + auxDraw2, (p.raio * 2 / auxDraw) + auxDraw2, (p.raio * 2 / auxDraw) + auxDraw2);
            Shape ponto = new Line2D.Double((p.x / auxDraw) + auxDraw2, (p.y / auxDraw) + auxDraw2, (p.x / auxDraw) + auxDraw2, (p.y / auxDraw) + auxDraw2);
            g2d.draw(ponto);
            g2d.draw(circulo);
        });

        //linhas da rota
        for (int i = 0; i < this.percursoFinal.size() - 1; i++) {
            g2d.setColor(Color.RED);
            Ponto p1 = this.percursoFinal.get(i);
            Ponto p2 = this.percursoFinal.get(i + 1);
            Shape linha = new Line2D.Double((p1.x / auxDraw) + auxDraw2, (p1.y / auxDraw) + auxDraw2, (p2.x / auxDraw) + auxDraw2, (p2.y / auxDraw) + auxDraw2);
            g2d.draw(linha);
        }
        Ponto p1 = this.percursoFinal.get(this.percursoFinal.size() - 1);
        Ponto p2 = this.percursoFinal.get(0);
        Shape linha = new Line2D.Double((p1.x / auxDraw) + auxDraw2, (p1.y / auxDraw) + auxDraw2, (p2.x / auxDraw) + auxDraw2, (p2.y / auxDraw) + auxDraw2);
        g2d.draw(linha);
    }

    public Drone(LinkedList<Torre> torres) {
        this.torres = torres;
        this.qtdTorres = torres.size();
        this.setVisible(true);
        this.setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.repaint();
    }

    public double calculaDistancia(Ponto t1, Ponto p1) {
        return Math.sqrt(Math.pow((t1.x - p1.x), 2) + Math.pow((t1.y - p1.y), 2));//distancia euclidiana entre os dois centros, dois pontos
    }

    public void HillClimb() {
        System.out.println("Inicio HillClimb");
        LinkedList<Ponto> cpypercurso;
        double distancia = this.calculaDistanciaRota(this.percursoFinal);
        for (int l = 0; l < 150000; l++) {
            //criancao de um ponto aleatório
            LinkedList<Integer> idTorres = new LinkedList();//Linkedlist para o novo ponto ver quais torres ele abrange
            Ponto pontoAleatorio = new Ponto(Math.random() * 10000, Math.random() * 10000, idTorres);//criacao do novo ponto aleatório
            //fim criacao de um ponto aletório

            cpypercurso = null;//apago a lista
            cpypercurso = this.copiaLista(this.percursoFinal);//criacao de uma copia do percurso pois caso não haja um percurso melhor que ele, devemos manter ele e nao alterá-lo

            //Captura por quais torres o ponto esta abrangendo
            for (Torre t : torres) {
                if (estaDentro(t, pontoAleatorio)) {//se o ponto está dentro de uma área da torre
                    pontoAleatorio.idTorres.add(t.id);//adiciono no ponto, o id das torres que ele abrange
                }
            }
            //fim Captura

            //Para cada torre abrangida pelo ponto aleatório, retirar as torres dos outros pontos que abrangem estes
            excluirIdTorres(cpypercurso, pontoAleatorio.idTorres);

            //inicio remover os pontos que nao abragem nenhuma torre, ou seja, se sua linkedlist está vazia
            limparPontosVazios(cpypercurso);

            //fim remove
            //inicio teste em qual posição melhor este novo ponto se encaixa
            int melhorPosição = -10;
            double distancia2 = distancia;
            if (!pontoAleatorio.idTorres.isEmpty()) {
                for (int i = 0; i <= cpypercurso.size(); i++) {//testando em todas posições na ordem que fica melhor
                    this.percursoFinal.add(i, pontoAleatorio);
                    double distanciaSimulada = calculaDistanciaRota(this.percursoFinal);
                    if (distanciaSimulada < distancia2) {//se a distancia nessa posição for melhor que a anterior, considero esta
                        melhorPosição = i;//salvo a posição que se encaixa melhor
                        System.out.println("Nova distancia top no HC");
                        distancia2 = distanciaSimulada;//salvo a distancia
                    }
                    this.percursoFinal.remove(i);
                }
            }
            if (melhorPosição != -10) {
                cpypercurso.add(melhorPosição, pontoAleatorio);//insiro o novo ponto na nova posição, melhor achada
                this.percursoFinal = this.copiaLista(cpypercurso);
                System.out.println("Nova distancia Hillclimb: " + distancia2);
                distancia = distancia2;
                repaint();
            } //fim teste

        }
        System.out.println("Fim HillClimb");
    }

    public LinkedList copiaLista(LinkedList<Ponto> a) {
        LinkedList<Ponto> retorno = new LinkedList();
        for (Ponto x : a) {
            LinkedList<Integer> k = new LinkedList();
            for (Integer y : x.idTorres) {
                k.add(y);
            }
            retorno.add(new Ponto(x.x, x.y, k));
        }
        return retorno;
    }

    public LinkedList<Torre> copiaTorres(LinkedList<Torre> a) {
        LinkedList<Torre> retorno = new LinkedList();
        for (Torre x : a) {
            retorno.add(new Torre(x.id, x.x, x.y, x.raio));
        }
        return retorno;
    }

    public void excluirIdTorres(LinkedList<Ponto> umPercurso, LinkedList<Integer> torresAbrangidas) {
        for (Integer idTorreAbrangida : torresAbrangidas) {
            for (Ponto ponto : umPercurso) {//para cada ponto na copia do percurso
                if (ponto.idTorres.contains(idTorreAbrangida)) {
                    ponto.idTorres.remove(idTorreAbrangida);//removendo as torres dos pontos que estão abrangindo
                    break;
                }
            }
        }
    }

    public void limparPontosVazios(LinkedList<Ponto> umPercurso) {
        LinkedList<Integer> posicoesPontosVazios = new LinkedList();
        int i = 0;
        for (Ponto x : umPercurso) {
            if (x.idTorres.isEmpty()) {
                posicoesPontosVazios.add(i);
            }
            i++;
        }
        i = 0;
        for (Integer posicao : posicoesPontosVazios) {
            umPercurso.remove(posicao - i);
            i++;
        }
    }

    public LinkedList<Ponto> cruzamento(LinkedList<Ponto> pai, LinkedList<Ponto> mae) {
        LinkedList<Ponto> copyPai = this.copiaLista(pai);
        LinkedList<Ponto> copyMae = this.copiaLista(mae);
        LinkedList<Ponto> filho = new LinkedList();

        int qtdTorres = 0;
        boolean isPai = true;

        while (qtdTorres != this.qtdTorres) {
            if (isPai) {//pegamos um ponto do pai
                int posicaoPonto = (int) ((Math.random() * copyPai.size()) - 1);
                Ponto alelo = copyPai.remove(posicaoPonto);
                //filho.add(alelo);
                excluirIdTorres(copyMae, alelo.idTorres);
                limparPontosVazios(copyMae);
                qtdTorres += alelo.idTorres.size();
                int melhorPosição = 0;
                filho.add(0, alelo);
                double distancia = calculaDistanciaRota(filho);
                filho.remove(0);
                for (int i = 1; i <= filho.size(); i++) {//testando em todas posições na ordem que fica melhor
                    filho.add(i, alelo);//adiciono na posição escrita
                    double distanciaSimulada = calculaDistanciaRota(filho);
                    if (distanciaSimulada < distancia) {//se a distancia nessa posição for melhor que a anterior, considero esta
                        melhorPosição = i;//salvo a posição que se encaixa melhor
                        distancia = distanciaSimulada;//salvo a distancia
                    }
                    filho.remove(i);
                }
                filho.add(melhorPosição, alelo);
            } else {//pegamos um ponto da mae
                int posicaoPonto = (int) ((Math.random() * copyMae.size()) - 1);
                Ponto alelo = copyMae.remove(posicaoPonto);
                //filho.add(alelo);
                excluirIdTorres(copyPai, alelo.idTorres);
                limparPontosVazios(copyPai);
                qtdTorres += alelo.idTorres.size();
                int melhorPosição = 0;
                filho.add(0, alelo);
                double distancia = calculaDistanciaRota(filho);
                filho.remove(0);
                for (int i = 1; i <= filho.size(); i++) {//testando em todas posições na ordem que fica melhor
                    filho.add(i, alelo);//adiciono na posição escrita
                    double distanciaSimulada = calculaDistanciaRota(filho);
                    if (distanciaSimulada < distancia) {//se a distancia nessa posição for melhor que a anterior, considero esta
                        melhorPosição = i;//salvo a posição que se encaixa melhor
                        distancia = distanciaSimulada;//salvo a distancia
                    }
                    filho.remove(i);
                }
                filho.add(melhorPosição, alelo);

            }
            isPai = !isPai;
        }

        return filho;
    }

    public boolean estaDentro(Torre t1, Ponto p2) {
        //se a distancia entre os ponto for menor ou igual ao raio, o ponto está dentro da circunferencia da torre
        if ((Math.sqrt(Math.pow((t1.x - p2.x), 2) + Math.pow((t1.y - p2.y), 2))) <= t1.raio) {
            return true;
        }
        return false;
    }

    public double calculaDistanciaRota(LinkedList<Ponto> pontosRota) {
        double distanciaTotal = 0;
        for (int i = 0; i < pontosRota.size() - 1; i++) {
            distanciaTotal += calculaDistancia(pontosRota.get(i), pontosRota.get(i + 1));
        }
        distanciaTotal += calculaDistancia(pontosRota.get(pontosRota.size() - 1), pontosRota.get(0));//distancia do último ao primeiro
        return distanciaTotal;
    }

    // Do all 2-opt combinations
    private LinkedList<Ponto> TwoOpt() {
        // Get tour size
        int size = this.percursoFinal.size();//tamanho do percurso

        LinkedList<Ponto> newTour = new LinkedList();//criacoa do novo percurso

        //CHECK THIS!!
        newTour = copiaLista(this.percursoFinal);

        //repete até que nenhuma melhoria seja feita
        int improve = 0;//melhorar
        int iteration = 0;//iteracao

        while (improve < 800) {
            double best_distance = calculaDistanciaRota(this.percursoFinal);

            for (int i = 1; i < size - 1; i++) {
                for (int k = i + 1; k < size; k++) {
                    TwoOptSwap(i, k, newTour);
                    iteration++;
                    double new_distance = calculaDistanciaRota(newTour);

                    if (new_distance < best_distance) {
                        // Improvement found so reset
                        improve = 0;

                        //zerar o percurso final -> fazer apontar para o
                        this.percursoFinal = null;
                        //fim zerar

                        this.percursoFinal = this.copiaLista(newTour);
                        System.out.println("New distancia: " + new_distance);
//                        for (int j = 0; j < size; j++) {
//                            percursosPop.set(j, newTour.get(j));
//                        }
                        best_distance = new_distance;

                        this.repaint();;
                    }
                }
            }

            improve++;
        }
        System.out.println("Finalizado");
        return this.percursoFinal;
    }

    void TwoOptSwap(int i, int k, LinkedList<Ponto> newTour) {
        int size = this.percursoFinal.size();

        // 1. pegar rota [0] até rota [i-1] e adicioná-los na new_route
        for (int c = 0; c <= i - 1; ++c) {
            newTour.set(c, this.percursoFinal.get(c));
        }

        // 2. tomar rota [i] até rota [k] e adicioná-los na ordem inversa na new_route
        int dec = 0;
        for (int c = i; c <= k; ++c) {
            newTour.set(c, this.percursoFinal.get(k - dec));
            dec++;
        }

        // 3. pegue a rota [k + 1] até o fim e adicione-a na new_route
        for (int c = k + 1; c < size; ++c) {
            newTour.set(c, this.percursoFinal.get(c));
        }
    }

    public void calculaRota(int pontoInicial) {

        for (int pop = 0; pop < 1; pop++) {//criacao dos 100 individuos da população
            LinkedList<Ponto> percurso = new LinkedList();//linkedlist que possui os pontos do percurso
            LinkedList<Torre> copiaTorres = copiaTorres(this.torres);//copio torres pois no metodo de calcular a rota eu sempre passo por todas as torres

            //Criacao da solução inicial
            //criado o primeiro nó do percurso
            LinkedList<Integer> idTorre = new LinkedList();
            idTorre.add(copiaTorres.get(0).id);//adiciono o id da torre, no caso é a primeira, então o id é zero
            percurso.add(new Ponto(copiaTorres.get(0).x, copiaTorres.get(0).y, idTorre));
            copiaTorres.remove(0);//removo o primeiro nó pois já foi inserido no percurso
            //fim criar primeiro nó

            //inicio demais nós
            int qtdVezes = copiaTorres.size();
            for (int j = 0; j < qtdVezes; j++) {
                int tamanho = copiaTorres.size();
                int posicao = 0;

                double distancia = Double.POSITIVE_INFINITY;

                Ponto ultimoPontoAddNoPercurso = new Ponto(percurso.get(percurso.size() - 1).x, percurso.get(percurso.size() - 1).y, new LinkedList());
                int i;

                for (i = 0; i < tamanho; i++) {

                    double novaDistanciaCalculada = calculaDistancia(ultimoPontoAddNoPercurso, new Ponto(copiaTorres.get(i).x, copiaTorres.get(i).y, new LinkedList<>()));

                    if (novaDistanciaCalculada < distancia) {
                        posicao = i;
                        distancia = novaDistanciaCalculada;
                    }

                }

                LinkedList<Integer> id = new LinkedList();//crio o LL para o novo ponto a ser colocado no percurso
                id.add(copiaTorres.get(posicao).id);//adiciono o id do ponto
                percurso.add(new Ponto(copiaTorres.get(posicao).x, copiaTorres.get(posicao).y, id));//adiciono o ponto ao fim do percurso
                copiaTorres.remove(posicao);//removo esse ponto já adicionado

            }//fim demais nós

            //fim Criacao da solução inicial
            double distancia = calculaDistanciaRota(percurso);//calcula valor da rota inicial
            System.out.println("Valor rota inicial: " + distancia);
            LinkedList<Ponto> cpypercurso;
            this.repaint();

            this.percursoFinal = this.copiaLista(percurso);

            //INICIO HILLCLIMB
            for (int l = 0; l < 150000; l++) {
                //criancao de um ponto aleatório
                LinkedList<Integer> idTorres = new LinkedList();//Linkedlist para o novo ponto ver quais torres ele abrange
                Ponto pontoAleatorio = new Ponto(Math.random() * 10000, Math.random() * 10000, idTorres);//criacao do novo ponto aleatório
                //fim criacao de um ponto aletório

                cpypercurso = null;//apago a lista
                cpypercurso = this.copiaLista(percurso);//criacao de uma copia do percurso pois caso não haja um percurso melhor que ele, devemos manter ele e nao alterá-lo

                //Captura por quais torres o ponto esta abrangendo
                for (Torre t : torres) {
                    if (estaDentro(t, pontoAleatorio)) {//se o ponto está dentro de uma área da torre
                        pontoAleatorio.idTorres.add(t.id);//adiciono no ponto, o id das torres que ele abrange
                    }
                }
                //fim Captura

                //Para cada torre abrnagida pelo ponto aleatório, retirar as torres dos outros pontos que abrangem estes
                excluirIdTorres(cpypercurso, pontoAleatorio.idTorres);

                //inicio remover os pontos que nao abragem nenhuma torre, ou seja, se sua linkedlist está vazia
                limparPontosVazios(cpypercurso);

                //fim remove
                //inicio teste em qual posição melhor este novo ponto se encaixa
                int melhorPosição = -10;
                double distancia2 = distancia;
                if (!pontoAleatorio.idTorres.isEmpty()) {
                    for (int i = 0; i <= cpypercurso.size(); i++) {//testando em todas posições na ordem que fica melhor
                        cpypercurso.add(i, pontoAleatorio);//adiciono na posição escrita
                        double distanciaSimulada = calculaDistanciaRota(cpypercurso);
                        if (distanciaSimulada < distancia2) {//se a distancia nessa posição for melhor que a anterior, considero esta
                            melhorPosição = i;//salvo a posição que se encaixa melhor
                            distancia2 = distanciaSimulada;//salvo a distancia
                        }
                        cpypercurso.remove(i);
                    }
                }

                if (melhorPosição != -10) {
                    cpypercurso.add(melhorPosição, pontoAleatorio);//insiro o novo ponto na nova posição, melhor achada
                    this.percursoFinal = this.copiaLista(cpypercurso);//salvo o percurso final após ter inserido o nó
                    percurso = this.copiaLista(cpypercurso);
                    distancia = distancia2;
                    System.out.println("nova distancia: " + distancia);
                    repaint();
                } //fim teste
            }
//            this.populacao.add(this.percursoFinal);//adiciono um individuo na populacao
        }
//        double distancia = Double.POSITIVE_INFINITY;
//        int i = 0;
//        int posicaoMenorDistancia = 0;
//        for (LinkedList<Ponto> percurso : populacao) {
//            if (calculaDistanciaRota(percurso) < distancia) {
//                distancia = calculaDistanciaRota(percurso);
//                posicaoMenorDistancia = i;
//            }
//            i++;
//        }
//        this.percursoFinal = copiaLista(populacao.get(posicaoMenorDistancia));
//        repaint();

//        int qtdCruzmentos = 0;
//        while (qtdCruzmentos < 100) {
//            for (int j = 0; j < 10; j++) {//adiciono 10 novos individuos
//                int posicaoPai = (int) ((Math.random() * populacao.size()) - 1);
//                int posicaoMae = (int) ((Math.random() * populacao.size()) - 1);;
//                while (posicaoPai == posicaoMae) { //caso seja a mesma pessoa
//                    posicaoPai = (int) ((Math.random() * populacao.size()) - 1);
//                    posicaoMae = (int) ((Math.random() * populacao.size()) - 1);;
//                }
//                LinkedList<Ponto> filhoDoCruzamento = cruzamento(populacao.get(posicaoPai), populacao.get(posicaoMae));
//                double distanciaFilho = calculaDistanciaRota(filhoDoCruzamento);
//                if (distanciaFilho < distancia) {
//                    distancia = distanciaFilho;
//                    this.percursoFinal = copiaLista(filhoDoCruzamento);
//                    System.out.println("nova distancia: " + distancia);
//                    repaint();
//                }
//                populacao.add(filhoDoCruzamento);
//            }
//            for (int j = 0; j < 10; j++) {
//                int oponente1 = (int) ((Math.random() * populacao.size()) - 1);
//                int oponente2 = (int) ((Math.random() * populacao.size()) - 1);;
//                while (oponente1 == oponente2) { //caso seja a mesma pessoa
//                    oponente1 = (int) ((Math.random() * populacao.size()) - 1);
//                    oponente2 = (int) ((Math.random() * populacao.size()) - 1);;
//                }
//                if (calculaDistanciaRota(populacao.get(oponente1)) < calculaDistanciaRota(populacao.get(oponente2))) {
//                    populacao.remove(oponente1);
//                } else {
//                    populacao.remove(oponente2);
//                }
//            }
//            qtdCruzmentos++;
//
//        }//fim while(true)
        System.out.println("fim");
        this.TwoOpt();
        System.out.println("Fim tudo");
        System.out.println("Distancia Final: " + calculaDistanciaRota(percursoFinal));
        int qtdPontos = 0;
        for (Ponto a : this.percursoFinal) {
            qtdPontos += a.idTorres.size();
        }
        System.out.println("Qtd pontos: " + qtdPontos);
    }

}
