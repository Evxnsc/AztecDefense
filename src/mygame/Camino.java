package mygame;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.List;

public class Camino {

    private List<Vector3f> waypoints;

    public Camino() {
        waypoints = new ArrayList<>();
    }

    public void initPath() {
        waypoints.clear();
        waypoints.add(new Vector3f(-18, 0, -18)); // esquina superior izquierda
        waypoints.add(new Vector3f(-9, 0, -9));
        waypoints.add(new Vector3f(0, 0, 0));
        waypoints.add(new Vector3f(9, 0, 9));
        waypoints.add(new Vector3f(15, 0, 15)); // esquina inferior derecha
    }

    public void followPath(Node enemyNode, float tpf) {
        for (Spatial child : enemyNode.getChildren()) {
            Integer index = child.getUserData("pathIndex");
            if (index == null) {
                index = 1;
                child.setUserData("pathIndex", index);
            }
            // Si llegó al final, remover
            if (index >= waypoints.size()) {
                child.removeFromParent();
                continue;
            }
            Vector3f target = waypoints.get(index);
            Vector3f current = child.getLocalTranslation();
            Vector3f direction = target.subtract(current).normalizeLocal();
            float speed = 3f; // unidades por segundo
            Vector3f movement = direction.mult(speed * tpf);
            if (current.distance(target) > 0.1f) {
                child.setLocalTranslation(current.add(movement));
            } else {
                child.setUserData("pathIndex", index + 1);
            }
        }
    }

    public List<Vector3f> getWaypoints() {
        return waypoints;
    }
}