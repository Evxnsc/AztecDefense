package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.jme3.util.SkyFactory;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.math.Vector2f;


public class GameWorld {

    private SimpleApplication app;
    private Node rootNode;
    private Node enemyNode;
    private Camino camino;
    private Torre torre;
    private OleadaManager oleadas;
    private Geometry objetivo;
    private Geometry botonReiniciar;

    private float vidaObjetivo = 100f;
    private boolean juegoTerminado = false;
    private int torresColocadas = 0;
    private int torresPermitidas = 1;
    
    private UIManager ui;
    private int monedas = 6;
    private int oleadaActual = 1;
    private List<Torre> torres = new ArrayList<>();
    
    private Spatial piramide;
    private Spatial modeloBaseTorre;



    public GameWorld(SimpleApplication app) {
        this.app = app;
        this.rootNode = app.getRootNode();
        this.enemyNode = new Node("Enemies");
        this.camino = new Camino();
    }

    public void init() {
        setupCamera();
        setupLighting();
        crearTerrenoOficial();
        colocarConjuntoArboles(new Vector3f(10, 4, -5)); 
        
        colocarConjuntoArboles(new Vector3f(-10, 4, -5));
        
        colocarChoza(new Vector3f(10, 4, 10), 2.5f);
        colocarChoza(new Vector3f(-10, 4, 10), 2.5f);
        colocarChoza(new Vector3f(-10, 4, 15), 2.5f);
        colocarChoza(new Vector3f(-17, 4, 10), 2.5f);
        colocarChoza(new Vector3f(-17, 4, 15), 2.5f);
        
        colocarChozaB(new Vector3f(-14, 4, 15), 2.5f);
        colocarChozaB(new Vector3f(4, 4, 10), 2.5f);
        colocarChozaB(new Vector3f(4, 4, 14), 2.5f);
        colocarChozaB(new Vector3f(-17, 4, 20), 2.5f);
        colocarChozaB(new Vector3f(-10, 4, 20), 2.5f);
        
        colocarJuego(new Vector3f(-3, 4, 15), 5f);
        

        ui = new UIManager();
        app.getStateManager().attach(ui);

        rootNode.attachChild(enemyNode);
        
        
        crearObjetivo(app.getAssetManager());
        
        Vector3f destinoPiramide = getPiramide().getLocalTranslation().clone(); 

        camino.getWaypoints().clear();
        camino.getWaypoints().add(new Vector3f(-18, 0, -18)); // esquina superior izquierda
        camino.getWaypoints().add(new Vector3f(-9, 0, -9));
        camino.getWaypoints().add(new Vector3f(0, 0, 0));
        camino.getWaypoints().add(new Vector3f(9, 0, 9));
        camino.getWaypoints().add(destinoPiramide); 
        
        oleadas = new OleadaManager(camino, rootNode, app.getAssetManager(), this);

        
        // Activar controlador de jugador
        ControladorJugador controlador = new ControladorJugador(this);
        app.getStateManager().attach(controlador);
        
        Spatial cielo = SkyFactory.createSky(app.getAssetManager(), "Textures/8k_stars.jpg", true);
        rootNode.attachChild(cielo);

        
        

        // Asignar tecla C para colocar torre
        app.getInputManager().addMapping("ColocarTorre", 
            new com.jme3.input.controls.KeyTrigger(com.jme3.input.KeyInput.KEY_C));

        app.getInputManager().addListener(new com.jme3.input.controls.ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (name.equals("ColocarTorre") && isPressed) {
                    controlador.activarModoColocacion();
                }
            }
        }, "ColocarTorre");

    }

    private void setupCamera() {
        // Desactiva completamente la cámara libre
        app.getFlyByCamera().setEnabled(false);

        // Posición inclinada (como en la imagen)
        Vector3f posicionCamara = new Vector3f(-15, 30, 35);  // Ajusta si quieres más altura o inclinación
        Vector3f puntoObjetivo = new Vector3f(0, 0, 0);      // Mira hacia el centro del terreno

        app.getCamera().setLocation(posicionCamara);
        app.getCamera().lookAt(puntoObjetivo, Vector3f.UNIT_Y);

        // Mostrar el cursor por si usas clicks
        app.getInputManager().setCursorVisible(true);
    }


    private void setupLighting() {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -2, -3).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
    }
    
    private void crearTerrenoOficial() {
        Box planoBase = new Box(20, 0.1f, 20);
        planoBase.scaleTextureCoordinates(new Vector2f(5, 5)); 

        Geometry suelo = new Geometry("Suelo", planoBase);

        // Material con textura
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", app.getAssetManager().loadTexture("Textures/suelo.jpg"));
        mat.getTextureParam("DiffuseMap").getTextureValue().setWrap(WrapMode.Repeat);

        suelo.setMaterial(mat);
        suelo.setLocalTranslation(0, -0.1f, 0);  
        rootNode.attachChild(suelo);

        // Visualizar el camino con bloques delgados
        for (Vector3f waypoint : camino.getWaypoints()) {
            Box paso = new Box(1, 0.05f, 1);  // Bajo para simular pavimento
            Geometry cubo = new Geometry("PasoCamino", paso);
            Material matPaso = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            matPaso.setColor("Color", ColorRGBA.Red);
            cubo.setMaterial(matPaso);
            cubo.setLocalTranslation(waypoint.x, 0, waypoint.z);
            rootNode.attachChild(cubo);
        }
        
        Box agua = new Box(95, 0.05f, 90);  // tamaño del océano
        Geometry oceano = new Geometry("Agua", agua);

        Material matAgua = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        matAgua.setTexture("DiffuseMap", app.getAssetManager().loadTexture("Textures/orillasA.png"));
        matAgua.getTextureParam("DiffuseMap").getTextureValue().setWrap(WrapMode.Repeat);

        oceano.setMaterial(matAgua);
        oceano.setLocalTranslation(7, -1f, -9);

        rootNode.attachChild(oceano);

        
    }



    private void crearObjetivo(AssetManager assetManager) {
        piramide = assetManager.loadModel("Models/Piramide/aztec_pyramid.j3o");

        piramide.setLocalScale(0.15f);

        piramide.setLocalTranslation(new Vector3f(15, 0, 15));

        
        rootNode.attachChild(piramide);
    }


    public void restarVidaObjetivo(float cantidad) {
        if (juegoTerminado) return;

        vidaObjetivo -= cantidad;
        //System.out.println("¡La pirámide recibió daño! Vida restante: " + vidaObjetivo);

        if (vidaObjetivo <= 0) {
            juegoTerminado = true;
            //System.out.println("¡GAME OVER! La pirámide fue destruida.");
            mostrarGameOver();
        }
    }
    
    public Spatial getPiramide() {
        return piramide;
    }
    
    public void colocarConjuntoArboles(Vector3f posicion) {
        Spatial vegetacion = app.getAssetManager().loadModel("Models/Vegetacion/arboles.j3o");
        vegetacion.setLocalTranslation(posicion);
        vegetacion.setLocalScale(0.2f); 
        rootNode.attachChild(vegetacion);
    }
    
    
    public void colocarChoza(Vector3f posicion, float escala) {
        Spatial choza = app.getAssetManager().loadModel("Models/Construcciones/ChozaA.j3o");
        choza.setLocalTranslation(posicion);
        choza.setLocalScale(escala);
        rootNode.attachChild(choza);
    }

    
    public void colocarChozaB(Vector3f posicion, float escala) {
        Spatial choza = app.getAssetManager().loadModel("Models/Construcciones/chozaB.j3o");
        choza.setLocalTranslation(posicion);
        choza.setLocalScale(escala);
        rootNode.attachChild(choza);
    }
    
    public void colocarJuego(Vector3f posicion, float escala) {
        Spatial choza = app.getAssetManager().loadModel("Models/Construcciones/juegopelota.j3o");
        choza.setLocalTranslation(posicion);
        choza.setLocalScale(escala);
        rootNode.attachChild(choza);
    }




    public void update(float tpf) {
        if (!juegoTerminado) {
            oleadas.update(tpf);
            for (Torre t : torres) {
                t.update(oleadas.getEnemigos(), tpf);
            }
        }
        ui.setValores((int) vidaObjetivo, monedas, oleadaActual);
    }

    
    public boolean registrarNuevaTorre(Torre torre) {
        if (torresColocadas < torresPermitidas) {
            torresColocadas++;
            torres.add(torre);
            return true;
        } else {
            return false;
        }
    }


    
    public void notificarOleadaTerminada() {
        oleadaActual++;

        if (oleadaActual % 2 == 0) {
            torresPermitidas++;
            //System.out.println("Nueva torre desbloqueada. Torres permitidas: " + torresPermitidas);
        }

        ui.setValores((int) vidaObjetivo, monedas, oleadaActual);
    }


    
    public void setOleadaActual(int oleada) {
        this.oleadaActual = oleada;
    }

    public void sumarMonedas(int cantidad) {
        this.monedas += cantidad;
    }


    private void mostrarGameOver() {
        BitmapText texto = new BitmapText(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"), false);
        texto.setText("¡GAME OVER!");
        texto.setSize(40);
        texto.setColor(ColorRGBA.Cyan);
        texto.setLocalTranslation(
            app.getCamera().getWidth() / 2f - 100,
            app.getCamera().getHeight() - 50,
            0
        );
        app.getGuiNode().attachChild(texto);
    }



    private final ActionListener reiniciarListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("ReiniciarClick") && isPressed && juegoTerminado) {
                
                app.restart();
            }
        }
    };

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }
    
    public List<Torre> getTorres() {
        return torres;
    }
    
    public int getMonedas() {
        return monedas;
    }

    public void restarMonedas(int cantidad) {
        monedas -= cantidad;
        ui.setValores((int) vidaObjetivo, monedas, oleadaActual); // actualiza la UI
    }
   




    private void spawnTestEnemy() {
        Enemigo e = new Enemigo(app.getAssetManager(), camino);
        enemyNode.attachChild(e.getSpatial());
    }
}
