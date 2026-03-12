package com.example.ConnectDB.service;

import java.util.List;

public class CustomCakeRequest {
    private Long userId; // ← ДОБАВЬТЕ ЭТО ПОЛЕ
    private Long cakeTypeId;
    private Long layerId;
    private Long creamId;
    private Long fillingId;
    private Long coatingId;
    private Long coatingColorId;
    private Long textId;
    private String comment;
    private List<DecorationRequest> decorations;
    private List<CandleRequest> candles;

    // Вложенные классы для декораций и свечей
    public static class DecorationRequest {
        private Long decorationId;
        private Double posX;
        private Double posY;
        private Double scale;
        private Double rotation;

        // Геттеры и сеттеры
        public Long getDecorationId() { return decorationId; }
        public void setDecorationId(Long decorationId) { this.decorationId = decorationId; }
        public Double getPosX() { return posX; }
        public void setPosX(Double posX) { this.posX = posX; }
        public Double getPosY() { return posY; }
        public void setPosY(Double posY) { this.posY = posY; }
        public Double getScale() { return scale; }
        public void setScale(Double scale) { this.scale = scale; }
        public Double getRotation() { return rotation; }
        public void setRotation(Double rotation) { this.rotation = rotation; }
    }

    public static class CandleRequest {
        private Long candleId;
        private Integer quantity;

        // Геттеры и сеттеры
        public Long getCandleId() { return candleId; }
        public void setCandleId(Long candleId) { this.candleId = candleId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }


    // Геттеры и сеттеры
    public Long getUserId() { return userId; } // ← ДОБАВЬТЕ ГЕТТЕР
    public void setUserId(Long userId) { this.userId = userId; } // ← ДОБАВЬТЕ СЕТТЕР
    public Long getCakeTypeId() { return cakeTypeId; }
    public void setCakeTypeId(Long cakeTypeId) { this.cakeTypeId = cakeTypeId; }
    public Long getLayerId() { return layerId; }
    public void setLayerId(Long layerId) { this.layerId = layerId; }
    public Long getCreamId() { return creamId; }
    public void setCreamId(Long creamId) { this.creamId = creamId; }
    public Long getFillingId() { return fillingId; }
    public void setFillingId(Long fillingId) { this.fillingId = fillingId; }
    public Long getCoatingId() { return coatingId; }
    public void setCoatingId(Long coatingId) { this.coatingId = coatingId; }
    public Long getCoatingColorId() { return coatingColorId; }
    public void setCoatingColorId(Long coatingColorId) { this.coatingColorId = coatingColorId; }
    public Long getTextId() { return textId; }
    public void setTextId(Long textId) { this.textId = textId; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public List<DecorationRequest> getDecorations() { return decorations; }
    public void setDecorations(List<DecorationRequest> decorations) { this.decorations = decorations; }
    public List<CandleRequest> getCandles() { return candles; }
    public void setCandles(List<CandleRequest> candles) { this.candles = candles; }
}