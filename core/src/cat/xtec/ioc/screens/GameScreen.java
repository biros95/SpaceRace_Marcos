package cat.xtec.ioc.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

import cat.xtec.ioc.SpaceRace;
import cat.xtec.ioc.helpers.AssetManager;
import cat.xtec.ioc.helpers.InputHandler;
import cat.xtec.ioc.objects.Asteroid;
import cat.xtec.ioc.objects.Background;
import cat.xtec.ioc.objects.Bullet;
import cat.xtec.ioc.objects.ScrollHandler;
import cat.xtec.ioc.objects.Spacecraft;
import cat.xtec.ioc.utils.Settings;

public class GameScreen implements Screen {
    static Bullet bullet = null;
    // Per controlar el gameover
    Boolean gameOver = false;
    private int puntuacion;
    private double multiplicador;
    // Objectes necessaris
    private static Stage stage;
    private Spacecraft spacecraft;
   // private Background bg;
    private static ScrollHandler scrollHandler;

    // Encarregats de dibuixar elements per pantalla
    private ShapeRenderer shapeRenderer;
    private Batch batch;

    // Per controlar l'animació de l'explosió
    private float explosionTime = 0;
    private float explosionTimeBullet = 0;
    private SpaceRace game;

    // Preparem el textLayout per escriure text
    private GlyphLayout textPuntuacion;
    private GlyphLayout textLayout;

    public GameScreen(Batch prevBatch, Viewport prevViewport, int dificultad, SpaceRace game) {
        this.game = game;

        dificultad(dificultad);

        // Iniciem la música
      //  AssetManager.music.play();

        // Creem el ShapeRenderer
        shapeRenderer = new ShapeRenderer();

        // Creem la càmera de les dimensions del joc
        OrthographicCamera camera = new OrthographicCamera(Settings.GAME_WIDTH, Settings.GAME_HEIGHT);
        // Posant el paràmetre a true configurem la càmera per a
        // que faci servir el sistema de coordenades Y-Down
        camera.setToOrtho(true);

        // Creem el viewport amb les mateixes dimensions que la càmera
        StretchViewport viewport = new StretchViewport(Settings.GAME_WIDTH, Settings.GAME_HEIGHT, camera);

        // Creem l'stage i assginem el viewport
        stage = new Stage(viewport);

        batch = stage.getBatch();

        // Creem la nau i la resta d'objectes
        spacecraft = new Spacecraft(Settings.SPACECRAFT_STARTX, Settings.SPACECRAFT_STARTY, Settings.SPACECRAFT_WIDTH, Settings.SPACECRAFT_HEIGHT);
       // bg = new Background(0, 0, Settings.GAME_WIDTH * 2, Settings.GAME_HEIGHT, 0);
        scrollHandler = new ScrollHandler();

        // Afegim els actors a l'stage
        stage.addActor(scrollHandler);

       // stage.addActor(bg);
        stage.addActor(spacecraft);

        // Donem nom a l'Actor
        spacecraft.setName("spacecraft");

        textPuntuacion = new GlyphLayout();
        puntuacion = 0;

       textLayout = new GlyphLayout();

        // Assignem com a gestor d'entrada la classe InputHandler
        Gdx.input.setInputProcessor(new InputHandler(this));

    }
    public static void shoot() {
        if (bullet == null) {
            for (Actor actor : stage.getActors()) {
                if (actor.getName() != null && actor.getName().equalsIgnoreCase("spacecraft")) {
                    bullet = new Bullet(actor.getX() + actor.getWidth(), actor.getY() + actor.getHeight() / 2, scrollHandler);
                    stage.addActor(bullet);
                    break;
                }
            }
        }
    }
    public void puntuacion (){
        batch.begin();
        puntuacion+=10*multiplicador;
        textPuntuacion.setText(AssetManager.font, "Pts: "+puntuacion );
        AssetManager.font.draw(batch, textPuntuacion, Settings.GAME_WIDTH-100, 5);
        batch.end();
    }
    public void gameOver(){
        batch.begin();
        textLayout.setText(AssetManager.font, "FINAL: " + Integer.toString(puntuacion));
        gameOver = true;
        AssetManager.font.draw(batch, textLayout, (Settings.GAME_WIDTH - textLayout.width) / 2, (Settings.GAME_HEIGHT - textLayout.height) / 2);
        batch.end();
    }
    public void reiniciar(){
        if (Gdx.input.isTouched()) {
            game.setScreen(new MenuScreen(this.game));
            dispose();
        }
    }





