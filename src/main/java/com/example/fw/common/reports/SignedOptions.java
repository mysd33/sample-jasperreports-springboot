package com.example.fw.common.reports;

import java.io.File;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class SignedOptions {
    enum SignType {
        Default, // デフォルト
        PAdES // PAdES
    }
    
    /**
     * 署名の種類
     */
    private SignType signType;
    
    /**
     * 可視署名かどうか
     */
    private boolean visible;

    /**
     * キーストアのファイル
     */
    private File keyStoreFile;
    /**
     * パスワード
     */
    private String password;;
}
