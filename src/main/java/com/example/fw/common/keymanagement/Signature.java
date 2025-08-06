package com.example.fw.common.keymanagement;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Signature {
    private final byte[] value;
    //TODO: 
}
