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
package net.andylizi.mojang.api.security;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import static java.nio.charset.StandardCharsets.UTF_8;

import net.andylizi.mojang.utils.IOUtils;
import net.andylizi.mojang.exception.MojangException;
import static net.andylizi.mojang.utils.Common.*;
/**
 * 安全相关API.
 * 
 * @author andylizi
 */
public class SecurityAPI {
    private static final String SECURITY_QUESTION_URL = "https://api.mojang.com/user/security/location";
    private static final String SECURITY_QUESTION_FETCH_URL = "https://api.mojang.com/user/security/challenges";
    
    /**
     * AccessToken
     */
    private transient String accessToken;

    /**
     * @param accessToken 有效的AccessToken
     */
    public SecurityAPI(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 更新AccessToken. 
     * 
     * @param accessToken 新AccessToken
     */
    public void updateAccessToken(String accessToken) {
        this.accessToken = Objects.requireNonNull(accessToken);
    }
    
    /**
     * 检测是否需要验证安全问题.
     * 
     * @return 是否需要验证安全问题
     * @throws java.io.IOException 发生 I/O 错误
     */
    public boolean needSecurityQuestions() throws IOException{
        HttpURLConnection conn = Common.createHttpURLConnection(new URL(String.format(SECURITY_QUESTION_URL)));
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization ", String.format(AUTHORIZATION_PROPERTY_FORMAT, accessToken));
        conn.connect();
        return conn.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN;
    }
    
    /**
     * 获取安全问题. 
     * 
     * @return 安全问题
     * @throws java.io.IOException 发生 I/O 错误
     * @throws net.andylizi.mojang.exception.MojangException Mojang服务器返回异常
     */
    public SecurityQuestions fetchSecurityQuestions() throws IOException, RuntimeException, MojangException{
        URL url = new URL(SECURITY_QUESTION_FETCH_URL);
        HttpURLConnection conn = Common.createHttpURLConnection(url);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.setRequestProperty("Authorization ", String.format(AUTHORIZATION_PROPERTY_FORMAT, accessToken));
        conn.connect();
        
        String result = new String(IOUtils.readFully(IOUtils.handleInputStream(conn)), UTF_8);
        Common.throwMojangException(result);
        return new SecurityQuestions(result);
    }
    
    /**
     * 提交安全问题. 
     * 
     * @param questions 填写完成的安全问题
     * @return 是否成功
     * @throws java.io.IOException 发生 I/O 错误
     * @throws java.lang.IllegalArgumentException 安全问题未填写完成
     * @throws net.andylizi.mojang.exception.MojangException 答案错误 / Mojang服务器返回异常
     */
    public boolean submitSecurityQuestions(SecurityQuestions questions) throws IllegalArgumentException, IOException, MojangException{
        if(!questions.isComplete())
            throw new IllegalArgumentException("Questions incompleted");
        byte[] data = questions.toJSON().getBytes(UTF_8);

        HttpURLConnection conn = Common.createHttpURLConnection(new URL(String.format(SECURITY_QUESTION_URL)));
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.setRequestProperty("Authorization ", String.format(AUTHORIZATION_PROPERTY_FORMAT, accessToken));
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        conn.connect();
        conn.getOutputStream().write(data);
        
        if(conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT)
            return true;
        Common.throwMojangException(new String(IOUtils.readFully(IOUtils.handleInputStream(conn)), UTF_8));
        return false;
    }
}
