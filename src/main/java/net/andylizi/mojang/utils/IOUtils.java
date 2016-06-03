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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author andylizi
 */
public abstract class IOUtils {
    
    /**
     * 获得HttpURLConnection中的输入流.
     * 
     * @param conn 一个<b>打开的</b>HttpURLConnection对象
     * @return 输入流
     * @throws IOException 发送 I / O 错误
     */
    public static InputStream handleInputStream(HttpURLConnection conn) throws IOException{
        InputStream in;
        if(conn.getResponseCode() != HttpURLConnection.HTTP_OK)
            in = conn.getErrorStream() == null ? conn.getInputStream() : conn.getErrorStream();
        else
            in = conn.getInputStream();
        if(conn.getContentEncoding() != null && conn.getContentEncoding().toLowerCase().contains("gzip"))
            in = new GZIPInputStream(in);
        else
            in = new BufferedInputStream(in);
        return in;
    }
    
    /**
     * 读取输入流中所有内容并转换为字节数组
     * @param in 输入流
     * @return 输入流中的所有内容
     * @throws IOException 发生 I/O 错误
     */
    public static byte[] readFully(InputStream in) throws IOException{
        Objects.requireNonNull(in);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[64];
            int len = -1;
            while((len = in.read(buf)) != -1)
                baos.write(buf, 0, len);
            return baos.toByteArray();
        }
    }
    
    /**
     * :/
     * @throws AssertionError 意思是让你一边去. 
     */
    private IOUtils() throws AssertionError{ throw new AssertionError(); }
}
