package ch.idsia.utils;

import ch.idsia.benchmark.mario.environments.Environment;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class TrainFileManager {
    private BufferedWriter bw = null;
    private Regresion regresion = null;
    private String ruta;
    private String prevAction = "";
    private static double f1Coef;
    private static double f2Coef;
    private static double f1Const;
    private static double f2Const;
    private double prevClosestEnemy = -1;
    private double prevClosestObject = -1;
    private double distanceClosestEnemy;
    private double distanceClosestObject;
    private boolean prevEnemyAproximating = false;
    private boolean prevObjectAproximating = false;
    private boolean prevMarioIsMoving = false;
    private boolean marioCanWalkThrough;
    private int prevDistancePassedPhys = -1;
    private int tick_aproximation = 0;
    private int closestEnemyLatitude;
    private int closestObjectLatitude;
    private int objectHeight;
    private int enemyHeight;
    private int ticks_mario_salto = 0;

    //Variables para configurar la regresión
    private boolean esRegresion = false;


    public static final int LIMIT_VISION_DEPTH = 6;
    public static final int MATRIX_SIZE = 19;
    public static final int MARIO_IN_MATRIX = 9;
    public static final double DATA_RECORDING_DISTANCE = 5;
    private static final double DEGREES_MARIO_VISION = 70;

    public TrainFileManager(String ruta) {
        if (esRegresion) {
            this.ruta = Regresion.getFileName(ruta);
        } else {
            this.ruta = ruta;
        }
        FileWriter fw = null;
        calcMarioVisionParam(DEGREES_MARIO_VISION);
        checkHeader();

        try {
            fw = new FileWriter(this.ruta, true);
        } catch (IOException e) {
            System.out.println("El archivo no se ha podido crear.");
            e.printStackTrace();
        }
        if (fw != null) {
            bw = new BufferedWriter(fw);
        }
        regresion = new Regresion(bw);
    }


    /**
     * Método que lee el archivo con los datos de entrenamiento y borra la última línea de este.
     */
    public void deleteLastLine() {
        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile(ruta, "rw");
            long length;
            length = f.length() - 1;
            byte b = 0;
            while (b != 10 && length > 0) {
                length -= 1;
                f.seek(length);
                b = f.readByte();
            }
            if (length == 0) {
                f.setLength(length);
            } else {
                f.setLength(length + 1);
            }
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que comprueba si hay una cabecera escrita, y, si no, la escribe
     */
    private void checkHeader() {
        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile(ruta, "rw");
            byte[] b = new byte[9];
            f.read(b, 0, 9);
            if (!Objects.equals(new String(b), "@RELATION")) {
                f.seek(0);
                if (esRegresion) {
                    f.write(TrainAttributtes.buildCabeceraRegresion().getBytes());
                } else {
                    f.write(TrainAttributtes.buildCabecera().getBytes());
                }
            }
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Escritura del archivo con los datos de entrenamiento que se pueden obtener del estado del mundo. Se van
     * concatenando valores con comas para finalizar con una única cadena de caracteres que contenga todos los datos
     * separados por comas y puedan ser escritos de una sola vez.
     */
    public void escrituraDatos(Environment environment, boolean[] action) {
        int enemigosEnPantalla = 0;
        int ladrillosEnPantalla = 0;
        int monedasEnPantalla = 0;
        int intermediateReward = environment.getIntermediateReward();
        boolean enemyApproximating = prevEnemyAproximating;
        boolean objectApproximating = prevObjectAproximating;
        boolean marioIsMoving = prevMarioIsMoving;
        boolean marioIsAbleToJump = environment.isMarioAbleToJump();
        double distanceClosestCoin = 100;
        String actionMario;
        StringBuilder datos = new StringBuilder();

        closestEnemyLatitude = -10;
        closestObjectLatitude = -10;
        enemyHeight = 0;
        objectHeight = 0;
        distanceClosestObject = 100;
        distanceClosestEnemy = 100;
        marioCanWalkThrough = true;


        byte[][] env;
        env = environment.getMergedObservationZZ(2, 1);

        int[] infoEvaluacion;
        infoEvaluacion = environment.getEvaluationInfoAsInts();

        /*Cálculo de mergedMatrix, closestEnemy, closestEnemyLatitude, closestObject, closestObjectLatitude,
         * ladrillosEnPantalla y enemigosEnPantalla */
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                if (cellInMarioVision(i, j)) {

                    if ( j < MARIO_IN_MATRIX + LIMIT_VISION_DEPTH) datos.append(String.valueOf(env[i][j])).append(", ");
                    /*Se calcula la distancia y latitud de los enemigos del entorno*/
                    if ((env[i][j] == 80)) {
                        enemigosEnPantalla++;
                        if (euclideanDistance(MARIO_IN_MATRIX, MARIO_IN_MATRIX, i, j) < distanceClosestEnemy) {
                            calcEnemObjData(i, j, env, infoEvaluacion, true);
                        }
                    }
                    /*Se calcula la distancia y latitud de los objetos que se encuentren en el campo de visión de mario*/
                    if (env[i][j] == -60 || env[i][j] == 1) {
                        ladrillosEnPantalla++;
                        if ((euclideanDistance(MARIO_IN_MATRIX, MARIO_IN_MATRIX, i, j) < distanceClosestObject)
                                && !canMarioWalkThrough(j, env, infoEvaluacion, false)) {
                            calcEnemObjData(i, j, env, infoEvaluacion, false);
                        }
                    }
                    if (esRegresion) {
                        if (env[i][j] == 2) {
                            monedasEnPantalla++;
                            if (euclideanDistance(MARIO_IN_MATRIX, MARIO_IN_MATRIX, i, j) < distanceClosestCoin) {
                                distanceClosestCoin = euclideanDistance(MARIO_IN_MATRIX, MARIO_IN_MATRIX, i, j);
                            }
                        }
                    }
                }
            }
        }

        if (!environment.isMarioAbleToJump()) ticks_mario_salto++;
        else ticks_mario_salto = 0;

        /*Comproación cada diez tics si existe un aproximamiento de enemigos u objetos */
        if (tick_aproximation % 5 == 0) {
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

        /*Escritura de atributos a incluir en archivo .arff*/
        datos.append(String.valueOf(infoEvaluacion[7]).concat(", "))
                .append(String.valueOf(infoEvaluacion[8]).concat(", "))
                .append(String.valueOf(marioIsMoving).toUpperCase().concat(", "))
                .append(String.valueOf(marioIsAbleToJump).toUpperCase().concat(", "))
                .append(String.valueOf(ladrillosEnPantalla).concat(", "))
                .append(String.valueOf(enemigosEnPantalla).concat(", "))
                .append(String.valueOf(infoEvaluacion[6]).concat(", "))
                .append(String.valueOf(distanceClosestEnemy).concat(", "))
                .append(String.valueOf(distanceClosestObject).concat(", "))
                .append(String.valueOf(enemyApproximating).toUpperCase().concat(", "))
                .append(String.valueOf(objectApproximating).toUpperCase().concat(", "))
                .append(String.valueOf(closestEnemyLatitude).concat(", "))
                .append(String.valueOf(closestObjectLatitude).concat(", "))
                .append(String.valueOf(enemyHeight).concat(", "))
                .append(String.valueOf(objectHeight).concat(", "))
                .append(String.valueOf(marioCanWalkThrough).toUpperCase().concat(", "));

        // La clase cambia dependiendo de si se hace una clasificación o una regresión.
        if (esRegresion) {
            datos.append(String.valueOf(monedasEnPantalla).concat(", "));
            datos.append(String.valueOf(distanceClosestCoin).concat(", "));
            datos.append(String.valueOf(intermediateReward).concat(", "));
            if (marioIsMoving) {
                regresion.escribirRegresion(datos.toString(), intermediateReward);
            }
        } else {
            datos.append(String.valueOf(ticks_mario_salto).concat(", "));
            datos.append(actionMario.concat("\n"));

            /*Grabado de datos recopilados*/
            if (marioIsMoving) {
                if (!prevAction.equals(actionMario)) {
                    try {
                        bw.write(datos.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (Math.random() < 0.1) {
                    try {
                        bw.write(datos.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        prevAction = actionMario;
        tick_aproximation++;
    }

    public void lecturaDatos(String ruta, int ticks) {
        BufferedReader br = null;
        FileReader fr = null;
        boolean readData = false;
        ArrayList<IBLInstance> IBLList = new ArrayList<IBLInstance>();
        String sCurrentLine;

        try {

            fr = new FileReader(ruta);
            br = new BufferedReader(fr);
            sCurrentLine = br.readLine();

            while (sCurrentLine != null) {
                if (readData) {
                    IBLList.add(new IBLInstance(sCurrentLine, "undefined"));
                }
                if (sCurrentLine.contains("@data")) readData = true;
                sCurrentLine = br.readLine();
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
    }

    /*Calcula la distanca euclidea entre dos celdas de la matriz*/
    public static double euclideanDistance(int posx1, int posy1, int posx2, int posy2) {
        return Math.sqrt(Math.pow(posx2 - posx1, 2) + Math.pow(posy2 - posy1, 2));
    }

    /*Calcula si la celda se encuentra dentro de la visión definida para Mario*/
    public static boolean cellInMarioVision(int i, int j) {
        int y = (i - 18) * (-1);
        int x = j;
        if (y - (x * f1Coef) - f1Const <= 0 && y - (x * f2Coef) - f2Const >= 0) return true;
        return false;
    }

    /*Calcula si Mario puede seguir avanzando hacia delante sin toparse con un objeto o enemigo*/
    public static boolean canMarioWalkThrough(int j, byte[][] env, int[] infoEvaluacion, boolean isEnemy) {
        if (isEnemy) {
            if (infoEvaluacion[7] == 0) {
                for (int y = j; y > MARIO_IN_MATRIX; y--) {
                    if (env[MARIO_IN_MATRIX][j] == 80) return false;
                }
            } else {
                for (int y = j; y > MARIO_IN_MATRIX; y--) {
                    if ((env[MARIO_IN_MATRIX][j] == 80) || (env[MARIO_IN_MATRIX - 1][j] == 80)) return false;
                }
            }
            return true;
        } else {
            if (infoEvaluacion[7] == 0) {
                for (int y = j; y > MARIO_IN_MATRIX; y--) {
                    if (env[MARIO_IN_MATRIX][j] != 0 && env[MARIO_IN_MATRIX][j] != 2 && env[9][j] != 80) return false;
                }
            } else {
                for (int y = j; y > MARIO_IN_MATRIX; y--) {
                    if ((env[MARIO_IN_MATRIX][j] != 0 && env[MARIO_IN_MATRIX][j] != 2 && env[MARIO_IN_MATRIX][j] != 80)
                            || (env[MARIO_IN_MATRIX - 1][j] != 0 && env[MARIO_IN_MATRIX - 1][j] != 2 && env[MARIO_IN_MATRIX - 1][j] != 80))
                        return false;
                }
            }
            return true;
        }
    }

    /*Calcula la altura de un enemigo u objeto en relación a la altura de Mario*/
    public static int calcObstacleHeight(int i, int j, byte[][] env) {
        int altura = i;
        while (altura > 0 && env[i][j] == env[altura][j]) altura--;
        altura = i - altura;
        return altura;
    }

    /*Calcula distintos atributos de objetos y enemigos*/
    public void calcEnemObjData(int i, int j, byte[][] env, int[] infoEvaluacion, boolean isEnemy) {
        if (isEnemy) {
            distanceClosestEnemy = euclideanDistance(MARIO_IN_MATRIX, MARIO_IN_MATRIX, i, j);
            closestEnemyLatitude = (i - MARIO_IN_MATRIX) * (-1);
            enemyHeight = calcObstacleHeight(i, j, env);
        } else {
            distanceClosestObject = euclideanDistance(MARIO_IN_MATRIX, MARIO_IN_MATRIX, i, j);
            closestObjectLatitude = (i - MARIO_IN_MATRIX) * (-1);
            objectHeight = calcObstacleHeight(i, j, env);
        }
        if (marioCanWalkThrough) marioCanWalkThrough = canMarioWalkThrough(j, env, infoEvaluacion, isEnemy);
    }

    /*Calcula las funciones de vision de Mario que acotan el grabado de datos de merged_matrix*/
    public static void calcMarioVisionParam(double percentage) {
        percentage = Math.toRadians(percentage % 90);
        double f1_x1 = MARIO_IN_MATRIX;
        double f1_y1 = MARIO_IN_MATRIX;
        double f1_x2 = f1_x1 + 1;
        double f1_y2 = (Math.sin(percentage) / Math.cos(percentage)) + f1_y1;
        f1Coef = (f1_y2 - f1_y1) / (f1_x2 - f1_x1);
        f1Const = f1_y1 - (f1Coef * f1_x1);
        f2Coef = f1Coef * (-1);
        f2Const = (2 * MARIO_IN_MATRIX) - f1Const;
    }
}
