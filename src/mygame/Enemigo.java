package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.scene.Node;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;





public class Enemigo {

    private Spatial model; 
    private Camino camino;
    private float vida = 100f;
    private AnimComposer animComposer;






    public Enemigo(AssetManager assetManager, Camino camino) {
        this.camino = camino;

       
        model = assetManager.loadModel("Models/Enemigo/zombie-run.j3o");
        model.setLocalScale(1f);
        model.setLocalTranslation(camino.getWaypoints().get(0));

        // Buscar AnimComposer aunque esté en subnodos
        AnimComposer animComposer = findAnimComposer(model);
        if (animComposer != null) {
            System.out.println("✅ Animaciones encontradas:");
            for (String name : animComposer.getAnimClipsNames()) {
                System.out.println("- " + name);
            }

            
            animComposer.setCurrentAction("Armature");
        } else {
            System.out.println("AnimComposer no encontrado en ningún subnodo.");
        }

        // Datos del enemigo
        model.setUserData("health", 100f);
        model.setUserData("pathIndex", 1);
    }



    public void update(float tpf) {
        mover(tpf);

        // Rotar el modelo para que mire hacia el siguiente waypoint
        Vector3f posicionActual = model.getLocalTranslation();
        Integer pathIndex = model.getUserData("pathIndex");

        if (pathIndex != null && pathIndex < camino.getWaypoints().size()) {
            Vector3f destino = camino.getWaypoints().get(pathIndex);
            Vector3f direccion = destino.subtract(posicionActual).setY(0).normalizeLocal();

            if (direccion.length() > 0.01f) {
                float angulo = FastMath.atan2(direccion.x, direccion.z);
                model.setLocalRotation(new Quaternion().fromAngleAxis(angulo, Vector3f.UNIT_Y));
            }
        }
    }




    public void mover(float tpf) {
        int index = model.getUserData("pathIndex");

        if (index >= camino.getWaypoints().size()) {
            return; // Ya llegó al final
        }

        Vector3f posActual = model.getLocalTranslation();
        Vector3f posObjetivo = camino.getWaypoints().get(index);

        // Ajuste: igualar la altura Y del objetivo al enemigo actual
        posObjetivo = new Vector3f(posObjetivo.x, posActual.y, posObjetivo.z);

        Vector3f direccion = posObjetivo.subtract(posActual).normalizeLocal();
        float velocidad = 2f;

        model.move(direccion.mult(tpf * velocidad));

        
        Vector3f posCorr = model.getLocalTranslation();
        model.setLocalTranslation(posCorr.x, posActual.y, posCorr.z);

        
        if (posCorr.distance(posObjetivo) < 0.5f) {
            model.setUserData("pathIndex", index + 1);
        }
    }



    public void recibirDanio(float cantidad) {
        vida -= cantidad;
        //System.out.println("Enemigo recibió daño. Vida actual: " + vida);
    }

    public void setVida(float nuevaVida) {
        model.setUserData("health", nuevaVida);
    }

    private AnimComposer findAnimComposer(Spatial spatial) {
        if (spatial == null) return null;

        if (spatial.getControl(AnimComposer.class) != null) {
            return spatial.getControl(AnimComposer.class);
        }

        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                AnimComposer result = findAnimComposer(child);
                if (result != null) return result;
            }
        }

        return null;
    }

   
    
    public boolean estaMuerto() {
        return vida <= 0;
    }



    public Spatial getSpatial() {
        return model;
    }


    public Vector3f getPosicion() {
        return model.getLocalTranslation();
    }
    
    public float getHealth() {
        return vida;
    }

}
