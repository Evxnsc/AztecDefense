package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.math.Vector3f;
import java.util.List;
import mygame.Enemigo;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.UUID;


public class Torre {
    private Vector3f posicion;
    private float rango;
    private float dano;
    private float cooldownMaximo;
    private float cooldownActual;
    private int vida = 50;
    private int nivelMejora = 0;
    private String nombre;
    private Spatial modelo;
    
    public Torre(Vector3f posicion, float rango, float dano, float cooldown, String nombre) {
        this.posicion = posicion.clone(); 
        this.rango = rango;
        this.dano = dano;
        this.cooldownMaximo = cooldown;
        this.cooldownActual = 0f;
        this.nombre = nombre;
    }
    
    /**
     * Actualiza la torre en cada frame.
     * Cada instancia de torre funciona independientemente.
     * 
     * @param enemigos Lista de enemigos vivos en el juego
     * @param tpf Time per frame (tiempo transcurrido desde el último frame)
     */
    public void update(List<Enemigo> enemigos, float tpf) {
   
        mirarAlEnemigo(enemigos);

 
        if (cooldownActual > 0) {
            cooldownActual -= tpf;
            if (cooldownActual < 0) {
                cooldownActual = 0;
            }
        }

        if (cooldownActual <= 0) {
            Enemigo objetivo = encontrarEnemigoMasCercano(enemigos);

            if (objetivo != null) {
                atacar(objetivo);
                cooldownActual = cooldownMaximo; 
            }
        }
    }

    
    /**
     * Encuentra el enemigo más cercano dentro del rango de la torre.
     * 
     * @param enemigos Lista de enemigos disponibles
     * @return El enemigo más cercano en rango, o null si no hay ninguno
     */
    private Enemigo encontrarEnemigoMasCercano(List<Enemigo> enemigos) {
        Enemigo enemigoCercano = null;
        float distanciaMasCercana = Float.MAX_VALUE;
        
        for (Enemigo enemigo : enemigos) {
            // Verificar si el enemigo está vivo
            if (enemigo.getHealth() <= 0) {
                continue;
            }
            
            // Calcular distancia entre la torre y el enemigo
            float distancia = posicion.distance(enemigo.getPosicion());
            
            // Verificar si está en rango y es más cercano que el anterior
            if (distancia <= rango && distancia < distanciaMasCercana) {
                enemigoCercano = enemigo;
                distanciaMasCercana = distancia;
            }
        }
        
        return enemigoCercano;
    }
    
    /**
     * Ejecuta el ataque contra un enemigo.
     * 
     * @param enemigo El enemigo objetivo
     */
    private void atacar(Enemigo enemigo) {
        //System.out.println("Torre ataca a enemigo en: " + enemigo.getSpatial().getLocalTranslation());
        enemigo.recibirDanio(dano);
    }

    
    /**
     * Verifica si la torre puede atacar (cooldown terminado).
     * 
     * @return true si puede atacar, false si está en cooldown
     */
    public boolean puedeAtacar() {
        return cooldownActual <= 0;
    }
    
    /**
     * Obtiene el tiempo restante de cooldown.
     * 
     * @return Tiempo restante en segundos
     */
    public float getCooldownRestante() {
        return Math.max(0, cooldownActual);
    }
    
    // Getters y setters
    public Vector3f getPosicion() {
        return posicion.clone();
    }
    
    public float getRango() {
        return rango;
    }
    
    public void setRango(float rango) {
        this.rango = rango;
    }
    
    public float getDano() {
        return dano;
    }
    
    public void setDano(float dano) {
        this.dano = dano;
    }
    
    public boolean aplicarMejora(int nivel) {
        if (nivel == 1 && nivelMejora == 0) {
            this.dano += 5f;
            this.rango += 1f;
            nivelMejora = 1;
            System.out.println("Mejora 1: +20 daño, +10 rango");
            return true;
        } else if (nivel == 2 && nivelMejora == 1) {
            this.dano += 2f;
            this.rango += 1f;
            nivelMejora = 2;
            System.out.println("Mejora 2: +20 daño, +10 rango");
            return true;
        }

        System.out.println("Mejora inválida o ya aplicada. Nivel actual: " + nivelMejora);
        return false;
    }

    
    public float getCooldownMaximo() {
        return cooldownMaximo;
    }
    
    public void setCooldownMaximo(float cooldownMaximo) {
        this.cooldownMaximo = cooldownMaximo;
    }
    
    public Spatial getSpatial(AssetManager assetManager) {
        
        Spatial modelo = assetManager.loadModel("Models/Torre/torre_azteca.glb");

        modelo.setLocalScale(1.5f);

        Vector3f posicionElevada = posicion.clone();
        posicionElevada.setY(1.2f);
        modelo.setLocalTranslation(posicionElevada);

        String nombreUnico = "Torre_" + UUID.randomUUID();
        modelo.setName(nombreUnico);
        this.nombre = nombreUnico; 

     
        this.modelo = modelo;

        AnimComposer anim = findAnimComposer(modelo);
        if (anim != null) {
            System.out.println("Animaciones encontradas en torre_azteca:");
            for (String nombre : anim.getAnimClipsNames()) {
                System.out.println("- " + nombre);
            }

            anim.setCurrentAction("Armature_acción");
        } else {
            System.out.println("No se encontró AnimComposer en la torre.");
        }

        return modelo;
    }


    
    private AnimComposer findAnimComposer(Spatial spatial) {
        if (spatial == null) return null;

        AnimComposer composer = spatial.getControl(AnimComposer.class);
        if (composer != null) return composer;

        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                composer = findAnimComposer(child);
                if (composer != null) return composer;
            }
        }
        return null;
    }
    
    public void mirarAlEnemigo(List<Enemigo> enemigos) {
        if (modelo == null || enemigos == null || enemigos.isEmpty()) return;

        Enemigo objetivo = null;
        float distanciaMinima = Float.MAX_VALUE;

        for (Enemigo enemigo : enemigos) {
            float distancia = enemigo.getSpatial().getWorldTranslation().distance(modelo.getWorldTranslation());
            if (distancia < distanciaMinima && distancia <= rango) {
                distanciaMinima = distancia;
                objetivo = enemigo;
            }
        }

        if (objetivo != null) {
            Vector3f direccion = objetivo.getSpatial().getWorldTranslation().subtract(modelo.getWorldTranslation());
            direccion.y = 0; // ignorar inclinación vertical
            modelo.lookAt(modelo.getWorldTranslation().add(direccion), Vector3f.UNIT_Y);
        }
    }





    
    public String getNombre() {
        return nombre;
    }
    

    public void setSpatial(Spatial spatial) {
        this.modelo = spatial;
    }


    public Spatial getSpatial() {
        return modelo;
    }



}