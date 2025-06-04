package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OleadaManager {

    private List<Enemigo> enemigos = new ArrayList<>();
    private Camino camino;
    private Node rootNode;
    private AssetManager assetManager;
    private float tiempoEntreOleadas = 5f;
    private float tiempoRestante = 0f;
    private int cantidadPorOleada = 3;
    private GameWorld gameWorld;
    private int oleadaActual = 0;
    private boolean esperandoSiguienteOleada = true;
    private List<Vector3f> puntosDeSpawn = new ArrayList<>();
    private List<Camino> caminos = new ArrayList<>();



    public OleadaManager(Camino camino, Node rootNode, AssetManager assetManager, GameWorld gameWorld) {
        this.camino = camino;
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.gameWorld = gameWorld;
        puntosDeSpawn.add(camino.getWaypoints().get(0).add(-10, 1f, 0)); // Spawn izquierdo lejano
        puntosDeSpawn.add(camino.getWaypoints().get(0).add(28, 1f, 0));   // Spawn central
        puntosDeSpawn.add(camino.getWaypoints().get(0).add(45, 1f, 0));  // Spawn derecho lejano
        
        this.caminos.add(CaminoFactory.crearCaminoIzquierdo());
        this.caminos.add(CaminoFactory.crearCaminoCentral());
        this.caminos.add(CaminoFactory.crearCaminoDerecho());



    }

    public void update(float tpf) {
        
        Iterator<Enemigo> iter = enemigos.iterator();
        while (iter.hasNext()) {
            Enemigo enemigo = iter.next();

            if (enemigo.estaMuerto()) {
                gameWorld.sumarMonedas(1); //Aquí se da la recompensa
                rootNode.detachChild(enemigo.getSpatial());
                iter.remove();
                continue;
            }


            enemigo.update(tpf);

            Vector3f posEnemigo = enemigo.getSpatial().getLocalTranslation().clone();
            Vector3f posPiramide = gameWorld.getPiramide().getLocalTranslation().clone();

            posEnemigo.y = 0;
            posPiramide.y = 0;

            float distancia = posEnemigo.distance(posPiramide);

            if (distancia < 2f) {
                gameWorld.restarVidaObjetivo(10f); // aplica daño
                rootNode.detachChild(enemigo.getSpatial()); 
                iter.remove(); 
            }


        }

        if (enemigos.isEmpty()) {
            if (!esperandoSiguienteOleada) {
                esperandoSiguienteOleada = true;
                tiempoRestante = tiempoEntreOleadas;
                gameWorld.notificarOleadaTerminada();
            } else {
                tiempoRestante -= tpf;
                if (tiempoRestante <= 0f) {
                    oleadaActual++;
                    lanzarOleada();
                    esperandoSiguienteOleada = false;
                    gameWorld.setOleadaActual(oleadaActual);
                 
                    cantidadPorOleada += 2;
                }
            }
        }
    }


    private void lanzarOleada() {
        int vidaPorEnemigo = 50 + (oleadaActual - 1) * 15;

        for (int i = 0; i < cantidadPorOleada; i++) {
            int indiceCamino = i % caminos.size();
            Camino caminoElegido = caminos.get(indiceCamino);
            Vector3f inicio = caminoElegido.getWaypoints().get(0).clone();
            inicio = inicio.add(0, 1, -i * 3f); 

            Enemigo enemigo = new Enemigo(assetManager, caminoElegido);
            enemigo.setVida(vidaPorEnemigo);
            enemigo.getSpatial().setLocalTranslation(inicio);

            enemigos.add(enemigo);
            rootNode.attachChild(enemigo.getSpatial());
        }
    }





    public List<Enemigo> getEnemigos() {
        return enemigos;
    }
}
