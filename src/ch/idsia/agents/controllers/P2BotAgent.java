package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.utils.TrainFileManager;

import java.util.Random;

public class P2BotAgent extends BasicMarioAIAgent implements Agent {

    private Random R = null;
    private Environment environment;
    private TrainFileManager trainFileManager;
    private int ticks = 0;

    public P2BotAgent() {
        super("P2BotAgent");
        reset();
        trainFileManager = new TrainFileManager("P2BotAgent.arff");
        trainFileManager.lecturaDatos("P1HumanAgent.arff", ticks);
        ticks = 0;
    }

    public void reset() {
        // Dummy reset, of course, but meet formalities!
        R = new Random();
    }

    /**
     * Se utiliza el parámetro para asignar valor al atributo de la clase con el mismo nombre.
     *
     * @param environment Objeto de tipo Environment con toda la información sobre el estado del mundo.
     */
    public void integrateObservation(Environment environment) {
        this.environment = environment;
    }

    /**
     * Se analizan los alrededores del personaje, de forma que avance a la derecha constantemente y salte cuando
     * tenga enemigos, obstáculos y pozos delante.
     *
     * @return action
     */
    public boolean[] getAction() {
        ticks++;
        return action;
    }
}
