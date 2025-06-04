// UIManager.java
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.app.SimpleApplication;

public class UIManager extends BaseAppState {

    private BitmapText textoVida;
    private BitmapText textoMonedas;
    private BitmapText textoOleada;

    private int vida;
    private int monedas;
    private int oleada;

    private Node guiNode;
    private SimpleApplication app;

    @Override
    protected void initialize(Application application) {
        this.app = (SimpleApplication) application;
        this.guiNode = app.getGuiNode();

        textoVida = crearTexto(10, app.getCamera().getHeight() - 20);
        textoMonedas = crearTexto(10, app.getCamera().getHeight() - 50);
        textoOleada = crearTexto(10, app.getCamera().getHeight() - 80);

        guiNode.attachChild(textoVida);
        guiNode.attachChild(textoMonedas);
        guiNode.attachChild(textoOleada);
    }

    private BitmapText crearTexto(float x, float y) {
        BitmapText txt = new BitmapText(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"));
        txt.setSize(20);
        txt.setLocalTranslation(x, y, 0);
        return txt;
    }

    public void setValores(int vida, int monedas, int oleada) {
        this.vida = vida;
        this.monedas = monedas;
        this.oleada = oleada;

        textoVida.setText("Vida de la pirámide: " + vida);
        textoMonedas.setText("Monedas: " + monedas);
        textoOleada.setText("Oleada: " + oleada);
    }


    @Override
    public void update(float tpf) {
        textoVida.setText("Vida de la pirámide: " + vida);
        textoMonedas.setText("Monedas: " + monedas);
        textoOleada.setText("Oleada: " + oleada);
    }

    @Override protected void cleanup(Application app) {}
    @Override protected void onEnable() {}
    @Override protected void onDisable() {}
}
