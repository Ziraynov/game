package Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Entity {
    protected float x, y; // Координаты сущности
    protected float width, height; // Размеры сущности
    protected float maxHP; // Максимальное здоровье
    protected float currentHP; // Текущее здоровье

    public Entity(float x, float y, float width, float height, float maxHP) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.maxHP = maxHP;
        this.currentHP = maxHP;
    }

    // Метод для получения урона
    public void takeDamage(float damage) {
        currentHP -= damage;
        if (currentHP < 0) currentHP = 0;
    }

    // Метод для проверки, жива ли сущность
    public boolean isAlive() {
        return currentHP > 0;
    }

    // Метод для отрисовки HP-бара
    public void renderHPBar(ShapeRenderer shapeRenderer) {
        float hpBarWidth = width; // Ширина HP-бара равна ширине сущности
        float hpBarHeight = 5; // Высота HP-бара

        // Задний фон HP-бара (чёрный)
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x, y + height + 5, hpBarWidth, hpBarHeight);

        // Заполненный HP-бар (зелёный, пропорционален текущему HP)
        shapeRenderer.setColor(Color.RED);
        float hpRatio = currentHP / maxHP;
        shapeRenderer.rect(x, y + height + 5, hpBarWidth * hpRatio, hpBarHeight);
    }

    // Абстрактный метод для обновления логики (будет реализован в наследниках)
    public abstract void update(float deltaTime);
}
