/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.utils.TrainFileManager;

import java.util.Random;

public class T3BotAgent extends BasicMarioAIAgent implements Agent {

    private int ticks;
    private Random R = null;
    private Environment environment;
    private float[] posicionAnterior;
    private TrainFileManager trainFileManager;

    public T3BotAgent() {
        super("T3BotAgent");
        reset();
        trainFileManager = new TrainFileManager("T3BotAgent.arff");
        posicionAnterior = new float[]{0, 0};
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
        byte[][] mergedMatrix = environment.getMergedObservationZZ(2, 1);

        action[Mario.KEY_RIGHT] = true;

        if (environment.isMarioAbleToJump()) {
            if ((mergedMatrix[10][10] == 0 && mergedMatrix[11][10] == 0) //hueco
                    //obstáculos
                    || mergedMatrix[8][10] == -60
                    || mergedMatrix[9][10] == -60
                    || mergedMatrix[9][10] == 1
                    || mergedMatrix[8][10] == 1
                    //enemigos
                    || mergedMatrix[8][10] == 80
                    || mergedMatrix[8][10] == 93
                    || mergedMatrix[8][10] == 25
                    || mergedMatrix[8][11] == 25
                    || mergedMatrix[8][11] == 80
                    || mergedMatrix[8][11] == 93
                    || mergedMatrix[9][10] == 25
                    || mergedMatrix[9][10] == 93
                    || mergedMatrix[9][10] == 80
                    || mergedMatrix[9][11] == 25
                    || mergedMatrix[9][11] == 80
                    || mergedMatrix[9][11] == 93
                    || mergedMatrix[10][10] == 25
                    || mergedMatrix[10][10] == 80
                    || mergedMatrix[10][10] == 93
                    || mergedMatrix[10][11] == 25
                    || mergedMatrix[10][11] == 80
                    || mergedMatrix[10][11] == 93) {
                action[Mario.KEY_JUMP] = true;
            } else {
                action[Mario.KEY_JUMP] = false;
            }
        }

        //Permite que el personaje deje de saltar cuando no avanza horizontalmente durante 15 ticks (se encuentra atascado).
        if (ticks % 15 == 0) {
            float[] marioFloatPos = environment.getMarioFloatPos();
            if (posicionAnterior[0] == marioFloatPos[0]) {
                action[Mario.KEY_JUMP] = false;
            }
            posicionAnterior = marioFloatPos;
        }
        System.out.printf("%d; %d; %d; %d\n", ticks, environment.getEvaluationInfo().distancePassedCells, environment.getIntermediateReward(), environment.getMarioMode());
        //trainFileManager.escrituraDatos(environment, action);
        ticks++;
        return action;
    }
}
