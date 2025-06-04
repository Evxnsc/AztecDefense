package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;

public class Main extends SimpleApplication {

    private GameWorld gameWorld;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        gameWorld = new GameWorld(this);
        gameWorld.init();
    }

    @Override
    public void simpleUpdate(float tpf) {
    gameWorld.update(tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {

    }
}
