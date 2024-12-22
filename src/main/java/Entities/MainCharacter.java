package Entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import Weapon.Weapon;
public class MainCharacter extends Entity {
    private Weapon weapon;

    public MainCharacter(float x, float y, float width, float height, float maxHP, Weapon weapon) {
        super(x, y, width, height, maxHP);
        this.weapon = weapon;
    }

    @Override
    public void update(float deltaTime) {
        // Обновление ввода игрока
        // Например, обработка движения и стрельбы
    }

    // Отрисовка персонажа
    public void render(Batch batch) {
        // Отрисовка спрайта игрока
        // batch.draw(...);
    }
}
