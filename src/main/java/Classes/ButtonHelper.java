package Classes;

import com.badlogic.gdx.Input;

public class ButtonHelper {
    // Метод для преобразования строки в соответствующий код кнопки
    public static int valueOf(String name) {
        return switch (name) {
            case "Mouse Left" -> Input.Buttons.LEFT;
            case "Mouse Right" -> Input.Buttons.RIGHT;
            case "Mouse Middle" -> Input.Buttons.MIDDLE;
            case "Mouse Back" -> Input.Buttons.BACK;
            case "Mouse Forward" -> Input.Buttons.FORWARD;
            default -> throw new IllegalArgumentException("Unknown button: " + name);
        };
    }
}