    private void dificultad(int dificultad) {


        switch (dificultad) {
            case 1:
                Settings.ASTEROID_GAP += 10;
                Settings.SPACECRAFT_VELOCITY += 30;
                Settings.ASTEROID_SPEED += 20;
                multiplicador = 1;
                break;
            case 2:
                Settings.ASTEROID_SPEED -= 20;
                Settings.SPACECRAFT_VELOCITY += 10;
                Settings.ASTEROID_GAP -= 20;
                multiplicador = 1.5;

            case 3:
                Settings.ASTEROID_GAP -= 50;
                Settings.ASTEROID_SPEED -= 40;
                Settings.SPACECRAFT_VELOCITY -= 10;
                multiplicador = 2;
        }
    }
    private void drawElements() {

        // Recollim les propietats del Batch de l'Stage
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        // Pintem el fons de negre per evitar el "flickering"
        //Gdx.gl20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Inicialitzem el shaperenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Definim el color (verd)
        shapeRenderer.setColor(new Color(0, 1, 0, 1));

        // Pintem la nau
        shapeRenderer.rect(spacecraft.getX(), spacecraft.getY(), spacecraft.getWidth(), spacecraft.getHeight());

        // Recollim tots els Asteroid
        ArrayList<Asteroid> asteroids = scrollHandler.getAsteroids();
        Asteroid asteroid;

        for (int i = 0; i < asteroids.size(); i++) {

            asteroid = asteroids.get(i);
            switch (i) {
                case 0:
                    shapeRenderer.setColor(1, 0, 0, 1);
                    break;
                case 1:
                    shapeRenderer.setColor(0, 0, 1, 1);
                    break;
                case 2:
                    shapeRenderer.setColor(1, 1, 0, 1);
                    break;
                default:
                    shapeRenderer.setColor(1, 1, 1, 1);
                    break;
            }
            shapeRenderer.circle(asteroid.getX() + asteroid.getWidth() / 2, asteroid.getY() + asteroid.getWidth() / 2, asteroid.getWidth() / 2);
        }
        shapeRenderer.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        // Dibuixem i actualizem tots els actors de l'stage
        stage.draw();
        stage.act(delta);



        if (!gameOver) {
            if (scrollHandler.collides(spacecraft)) {
               // Si hi ha hagut col·lisió: Reproduïm l'explosió
                AssetManager.explosionSound.play();
                stage.getRoot().findActor("spacecraft").remove();
                gameOver = true;
            }
            if (bullet != null && scrollHandler.collidesBullet(bullet)) {
                // Si hi ha hagut col·lisió: Reproduïm l'explosió
                AssetManager.explosionSound.play();


//                stage.getRoot().findActor("bullet").remove();
           //     gameOver = true;

                batch.begin();
                // Si hi ha hagut col·lisió: Reproduïm l'explosió
                batch.draw(AssetManager.explosionAnim.getKeyFrame(explosionTimeBullet, false), (bullet.getX() + bullet.getWidth() / 2) - 32, bullet.getY() + bullet.getHeight() / 2 - 32, 64, 64);
                // AssetManager.font.draw(batch, textLayout, Settings.GAME_WIDTH/2 - textLayout.width/2, Settings.GAME_HEIGHT/2 - textLayout.height/2);
                batch.end();

                explosionTimeBullet += delta;
                bullet = null;
                puntuacion +=100;
            }
            puntuacion();
        } else {
            batch.begin();
            // Si hi ha hagut col·lisió: Reproduïm l'explosió
            batch.draw(AssetManager.explosionAnim.getKeyFrame(explosionTime, false), (spacecraft.getX() + spacecraft.getWidth() / 2) - 32, spacecraft.getY() + spacecraft.getHeight() / 2 - 32, 64, 64);
            // AssetManager.font.draw(batch, textLayout, Settings.GAME_WIDTH/2 - textLayout.width/2, Settings.GAME_HEIGHT/2 - textLayout.height/2);
            batch.end();

            explosionTime += delta;
            gameOver();
            reiniciar();
        }


           // drawElements();


        }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public Spacecraft getSpacecraft() {
        return spacecraft;
    }

    public Stage getStage() {
        return stage;
    }

 /*   public ScrollHandler getScrollHandler() {
        return scrollHandler;
    }
    */
}