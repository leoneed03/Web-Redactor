package com.example.webredactor.tokens;

import java.sql.Time;
import java.sql.Timestamp;

public interface ResponseToken {
    Long getUniqueId();

    Timestamp getTimeExpire();

    Timestamp getTimeIntervalToLive();
}
