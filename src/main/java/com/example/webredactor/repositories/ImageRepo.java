package com.example.webredactor.repositories;

import com.example.webredactor.data.Drawable;
import com.example.webredactor.tokens.ResponseToken;

import java.sql.Timestamp;

public interface ImageRepo {

    Timestamp getCurrentTime();

    Drawable getFileById(Long id);

    ResponseToken addFile(Drawable file);
}
