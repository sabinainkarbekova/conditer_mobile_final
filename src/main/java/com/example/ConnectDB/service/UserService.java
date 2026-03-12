
package com.example.ConnectDB.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final Map<Integer, String> users = new HashMap<>();

    public String findUser(int id) {
        logger.debug("Входные данные: id={}", id);
        logger.info("Поиск пользователя с id={}", id);

        String user = users.get(id);
        if (user != null) {
            logger.info("Пользователь найден: {}", user);
        } else {
            logger.warn("Пользователь с id={} не найден", id);
        }
        return user;
    }

    public void createUser(int id, String name) {
        logger.debug("Входные данные: id={}, name={}", id, name);
        logger.info("Создание пользователя с id={}", id);
        users.put(id, name);
        logger.info("Пользователь с id={} успешно создан", id);
    }

    public void deleteUser(int id) {
        logger.debug("Входные данные: id={}", id);
        logger.info("Удаление пользователя с id={}", id);
        String removed = users.remove(id);
        if (removed != null) {
            logger.info("Пользователь '{}' с id={} успешно удален", removed, id);
        } else {
            logger.warn("Попытка удалить несуществующего пользователя с id={}", id);
        }
    }
}