package org.codewithNishu.ecom_order_service.exceptions;

import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if(response.status() == 404){
            return new RuntimeException("Product Not Found!");
        } 
        return new RuntimeException("Generic Error: "+ response.status());
    }

}
