package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.utils.TrainFileManager;

import java.util.Random;

import static ch.idsia.utils.TrainFileManager.MARIO_IN_MATRIX;

public class P1BotAgent extends BasicMarioAIAgent implements Agent {

    private static final int MATRIX_SIZE = 19;
    private Random R = null;
    private Environment environment;
    private TrainFileManager trainFileManager;
    private float[] posicionAnterior;
    private double prevClosestEnemy = -1;
    private double prevClosestObject = -1;
    private double distanceClosestEnemy;
    private double distanceClosestObject;
    private static int limit_vision_depth = 3;
    private boolean prevEnemyAproximating = false;
    private boolean prevObjectAproximating = false;
    private boolean prevMarioIsMoving = false;
    private boolean marioCanWalkThrough;
    private int prevDistancePassedPhys = -1;
    private int ticks = 0;
    private int closestEnemyLatitude;
    private int closestObjectLatitude;
    private int objectHeight;
    private int enemyHeight;
    private int ticks_mario_salto = 0;
    private int enemigosEnPantalla;
    private int ladrillosEnPantalla;
    private int monedasEnPantalla;
    private int intermediateReward;
    private boolean enemyApproximating;
    private boolean objectApproximating;
    private boolean marioIsMoving;
    private boolean marioIsAbleToJump;
    private double distanceClosestCoin;
    private int[] infoEvaluacion;
    byte[][] env;
    private String actionMario;



    public P1BotAgent() {
        super("P1BotAgent");
        reset();
        posicionAnterior = new float[]{0, 0};
        trainFileManager = new TrainFileManager("P1BotAgent.arff");
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
        action[Mario.KEY_RIGHT] = false;
        action[Mario.KEY_JUMP] = false;
        obtenerDatos();
        env = environment.getMergedObservationZZ(2, 1);

        /*Selección de acción T2B*/
        if (ticks_mario_salto <= 5) {
            if (distanceClosestObject <= 1.414214) {
                action[Mario.KEY_RIGHT] = true;
                action[Mario.KEY_JUMP] = true;
            } else {
                if (distanceClosestEnemy <= 2.8288427) {
                    action[Mario.KEY_RIGHT] = true;
                    action[Mario.KEY_JUMP] = true;
                } else {
                    if (env[11][10] <= 0) {
                        if (enemyApproximating) {
                            action[Mario.KEY_RIGHT] = true;
                            action[Mario.KEY_JUMP] = true;
                        } else {
                            action[Mario.KEY_RIGHT] = true;
                        }
                    } else {
                        action[Mario.KEY_RIGHT] = true;
                    }
                }
            }
        } else {
            action[Mario.KEY_RIGHT] = true;
        }
        trainFileManager.escrituraDatos(environment, action);
        ticks++;
        return action;
    }

    private void obtenerDatos() {
        enemigosEnPantalla = 0;
        ladrillosEnPantalla = 0;
        monedasEnPantalla = 0;
        intermediateReward = environment.getIntermediateReward();
        enemyApproximating = prevEnemyAproximating;
        objectApproximating = prevObjectAproximating;
        marioIsMoving = prevMarioIsMoving;
        marioIsAbleToJump = environment.isMarioAbleToJump();
        distanceClosestCoin = 100;
        StringBuilder datos = new StringBuilder();

        closestEnemyLatitude = -10;
        closestObjectLatitude = -10;
        enemyHeight = 0;
        objectHeight = 0;
        distanceClosestObject = 100;
        distanceClosestEnemy = 100;
        marioCanWalkThrough = true;

        env = environment.getMergedObservationZZ(2, 1);

        infoEvaluacion = environment.getEvaluationInfoAsInts();


        /*Cálculo de mergedMatrix, closestEnemy, closestEnemyLatitude, closestObject, closestObjectLatitude,
         * ladrillosEnPantalla y enemigosEnPantalla */
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                if (TrainFileManager.cellInMarioVision(i, j)) {
                    datos.append(String.valueOf(env[i][j])).append(", ");
                    /*Se calcula la distancia y latitud de los enemigos del entorno*/
                    if ((env[i][j] == 80)) {
                        enemigosEnPantalla++;
                        if (TrainFileManager.euclideanDistance(MARIO_IN_MATRIX, MARIO_IN_MATRIX, i, j) < distanceClosestEnemy) {
                            distanceClosestEnemy = TrainFileManager.euclideanDistance(MARIO_IN_MATRIX, MARIO_IN_MATRIX, i, j);
                            closestEnemyLatitude = (i - MARIO_IN_MATRIX) * (-1);
                            enemyHeight = TrainFileManager.calcObstacleHeight(i, j, env);
                            if (marioCanWalkThrough)
                                marioCanWalkThrough = TrainFileManager.canMarioWalkThrough(j, env, infoEvaluacion, true);
                        }
                    }
                    /*Se calcula la distancia y latitud de los objetos que se encuentren en el campo de visión de mario*/
                    if (env[i][j] == -60 || env[i][j] == 1) {
                        ladrillosEnPantalla++;
                        if ((TrainFileManager.euclideanDistance(MARIO_IN_MATRIX, MARIO_IN_MATRIX, i, j) < distanceClosestObject)
                                && !TrainFileManager.canMarioWalkThrough(j, env, infoEvaluacion, false)) {
                            distanceClosestObject = TrainFileManager.euclideanDistance(MARIO_IN_MATRIX, MARIO_IN_MATRIX, i, j);
                            closestObjectLatitude = (i - MARIO_IN_MATRIX) * (-1);
                            objectHeight = TrainFileManager.calcObstacleHeight(i, j, env);
                            if (marioCanWalkThrough)
                                marioCanWalkThrough = TrainFileManager.canMarioWalkThrough(j, env, infoEvaluacion, false);
                        }
                    }
                }
            }
        }

        if (!environment.isMarioAbleToJump()) ticks_mario_salto++;
        else ticks_mario_salto = 0;

        /*Comproación cada diez tics si existe un aproximamiento de enemigos u objetos */
        if (ticks % 5 == 0) {
            enemyApproximating = prevClosestEnemy > distanceClosestEnemy;
            objectApproximating = prevClosestObject > distanceClosestObject;
            marioIsMoving = prevDistancePassedPhys < infoEvaluacion[1];
            prevEnemyAproximating = enemyApproximating;
            prevObjectAproximating = objectApproximating;
            prevMarioIsMoving = marioIsMoving;
            prevClosestEnemy = distanceClosestEnemy;
            prevClosestObject = distanceClosestObject;
            prevDistancePassedPhys = infoEvaluacion[1];
        }

        /*Los booleanos que representan las acciones se codifican en 0 o 1 */
        double action_code = 0;
        for (int i = 0; action.length > i; i++) {
            if (action[i]) action_code = action_code + Math.pow(2, i);
        }

        /*Asignación de valor dependiendo de código binario*/
        switch ((int) action_code) {
            case 2:
                actionMario = "RIGHT";
                break;
            case 8:
                actionMario = "JUMP";
                break;
            case 10:
                actionMario = "JUMP_RIGHT";
                break;
            default:
                actionMario = "NONE";
                break;
        }
    }
}
