package mygame;

import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class CaminoFactory {

    public static Camino crearCaminoIzquierdo() {
        Camino camino = new Camino();
        camino.getWaypoints().add(new Vector3f(-30, 0, -20));
        camino.getWaypoints().add(new Vector3f(-15, 0, -10));
        camino.getWaypoints().add(new Vector3f(0, 0, 0));
        camino.getWaypoints().add(new Vector3f(9, 0, 9));
        camino.getWaypoints().add(new Vector3f(15, 0, 15));
        return camino;
    }

    public static Camino crearCaminoCentral() {
        Camino camino = new Camino();
        camino.getWaypoints().add(new Vector3f(0, 0, -20));
        camino.getWaypoints().add(new Vector3f(0, 0, -10));
        camino.getWaypoints().add(new Vector3f(9, 0, 9));
        camino.getWaypoints().add(new Vector3f(15, 0, 15));
        return camino;
    }

    public static Camino crearCaminoDerecho() {
        Camino camino = new Camino();
        camino.getWaypoints().add(new Vector3f(30, 0, -20));
        camino.getWaypoints().add(new Vector3f(15, 0, -10));
        camino.getWaypoints().add(new Vector3f(9, 0, 9));
        camino.getWaypoints().add(new Vector3f(15, 0, 15));
        return camino;
    }
}
