package com.example.webredactor.tokens;

import java.sql.Timestamp;

public class StringResponseToken implements ResponseToken {

    Long id;
    Timestamp expireTime;


    public StringResponseToken(Long id) {
        this.id = id;
        this.expireTime = new Timestamp(System.currentTimeMillis() + getTimeIntervalToLive().getTime());
    }

    @Override
    public Long getUniqueId() {
        return id;
    }

    @Override
    public Timestamp getTimeExpire() {
        return expireTime;
    }

    @Override
    public Timestamp getTimeIntervalToLive() {
        return new Timestamp(1000 * 60 * 5);
    }
}
