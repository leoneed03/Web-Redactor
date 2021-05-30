package com.example.webredactor.repositories;

import com.example.webredactor.data.Drawable;
import com.example.webredactor.tokens.ResponseToken;
import com.example.webredactor.tokens.StringResponseToken;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class ImageRepoHashtable implements ImageRepo {

    Timer timer = new Timer();
    Hashtable<Long, Drawable> imageByToken = new Hashtable<>();

    @Override
    public Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    @Override
    public Drawable getFileById(Long id) {
        return imageByToken.get(id);
    }

    @Override
    public ResponseToken addFile(Drawable file) {
        ResponseToken responseToken;

        synchronized (this) {
            long currentSize = imageByToken.size();
            responseToken = new StringResponseToken(currentSize);
            if (imageByToken.containsKey(responseToken.getUniqueId())) {
                assert false;
            }
            imageByToken.put(responseToken.getUniqueId(), file);
        }
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                imageByToken.remove(responseToken.getUniqueId());
            }
        };

        timer.schedule(tt, responseToken.getTimeIntervalToLive().getTime());
        return responseToken;
    }
}
