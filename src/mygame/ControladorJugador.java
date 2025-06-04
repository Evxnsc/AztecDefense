package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.renderer.Camera;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import java.util.UUID;


public class ControladorJugador extends BaseAppState {



    private InputManager inputManager;
    private Camera cam;
    private Node rootNode;
    private GameWorld gameWorld;
    private boolean modoColocacion = false;
    private Torre torreSeleccionada = null;
    private SimpleApplication app;
    private boolean modoMejora = false;





    public ControladorJugador(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;
        inputManager = app.getInputManager();
        cam = app.getCamera();
        rootNode = ((SimpleApplication) app).getRootNode();


        inputManager.addMapping("ClickIzquierdo", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("ModoMejora", new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping("Mejorar1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("Mejorar2", new KeyTrigger(KeyInput.KEY_2));
        
        inputManager.addListener(actionListener, "ClickIzquierdo", "ModoMejora", "Mejorar1", "Mejorar2");
    }


    public void activarModoColocacion() {
        modoColocacion = true;
        //System.out.println("Modo colocación activado. Haz clic en el terreno para colocar la torre.");
    }

    private final ActionListener actionListener = new ActionListener() {
        
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isEnabled() || !isPressed) return;

            if (name.equals("ModoMejora")) {
                modoMejora = !modoMejora;
                System.out.println("Modo mejora: " + (modoMejora ? "ACTIVADO" : "DESACTIVADO"));
                return;
            }

            if (name.equals("ClickIzquierdo")) {
                Vector3f click3d = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0f);
                Vector3f dir = cam.getWorldCoordinates(inputManager.getCursorPosition(), 1f)
                                   .subtractLocal(click3d).normalizeLocal();
                
                
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(click3d, dir);
                if (modoColocacion) {
                    Spatial sueloNode = rootNode.getChild("Suelo");
                    if (sueloNode != null) {
                        sueloNode.collideWith(ray, results);
                    }
                } else {
                    rootNode.collideWith(ray, results);
                }
                
                

                if (results.size() > 0) {
                    Spatial seleccionado = null;

                    // Buscar la primera colisión que no sea con el cielo
                    for (CollisionResult result : results) {
                        if (!result.getGeometry().getName().equals("Sky")) {
                            seleccionado = result.getGeometry();
                            break;
                        }
                    }

                    if (seleccionado == null) {
                        System.out.println("❌ Solo se hizo clic en el cielo. Ignorado.");
                        return;
                    }

                    System.out.println("➡️ Geometría clickeada: " + seleccionado.getName());

                    // Subir de forma segura hasta encontrar el nodo "Torre_xxx"
                    Spatial torreSeleccionadaNodo = seleccionado;
                    while (torreSeleccionadaNodo != null) {
                        String nombre = torreSeleccionadaNodo.getName();
                        System.out.println("🔍 Explorando nodo: " + nombre);
                        if (nombre != null && nombre.startsWith("Torre")) {
                            break;
                        }
                        torreSeleccionadaNodo = torreSeleccionadaNodo.getParent();
                    }

                    if (torreSeleccionadaNodo != null) {
                        System.out.println("Nodo Torre detectado: " + torreSeleccionadaNodo.getName());
                    } else {
                        System.out.println("No se encontró nodo padre que sea una Torre.");
                    }

                    if (modoColocacion) {
                        Vector3f punto = results.getClosestCollision().getContactPoint();
                        colocarTorre(punto);
                        modoColocacion = false;
                    } else if (modoMejora) {
                        if (torreSeleccionadaNodo != null && torreSeleccionadaNodo.getName().startsWith("Torre")) {
                            boolean encontrada = false;
                            for (Torre t : gameWorld.getTorres()) {
                                System.out.println("Comparando con torre: " + t.getNombre());
                                if (t.getNombre().equals(torreSeleccionadaNodo.getName())) {
                                    torreSeleccionada = t;
                                    encontrada = true;
                                    System.out.println("Torre seleccionada correctamente.");
                                    break;
                                }
                            }
                            if (!encontrada) {
                                System.out.println("Nodo coincide con una torre, pero no está en la lista de gameWorld.");
                            }
                        } else {
                            System.out.println("Nodo clickeado no es una torre válida.");
                        }
                    }
                }

                
                
    
            }

            if (name.equals("Mejorar1")) {
                System.out.println("Tecla presionada: " + name);
                if (torreSeleccionada == null) {
                    System.out.println("⚠️ No hay torre seleccionada.");
                } else {
                    System.out.println("Intentando aplicar mejora 1...");
                    if (gameWorld.getMonedas() >= 5 && torreSeleccionada.aplicarMejora(1)) {
                        gameWorld.restarMonedas(5);
                        System.out.println("✅ Mejora 1 aplicada.");
                    } else {
                        System.out.println("❌ No se puede aplicar mejora 1.");
                    }
                }
            }

            if (name.equals("Mejorar2")) {
                System.out.println("Tecla presionada: " + name);
                if (torreSeleccionada == null) {
                    System.out.println("⚠️ No hay torre seleccionada.");
                } else {
                    System.out.println("Intentando aplicar mejora 2...");
                    if (gameWorld.getMonedas() >= 7 && torreSeleccionada.aplicarMejora(2)) {
                        gameWorld.restarMonedas(7);
                        System.out.println("✅ Mejora 2 aplicada.");
                    } else {
                        System.out.println("❌ No se puede aplicar mejora 2.");
                    }
                }
            }

        }
    };

    private void colocarTorre(Vector3f posicion) {
        if (gameWorld.getMonedas() < 6) return;

        String nombreUnico = "Torre_" + UUID.randomUUID();
        Torre nuevaTorre = new Torre(posicion, 5f, 50f, 1f, nombreUnico);

        if (gameWorld.registrarNuevaTorre(nuevaTorre)) {
            Spatial modelo = nuevaTorre.getSpatial(getApplication().getAssetManager());
            rootNode.attachChild(modelo);
            nuevaTorre.setSpatial(modelo);

            gameWorld.restarMonedas(6);
        }
    }




    



    @Override protected void cleanup(Application app) {}
    @Override protected void onEnable() {}
    @Override protected void onDisable() {}

    


}
