package Weapon;

import Classes.ConfigManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Weapon {
    private final String name;
    @Getter
    private final float fireRate;
    private final float damage;
    private final float range;
    private final float bulletSpeed;
    private final Sound sound;
    private final Texture bulletTexture;
    private final List<Bullet> bullets;
    private float timeSinceLastShot = 0;

    public Weapon(String name, float fireRate, float damage, float range, float bulletSpeed, float reloadTime, int ammo, Sound sound, Texture bulletTexture) {
        this.name = name;
        this.fireRate = fireRate;
        this.damage = damage;
        this.range = range;
        this.bulletSpeed = bulletSpeed;
        this.sound = sound;
        this.bulletTexture = bulletTexture;
        this.bullets = new ArrayList<>();

        // Создаем пул из 20 пуль (можно менять)
        for (int i = 0; i < ammo; i++) {
            bullets.add(new Bullet(bulletTexture));
        }
    }

    public void shoot(float startX, float startY, float targetX, float targetY) {
        if (timeSinceLastShot >= fireRate) {
            for (Bullet bullet : bullets) {
                if (!bullet.isActive()) {
                    bullet.activate(startX, startY, targetX, targetY, bulletSpeed);
                    if (sound != null) {
                        sound.setVolume(sound.play(),0.1f );
                    }
                    timeSinceLastShot = 0; // Сбрасываем таймер
                    break;
                }
            }
        }
    }

    public void update(float delta) {
        timeSinceLastShot += delta;
        for (Bullet bullet : bullets) {
            bullet.update(delta);
        }
    }

    public void render(SpriteBatch batch) {
        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }
    }
}
