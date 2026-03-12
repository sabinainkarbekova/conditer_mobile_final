package com.example.ConnectDB.service;

import com.example.ConnectDB.model.constructorCake.*;
import com.example.ConnectDB.repository.*;
import com.example.ConnectDB.repository.constructorCakeRepository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CakeConstructorService {


    @Autowired
    private UserRepository userRepository; // ← ДОБАВЬТЕ ЭТО

    @Autowired
    private CakeTypeRepository cakeTypeRepository;

    @Autowired
    private CakeLayerRepository cakeLayerRepository;

    @Autowired
    private CakeCreamRepository cakeCreamRepository;

    @Autowired
    private CakeFillingRepository cakeFillingRepository;

    @Autowired
    private CakeCoatingRepository cakeCoatingRepository;

    @Autowired
    private CoatingColorRepository coatingColorRepository;

    @Autowired
    private DecorationRepository decorationRepository;

    @Autowired
    private CandleRepository candleRepository;

    @Autowired
    private CakeTextRepository cakeTextRepository;

    @Autowired
    private CustomCakeRepository customCakeRepository;

    @Autowired
    private CustomCakeDecorationRepository customCakeDecorationRepository;

    @Autowired
    private CustomCakeCandleRepository customCakeCandleRepository;

    // Получение всех доступных компонентов
    public List<CakeType> getAllCakeTypes() {
        return cakeTypeRepository.findAll();
    }

    public List<CakeLayer> getAllCakeLayers() {
        return cakeLayerRepository.findAll();
    }

    public List<CakeCream> getAllCakeCreams() {
        return cakeCreamRepository.findAll();
    }

    public List<CakeFilling> getAllCakeFillings() {
        return cakeFillingRepository.findAll();
    }

    public List<CakeCoating> getAllCakeCoatings() {
        return cakeCoatingRepository.findAll();
    }

    public List<CoatingColor> getAllCoatingColors() {
        return coatingColorRepository.findAll();
    }

    public List<Decoration> getAllDecorations() {
        return decorationRepository.findAll();
    }

    public List<Candle> getAllCandles() {
        return candleRepository.findAll();
    }

    public List<CakeText> getAllCakeTexts() {
        return cakeTextRepository.findAll();
    }

    // Создание кастомного торта
    @Transactional
    public CustomCake createCustomCake(CustomCakeRequest request) {
        CustomCake customCake = new CustomCake();

        // Установка пользователя (ОБЯЗАТЕЛЬНО!)
        customCake.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден")));

        // Установка основных компонентов
        customCake.setCakeType(cakeTypeRepository.findById(request.getCakeTypeId())
                .orElseThrow(() -> new RuntimeException("Тип торта не найден")));

        customCake.setLayer(cakeLayerRepository.findById(request.getLayerId())
                .orElseThrow(() -> new RuntimeException("Корж не найден")));

        customCake.setCream(cakeCreamRepository.findById(request.getCreamId())
                .orElseThrow(() -> new RuntimeException("Крем не найден")));

        customCake.setFilling(cakeFillingRepository.findById(request.getFillingId())
                .orElseThrow(() -> new RuntimeException("Начинка не найдена")));

        customCake.setCoating(cakeCoatingRepository.findById(request.getCoatingId())
                .orElseThrow(() -> new RuntimeException("Покрытие не найдено")));

        customCake.setCoatingColor(coatingColorRepository.findById(request.getCoatingColorId())
                .orElseThrow(() -> new RuntimeException("Цвет покрытия не найден")));

        if (request.getTextId() != null) {
            customCake.setText(cakeTextRepository.findById(request.getTextId())
                    .orElse(null));
        }

        customCake.setComment(request.getComment());

        // Сохраняем основной торт сначала
        CustomCake savedCake = customCakeRepository.save(customCake);

        // Добавляем декорации
        if (request.getDecorations() != null) {
            for (CustomCakeRequest.DecorationRequest decorationRequest : request.getDecorations()) {
                Decoration decoration = decorationRepository.findById(decorationRequest.getDecorationId())
                        .orElseThrow(() -> new RuntimeException("Декорация не найдена"));

                CustomCakeDecoration cakeDecoration = new CustomCakeDecoration();
                cakeDecoration.setCustomCake(savedCake);
                cakeDecoration.setDecoration(decoration);
                cakeDecoration.setPosX(BigDecimal.valueOf(decorationRequest.getPosX()));
                cakeDecoration.setPosY(BigDecimal.valueOf(decorationRequest.getPosY()));
                cakeDecoration.setScale(BigDecimal.valueOf(decorationRequest.getScale()));
                cakeDecoration.setRotation(BigDecimal.valueOf(decorationRequest.getRotation()));

                customCakeDecorationRepository.save(cakeDecoration);
            }
        }

        // Добавляем свечи
        if (request.getCandles() != null) {
            for (CustomCakeRequest.CandleRequest candleRequest : request.getCandles()) {
                Candle candle = candleRepository.findById(candleRequest.getCandleId())
                        .orElseThrow(() -> new RuntimeException("Свеча не найдена"));

                CustomCakeCandle cakeCandle = new CustomCakeCandle();
                cakeCandle.setCustomCake(savedCake);
                cakeCandle.setCandle(candle);
                cakeCandle.setQuantity(candleRequest.getQuantity());

                customCakeCandleRepository.save(cakeCandle);
            }
        }

        // Перезагружаем торт со всеми связями и рассчитываем цену
        CustomCake fullCake = customCakeRepository.findById(savedCake.getId())
                .orElseThrow(() -> new RuntimeException("Торт не найден после сохранения"));

        calculatePrice(fullCake);

        return customCakeRepository.save(fullCake);
    }

    // Расчет общей цены
    private void calculatePrice(CustomCake customCake) {
        BigDecimal total = BigDecimal.ZERO;

        // Базовая цена типа торта
        total = total.add(customCake.getCakeType().getBasePrice());

        // Цены компонентов
        total = total.add(customCake.getLayer().getPrice());
        total = total.add(customCake.getCream().getPrice());
        total = total.add(customCake.getFilling().getPrice());
        total = total.add(customCake.getCoating().getPrice());

        // Цена текста (если есть)
        if (customCake.getText() != null) {
            total = total.add(customCake.getText().getPrice());
        }

        // Цена декораций
        List<CustomCakeDecoration> decorations = customCakeDecorationRepository.findByCustomCakeId(customCake.getId());
        for (CustomCakeDecoration decoration : decorations) {
            total = total.add(decoration.getDecoration().getPrice());
        }

        // Цена свечей
        List<CustomCakeCandle> candles = customCakeCandleRepository.findByCustomCakeId(customCake.getId());
        for (CustomCakeCandle candle : candles) {
            total = total.add(candle.getCandle().getPrice().multiply(BigDecimal.valueOf(candle.getQuantity())));
        }

        customCake.setBasePrice(customCake.getCakeType().getBasePrice());
        customCake.setTotalPrice(total);
    }

    // Получение тортов пользователя
    public List<CustomCake> getUserCustomCakes(Long userId) {
        return customCakeRepository.findByUserId(userId);
    }

    // Получение торта по ID
    public Optional<CustomCake> getCustomCakeById(Long id) {
        return customCakeRepository.findById(id);
    }

    // Удаление торта
    @Transactional
    public void deleteCustomCake(Long id) {
        customCakeRepository.deleteById(id);
    }
}