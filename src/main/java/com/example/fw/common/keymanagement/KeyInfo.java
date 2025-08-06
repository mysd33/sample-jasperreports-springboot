package com.example.fw.common.keymanagement;

import lombok.Builder;
import lombok.Value;

/**
 * 暗号鍵の情報を保持するクラス。
 * 
 */
@Value
@Builder
public class KeyInfo {
    private final String keyId; // キーのID
    private final String state; // キーの状態
}
