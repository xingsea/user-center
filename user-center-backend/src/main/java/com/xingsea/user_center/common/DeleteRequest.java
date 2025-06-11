package com.xingsea.user_center.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 2437730704158761408L;
    /**
     * id
     */
    private Long id;
    
}
