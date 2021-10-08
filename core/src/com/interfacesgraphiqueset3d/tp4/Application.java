package com.interfacesgraphiqueset3d.tp4;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;


public class Application extends ApplicationAdapter {
    private Texture dropImage;
    private Texture bucketImage;
    private Sound dropSound;
    private Music rainMusic;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Rectangle bucket;
    private Array<Rectangle> raindrops;
    private long lastDropTime;

    
	Texture img;
 
    @Override
    public void create() {
       // Chargement de l'image drop et bucket
       dropImage = new Texture(Gdx.files.internal("drop.png"));
       bucketImage = new Texture(Gdx.files.internal("bucket.png"));
 
       // chargement l’effet sonore de chute et le fond de pluie musique
       dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
       rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
 
       // démarrage immédiat  de la lecture de la musique de fond
       rainMusic.setLooping(true);
       rainMusic.play();
 
       // création de la caméra et le SpriteBatch
       camera = new OrthographicCamera();
       camera.setToOrtho(false, 800, 480);
       batch = new SpriteBatch();
 
       // création d'un rectangle pour représenter logiquement le compartiment
       bucket = new Rectangle();
       bucket.x = 800 / 2 - 64 / 2; // mise au centre de compartiment horizontalement
       bucket.y = 20; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
       bucket.width = 64;
       bucket.height = 64;
 
       // création du réseau de gouttes de pluie et engendrement de la première goutte de pluie
       raindrops = new Array<Rectangle>();
       spawnRaindrop();
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800-64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
     }

     @Override
     public void render() {
        
        ScreenUtils.clear(0, 0, 0.2f, 1);
  
        // indique à l’appareil photo de mettre à jour ses matrices.
        camera.update();
  
        // indique au SpriteBatch de faire le rendu dans le système de coordonnées spécifié par la caméra.
        batch.setProjectionMatrix(camera.combined);
  
        // debut d'un nouveau lot et dessin du seau et toutes les gouttes
        batch.begin();
        batch.draw(bucketImage, bucket.x, bucket.y);
        for(Rectangle raindrop: raindrops) {
           batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        batch.end();
  
        // traitement de l’entrée de l’utilisateur
        if(Gdx.input.isTouched()) {
           Vector3 touchPos = new Vector3();
           touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
           camera.unproject(touchPos);
           bucket.x = touchPos.x - 64 / 2;
        }
        if(Gdx.input.isKeyPressed(Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();
  
        //  délimitation du compartiment parraport à l’écran
        if(bucket.x < 0) bucket.x = 0;
        if(bucket.x > 800 - 64) bucket.x = 800 - 64;
  
        // vérification de creation de une nouvelle goutte de pluie
        if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
  
      
        // lecture de l'effet sonore.
        for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
           Rectangle raindrop = iter.next();
           raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
           if(raindrop.y + 64 < 0) iter.remove();
           if(raindrop.overlaps(bucket)) {
              dropSound.play();
              iter.remove();
           }
        }
     }
     @Override
     public void dispose() {
        // disposition de toutes les ressources natives
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        batch.dispose();
     }
  


}
