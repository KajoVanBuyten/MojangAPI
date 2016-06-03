/*
 * Copyright (C) 2016 andylizi.
 * 
 * This library is free software: you can redistribute it and/or  
 * modify it under the terms of the GNU Lesser General Public     
 * License as published by the Free Software Foundation, either   
 * version 3 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU Lesser General Public License for more details.            
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see 
 * <http://www.gnu.org/licenses/>.
 */
package net.andylizi.mojang.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import net.andylizi.mojang.exception.MojangException;
import net.andylizi.mojang.exception.NotSecuredException;
import net.andylizi.mojang.exception.UnauthorizedException;

/**
 * 通用. 
 * @author andylizi
 */
public class Common {
    public static final String AUTHORIZATION_PROPERTY_FORMAT = "Bearer %s";
    
    /**
     * 可替换此字段来实现自定义. 
     */
    public static Common Common = new Common();
    
    /**
     * 抛出Mojang异常. 
     * 
     * @param json Mojang服务器返回的JSON. 如果其不包含error, 此方法将什么也不会做
     * @throws net.andylizi.mojang.exception.UnauthorizedException 异常
     * @throws NotSecuredException 异常
     * @throws MojangException 异常
     */
    public void throwMojangException(String json) throws UnauthorizedException, NotSecuredException, MojangException{
        if(!json.contains("\"error\":\""))
            return;
        
        if(json.contains("\"Current IP not secured\""))
            throw new NotSecuredException();
        else if(json.contains("\"The request requires user authentication\""))
            throw new UnauthorizedException();
        else
            try {
                throw MojangException.ADAPTER.fromJson(json);
            } catch (IOException ex) {
                throw new MojangException("FormatFailed", json);
            }
    }
    
    /**
     * 打开一个HttpURLConnection并对其进行基本设置. 
     * 子类可重新该方法实现自定义Header等.
     * 
     * @param url URL
     * @return URL的HttpURLConnection
     * @throws IOException 发生 I/O 错误
     */
    public HttpURLConnection createHttpURLConnection(URL url) throws IOException{
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("UserAgent", "SkinAPI/2.0");
        conn.setConnectTimeout(6000);
        conn.setReadTimeout(6000);
        conn.setUseCaches(true);
        return conn;
    }
    
    /**
     * 创建一个用于<code>HTTP multipart/form-data</code>传输的Boundary.
     * 子类可重新该方法实现自定义boundary
     * 
     * @return boundary
     */
    public String createBoundary(){
        return "----".concat(Long.toString(new Random().nextLong(), 16));
    }
}
