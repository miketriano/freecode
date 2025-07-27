package com.miketriano.freecode.model;

import java.util.List;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder(toBuilder = true)
public class TestCase {
    private List<String> displayInput;
    private String input;
    private String output;
    private String result;
    
    public Boolean getPassed() {
        return output.equals(result);
    }
}
