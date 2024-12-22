package Classes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigManager {

    private static final String CONFIG_FILE_PATH = "src/main/assets/config/config.json";
    private final ObjectMapper objectMapper;
    private ObjectNode config;

    public ConfigManager() {
        objectMapper = new ObjectMapper();
        loadConfig();
    }

    /**
     * Загружает конфигурацию из файла config.json
     */
    private void loadConfig() {
        try {
            File configFile = new File(CONFIG_FILE_PATH);

            if (configFile.exists()) {
                config = (ObjectNode) objectMapper.readTree(configFile);
            } else {
                // Если файл не найден, создаём новый с дефолтными настройками
                config = createDefaultConfig();
                saveConfig();
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
            config = createDefaultConfig(); // Если ошибка, используем настройки по умолчанию
        }
    }

    /**
     * Создаёт дефолтную конфигурацию
     *
     * @return Объект конфигурации
     */
    private ObjectNode createDefaultConfig() {
        ObjectNode defaultConfig = objectMapper.createObjectNode();

        ObjectNode controls = objectMapper.createObjectNode();
        controls.put("moveUp", "W");
        controls.put("moveDown", "S");
        controls.put("moveLeft", "A");
        controls.put("moveRight", "D");
        controls.put("action", "E");
        controls.put("pause", "Escape");
        controls.put("attack", "Space");

        ObjectNode graphics = objectMapper.createObjectNode();
        graphics.put("resolution", "1920x1080");
        graphics.put("fullscreen", true);

        defaultConfig.set("controls", controls);
        defaultConfig.set("graphics", graphics);


        return defaultConfig;
    }

    /**
     * Сохраняет конфигурацию в файл
     */
    public void saveConfig() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(CONFIG_FILE_PATH), config);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения конфигурации: " + e.getMessage());
        }
    }

    /**
     * Получает значение из конфигурации по ключу
     *
     * @param category Категория (например, "controls", "graphics")
     * @param key      Ключ внутри категории (например, "moveUp", "resolution")
     * @return Значение в виде строки
     */
    public String getValue(String category, String key) {
        loadConfig();
        return config.get(category).get(key).asText();
    }

    /**
     * Устанавливает новое значение в конфигурацию
     *
     * @param category Категория (например, "controls", "graphics")
     * @param key      Ключ внутри категории (например, "moveUp", "resolution")
     * @param value    Новое значение
     */
    public void setValue(String category, String key, String value) {
        if (!config.has(category)) {
            // Если категории нет, создаём её как ObjectNode
            config.set(category, objectMapper.createObjectNode());
        }

        // Добавляем или обновляем значение внутри категории
        ((ObjectNode) config.get(category)).put(key, value);

        // Сохраняем изменения в файл
        saveConfig();
    }

    /**
     * Получает boolean значение из конфигурации
     *
     * @param category Категория (например, "graphics")
     * @param key      Ключ внутри категории (например, "fullscreen")
     * @return Значение в виде boolean
     */
    public boolean getBoolean(String category, String key) {
        loadConfig();
        return config.get(category).get(key).asBoolean();
    }

    /**
     * Устанавливает boolean значение в конфигурацию
     *
     * @param category Категория (например, "graphics")
     * @param key      Ключ внутри категории (например, "fullscreen")
     * @param value    Новое boolean значение
     */
    public void setBoolean(String category, String key, boolean value) {
        if (!config.has(category)) {
            // Если категории нет, создаём её как ObjectNode
            config.set(category, objectMapper.createObjectNode());
        }

        // Добавляем или обновляем значение внутри категории
        ((ObjectNode) config.get(category)).put(key, value);

        // Сохраняем изменения в файл
        saveConfig();
    }

    public boolean hasKey(String section, String key) {
        // Проверяем, существует ли раздел
        if (!config.has(section)) {
            return false;
        }
        // Проверяем, существует ли ключ в указанном разделе
        return config.path(section).has(key);
    }

}