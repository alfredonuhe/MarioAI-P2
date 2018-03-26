/*
 * Copyright (c) 2012-2013, Moisés Martínez
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
import ch.idsia.utils.TrainAttributtes;
import ch.idsia.utils.TrainFileManager;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by PLG Group.
 * User: Moisés Martínez
 * Date: Jan 24, 2014
 * Package: ch.idsia.controllers.agents.controllers;
 */
public final class T3HumanAgent extends KeyAdapter implements Agent {

    private boolean[] action = null;
    private String Name = "T3HumanAgent";
    private int ticks;
    private Environment environment;
    private TrainFileManager trainFileManager;

    public T3HumanAgent() {
        this.reset();
        trainFileManager = new TrainFileManager("T3HumanAgent.arff");
        ticks = 0;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public void setName(String name) {
        Name = name;
    }

    /**
     * Dado que para este agente no hay acción realizada automáticamente, en este método solo se hace lo necesario para
     * la escritura del archivo de datos de entrenamiento y se aumenta el contador de ticks.
     *
     * @return action
     */
    @Override
    public boolean[] getAction() {
        trainFileManager.escrituraDatos(environment, action);
        ticks++;
        return action;
    }

    /**
     * Se utiliza el parámetro para asignar valor al atributo de la clase con el mismo nombre.
     *
     * @param environment Objeto de tipo Environment con toda la información sobre el estado del mundo.
     */
    @Override
    public void integrateObservation(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void giveIntermediateReward(float intermediateReward) {
    }

    /**
     * Se crea el vector que almacena las acciones a realizar de Mario.
     */
    @Override
    public void reset() {
        action = new boolean[Environment.numberOfKeys];
    }

    @Override
    public void setObservationDetails(final int rfWidth, final int rfHeight, final int egoRow, final int egoCol) {
    }

    /**
     * Detección de las teclas presionadas por el usuario.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        toggleKey(e.getKeyCode(), true);
    }

    /**
     * Detección de liberación de tecla por el ususario.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        toggleKey(e.getKeyCode(), false);
    }

    /**
     * Se almacena en el vector Acciones los comandos del usuario a ejecutar por Mario.
     */
    private void toggleKey(int keyCode, boolean isPressed) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                action[Mario.KEY_LEFT] = isPressed;
                break;
            case KeyEvent.VK_RIGHT:
                action[Mario.KEY_RIGHT] = isPressed;
                break;
            case KeyEvent.VK_DOWN:
                action[Mario.KEY_DOWN] = isPressed;
                break;
            case KeyEvent.VK_UP:
                action[Mario.KEY_UP] = isPressed;
                break;

            case KeyEvent.VK_S:
                action[Mario.KEY_JUMP] = isPressed;
                break;
            case KeyEvent.VK_A:
                action[Mario.KEY_SPEED] = isPressed;
                break;
        }
    }

}
