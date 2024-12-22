package Weapon;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lombok.Getter;

public class Bullet {
    private float x, y; // Координаты пули
    private float velocityX, velocityY; // Скорость пули
    @Getter
    private boolean active; // Состояние пули
    private final Texture texture;

    public Bullet(Texture texture) {
        this.texture = texture;
        this.active = false; // Пуля по умолчанию неактивна
    }

    public void activate(float startX, float startY, float targetX, float targetY, float speed) {
        this.x = startX;
        this.y = startY;

        // Вычисляем направление
        float dx = targetX - startX;
        float dy = targetY - startY;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        // Нормализуем направление
        this.velocityX = (dx / length) * speed;
        this.velocityY = (dy / length) * speed;

        this.active = true;
    }

    public void update(float delta) {
        if (active) {
            x += velocityX * delta;
            y += velocityY * delta;

            // Деактивируем пулю, если она выходит за пределы уровня
            if (x < 0 || y < 0 || x > 3000 || y > 2000) {
                active = false;
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, x, y);
        }
    }

}
