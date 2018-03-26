package ch.idsia.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Regresion {
    private LinkedList<String> cadenaSeisTicks;
    private LinkedList<String> cadenaDoceTicks;
    private LinkedList<String> cadenaVeinticuatroTicks;
    private BufferedWriter bw = null;
    public static final int PORCENTAJE_RESTRICCION_ESCRITURA = 25;

    public static boolean REWARDN6 = false;
    public static boolean REWARDN12 = false;
    public static boolean REWARDN24 = true;

    public Regresion(BufferedWriter bw) {
        this.bw = bw;
        cadenaSeisTicks = new LinkedList<>();
        cadenaDoceTicks = new LinkedList<>();
        cadenaVeinticuatroTicks = new LinkedList<>();
    }

    public void escribirRegresion(String resultado, int intermediateReward) {
        if (REWARDN6) {
            cadenaSeisTicks.add(resultado);
            if (cadenaSeisTicks.size() == 6) {
                escribir(cadenaSeisTicks, intermediateReward);
            }
        } else if (REWARDN12) {
            cadenaDoceTicks.add(resultado);
            if (cadenaDoceTicks.size() == 12) {
                escribir(cadenaDoceTicks, intermediateReward);
            }
        } else if (REWARDN24) {
            cadenaVeinticuatroTicks.add(resultado);
            if (cadenaVeinticuatroTicks.size() == 24) {
                escribir(cadenaVeinticuatroTicks, intermediateReward);
            }
        }
    }

    private void escribir(LinkedList<String> listaCircular, int intermediateReward) {
        try {
            String cadenaFutura = getCadenaFutura(listaCircular, intermediateReward);
            if (!cadenaFutura.equals("")) {
                bw.write(cadenaFutura);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCadenaFutura(LinkedList<String> linkedList, int intermediateReward) {
        String cadenaExtraida = linkedList.remove();
        LinkedList<String> datosCadena = new LinkedList<>(Arrays.asList(cadenaExtraida.split(", ")));
        int rewardActual = Integer.parseInt(datosCadena.remove(datosCadena.size() - 1));
        int rewardFuturo = intermediateReward - rewardActual;

        if (rewardFuturo == 0) {
            if (new Random().nextInt(100 / PORCENTAJE_RESTRICCION_ESCRITURA) != 0) {
                return "";
            }
        }
        cadenaExtraida = datosCadena.toString()
                .replace("[", "").replace("]", "")
                .concat(", ")
                .concat(String.valueOf(rewardFuturo)).concat("\n");
        return cadenaExtraida;
    }

    public static String getFileName(String ruta) {
        if (Regresion.REWARDN6) {
            ruta = "regresion_n6_" + ruta;
        } else if (Regresion.REWARDN12) {
            ruta = "regresion_n12_" + ruta;
        } else if (Regresion.REWARDN24) {
            ruta = "regresion_n24_" + ruta;
        }
        return ruta;
    }

    public double estimarRecompensa2(double distanceClosestCoin, int monedasEnPantalla, int ladrillosEnPantalla, int enemigosEnPantalla, double distanceClosestObject, double distanceClosestEnemy) {
        double intermediateReward = 0;
        if (distanceClosestCoin <= 6.854) {
            if (monedasEnPantalla <= 4.5) {
                if (distanceClosestCoin <= 2.118) {
                    intermediateReward = -0.0007 * ladrillosEnPantalla - 0.0029 * enemigosEnPantalla - 0.0001 * distanceClosestEnemy - 0 * distanceClosestObject + 0.0186 * monedasEnPantalla - 0.0474 * distanceClosestCoin + 53.263;
                } else {
                    if (distanceClosestCoin <= 4.736) {
                        intermediateReward = -0.0007 * ladrillosEnPantalla - 0.0029 * enemigosEnPantalla - 0.0001 * distanceClosestEnemy - 0 * distanceClosestObject + 0.0186 * monedasEnPantalla - 0.073 * distanceClosestCoin + 71.4661;
                    } else {
                        intermediateReward = -0.0007 * ladrillosEnPantalla - 0.0029 * enemigosEnPantalla - 0.0001 * distanceClosestEnemy - 0 * distanceClosestObject + 0.0186 * monedasEnPantalla - 0.1471 * distanceClosestCoin + 41.6899;
                    }
                }
            } else {
                intermediateReward = -0.0007 * ladrillosEnPantalla - 0.0029 * enemigosEnPantalla - 0.0001 * distanceClosestEnemy - 0 * distanceClosestObject + 2.6274 * monedasEnPantalla - 9.6789 * distanceClosestCoin + 94.1622;
            }
        } else {
            if (distanceClosestCoin <= 8.972) {
                intermediateReward = -0.0038 * ladrillosEnPantalla - 0.0202 * enemigosEnPantalla + 0.0002 * distanceClosestEnemy - 0.0004 * distanceClosestObject + 0.0074 * monedasEnPantalla - 0.0015 * distanceClosestCoin + 26.3357;
            } else {
                if (ladrillosEnPantalla <= 7.5) {
                    if (enemigosEnPantalla <= 0.5) {
                        intermediateReward = -0.0038 * ladrillosEnPantalla - 0.0829 * enemigosEnPantalla + 0.0002 * distanceClosestEnemy - 0.0005 * distanceClosestObject + 0.0074 * monedasEnPantalla - 0.0007 * distanceClosestCoin + 18.8309;
                    } else {
                        intermediateReward = -0.0038 * ladrillosEnPantalla - 0.1077 * enemigosEnPantalla + 0.0002 * distanceClosestEnemy - 0.0005 * distanceClosestObject + 0.0074 * monedasEnPantalla - 0.0007 * distanceClosestCoin + 7.3673;
                    }
                } else {
                    if (distanceClosestObject <= 2.618) {
                        intermediateReward = -0.0028 * ladrillosEnPantalla - 0.0148 * enemigosEnPantalla + 0.0006 * distanceClosestEnemy - 0.0006 * distanceClosestObject + 0.0074 * monedasEnPantalla - 0.0007 * distanceClosestCoin + 14.5196;
                    } else {
                        if (distanceClosestEnemy <= 4.298) {
                            intermediateReward = -0.0028 * ladrillosEnPantalla - 0.0148 * enemigosEnPantalla + 0.0007 * distanceClosestEnemy - 0.0004 * distanceClosestObject + 0.0074 * monedasEnPantalla - 0.0007 * distanceClosestCoin + 0.5489;
                        } else {
                            intermediateReward = -0.0028 * ladrillosEnPantalla - 0.0148 * enemigosEnPantalla + 0.0005 * distanceClosestEnemy - 0.0004 * distanceClosestObject + 0.0074 * monedasEnPantalla - 0.0007 * distanceClosestCoin + 5.9227;
                        }
                    }
                }
            }
        }
        return intermediateReward;
    }

    public double estimarRecompensa(double distanceClosestCoin, int monedasEnPantalla, int ladrillosEnPantalla, double distanceClosestObject, double distanceClosestEnemy) {
        double intermediateReward = 0;
        if (distanceClosestCoin <= 6.854) {
            if (monedasEnPantalla <= 4.5) {
                if (distanceClosestCoin <= 2.118) {
                    intermediateReward = -0.0007 * ladrillosEnPantalla - 0.0001 * distanceClosestEnemy - 0 * distanceClosestObject + 0.0186 * monedasEnPantalla - 0.0474 * distanceClosestCoin + 53.2589;
                } else {
                    if (distanceClosestCoin <= 4.736) {
                        intermediateReward = -0.0007 * ladrillosEnPantalla - 0.0001 * distanceClosestEnemy - 0 * distanceClosestObject + 0.0186 * monedasEnPantalla - 0.073 * distanceClosestCoin + 71.462;
                    } else {
                        intermediateReward = -0.0007 * ladrillosEnPantalla - 0.0001 * distanceClosestEnemy - 0 * distanceClosestObject + 0.0186 * monedasEnPantalla - 0.1471 * distanceClosestCoin + 41.6858;
                    }
                }
            } else {
                intermediateReward = -0.0007 * ladrillosEnPantalla - 0.0001 * distanceClosestEnemy - 0 * distanceClosestObject + 2.6274 * monedasEnPantalla - 9.6789 * distanceClosestCoin + 94.1582;
            }
        } else {
            if (distanceClosestCoin <= 8.972) {
                intermediateReward = -0.0038 * ladrillosEnPantalla + 0.0005 * distanceClosestEnemy - 0.0004 * distanceClosestObject + 0.0075 * monedasEnPantalla - 0.0015 * distanceClosestCoin + 26.3071;
            } else {
                intermediateReward = -0.4447 * ladrillosEnPantalla + 0.0859 * distanceClosestEnemy - 0.0708 * distanceClosestObject + 0.0075 * monedasEnPantalla - 0.0007 * distanceClosestCoin + 15.0254;
            }
        }
        return intermediateReward;
    }
}